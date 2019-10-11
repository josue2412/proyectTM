package com.example.josuerey.helloworld.application.ascdesc;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.application.shared.BaseActivity;
import com.example.josuerey.helloworld.domain.busstop.BusStop;
import com.example.josuerey.helloworld.domain.busstop.BusStopRepository;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocationRepository;
import com.example.josuerey.helloworld.domain.metadata.Metadata;
import com.example.josuerey.helloworld.domain.metadata.MetadataRepository;
import com.example.josuerey.helloworld.infrastructure.network.APIClient;
import com.example.josuerey.helloworld.infrastructure.network.AscDescAssignmentResponse;
import com.example.josuerey.helloworld.utilities.ExportData;
import com.google.gson.Gson;

import java.util.Arrays;

public class TrackerFormActivity extends BaseActivity {

    public final static String METADATA_PROPERTY = "metadata";
    private EditText routeEditText;
    private EditText viaEditText;
    private EditText ecoNumberEditText;
    private EditText durationEditText;
    private EditText beginAtDateEditText;
    private EditText beginAtPlaceEditText;
    private EditText initialPassengers;
    private MetadataRepository metadataRepository;
    private GPSLocationRepository gpsLocationRepository;
    private BusStopRepository busStopRepository;
    private int metadataId;
    private final String TAG = this.getClass().getSimpleName();
    private APIClient apiClient;
    private String androidDeviceId;
    private AscDescAssignmentResponse assignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_form_activity);

        androidDeviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignment = new Gson().fromJson(
                    extras.getString("ascDescAssignment"), AscDescAssignmentResponse.class);
        }

        Log.d(TAG, "Unique id: " + androidDeviceId);

        routeEditText = findViewById(R.id.route_edit_text);
        viaEditText = findViewById(R.id.via_edit_text);
        ecoNumberEditText = findViewById(R.id.econNum_edit_text);
        beginAtDateEditText = findViewById(R.id.begin_at_date_edit_text);
        beginAtPlaceEditText = findViewById(R.id.begin_at_place_edit_text);
        durationEditText = findViewById(R.id.duration_edit_text);
        initialPassengers = findViewById(R.id.initial_passengers_edit_text);

        metadataRepository = new MetadataRepository(getApplication());
        gpsLocationRepository = new GPSLocationRepository(getApplication());
        busStopRepository = new BusStopRepository(getApplication());

        apiClient = APIClient.builder().app(getApplication()).build();

        checkForDataPendingToBackUp();
    }

    private void setFieldsValues() {

        routeEditText.setText(this.assignment.getRoute());
        viaEditText.setText(this.assignment.getVia());
        ecoNumberEditText.setText(this.assignment.getEconomicNumber());
        initialPassengers.setText(String.valueOf(this.assignment.getInitialPassengers()));
        beginAtDateEditText.setText(this.assignment.getBeginAtDate());
        beginAtPlaceEditText.setText(this.assignment.getBeginAtPlace());
        durationEditText.setText(String.valueOf(this.assignment.getDurationInHours()));

        if (this.assignment.getIsEditable() == 0) {
            routeEditText.setEnabled(false);
            viaEditText.setEnabled(false);
            ecoNumberEditText.setEnabled(false);
            initialPassengers.setEnabled(false);
        }
    }

    private void checkForDataPendingToBackUp() {

        Metadata[] metadataRecords = metadataRepository.findMetadataByBackedUpRemotely(0);
        Log.d(TAG, "Metadata records pending to backup: " + metadataRecords.length);
        if (metadataRecords.length > 0) {
            apiClient.postMetadataInBatch(Arrays.asList(metadataRecords), metadataRepository);
        }

        BusStop[] busStopsRecords = busStopRepository.findBusStopByBackedUpRemotely(0);
        Log.d(TAG, "BusStop records pending to backup: " + busStopsRecords.length);
        if (busStopsRecords.length > 0) {
            apiClient.postBusStopInBatch(Arrays.asList(busStopsRecords), busStopRepository);
        }

        GPSLocation[] gpsLocationsRecords = gpsLocationRepository.findGPSLocationByBackedUpRemotely(0);
        Log.d(TAG, "GPSLocation records pending to backup: " + gpsLocationsRecords.length);
        if (gpsLocationsRecords.length > 0) {
            apiClient.postGpsLocationInBatch(Arrays.asList(gpsLocationsRecords), gpsLocationRepository);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                    if (fieldsValidateSuccess()) {
                        Metadata metadata = getProperMetadata();
                        apiClient.postMetadataInBatch(Arrays.asList(metadata), metadataRepository);

                        Intent myIntent = new Intent(TrackerFormActivity.this, TrackerActivity.class);
                        myIntent.putExtra(METADATA_PROPERTY, new Gson().toJson(metadata));
                        TrackerFormActivity.this.startActivity(myIntent);
                        finish();
                    }
                break;
        }
    }

    private boolean fieldsValidateSuccess() {
        return evalEditText(viaEditText) && evalEditText(routeEditText)
                && evalEditText(ecoNumberEditText);
    }

    private boolean evalEditText(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("Campo requerido");
            editText.requestFocus();
            return false;
        }
        return true;
    }

    /**
     *
     * @return proper Metadata
     */
    private Metadata getProperMetadata() {

        Log.d(TAG, "Searching for Metadata with assignmentId: " + this.assignment.getId());
        Metadata existingMetadata =
                metadataRepository.findMetadataByAssignmentId(this.assignment.getId());

        if (existingMetadata != null) {

            // update local assignment with data from incoming assignment
            if (this.assignment.getIsEditable() == 1) {
                Log.d(TAG, "Updating Metadata with assignmentId: " + existingMetadata.getAssignmentId());
                return metadataRepository.updateMetadataFromAssignment(
                        this.assignment, existingMetadata.getId());
            }
            // return existing assignment
            Log.d(TAG, "Using Metadata with assignmentId: " + existingMetadata.getAssignmentId());
            return existingMetadata;
        } else {
            // return brand new assignment
            Log.d(TAG, "Creating new Metadata with assignmentId: " + this.assignment.getId());
            return saveMetadata();
        }
    }

    /**
     *
     * @return id autogenerated for the persisted row.
     */
    private Metadata saveMetadata() {

        int defaultInitialPassengers = 0;
        if (!initialPassengers.getText().toString().isEmpty()) {
            defaultInitialPassengers = Integer.valueOf(initialPassengers.getText().toString());
        }

        Metadata metadata = Metadata.builder()
                .durationInHours(Integer.valueOf(durationEditText.getText().toString()))
                .assignmentId(this.assignment.getId())
                .beginAtDate(beginAtDateEditText.getText().toString())
                .beginAtPlace(beginAtPlaceEditText.getText().toString())
                .economicNumber(ecoNumberEditText.getText().toString())
                .via(viaEditText.getText().toString())
                .deviceId(androidDeviceId)
                .initialPassengers(defaultInitialPassengers)
                .backedUpRemotely(0)
                .route(routeEditText.getText().toString()).build();

        metadataId = (int) metadataRepository.insert(metadata);
        metadata.setId(metadataId);

        Log.d(TAG, "New metadata created: " + metadata.toString());
        // Create Metadata file
        ExportData.createFile(String.format("%s-%s-Recorrido-%d.txt",
                routeEditText.getText().toString(),
                ecoNumberEditText.getText().toString(),
                metadataId), metadata.toString());

        return metadata;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFieldsValues();
    }
}
