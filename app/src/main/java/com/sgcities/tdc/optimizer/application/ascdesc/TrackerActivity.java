package com.sgcities.tdc.optimizer.application.ascdesc;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sgcities.tdc.optimizer.application.HomeActivity;
import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.shared.TrackableBaseActivity;
import com.sgcities.tdc.optimizer.domain.busstop.BusStop;
import com.sgcities.tdc.optimizer.domain.busstop.BusStopRepository;
import com.sgcities.tdc.optimizer.domain.gpslocation.GPSLocation;
import com.sgcities.tdc.optimizer.domain.gpslocation.GPSLocationRepository;
import com.sgcities.tdc.optimizer.domain.gpslocation.GPSLocationViewModel;
import com.sgcities.tdc.optimizer.domain.metadata.Metadata;
import com.sgcities.tdc.optimizer.infrastructure.network.APIClient;
import com.sgcities.tdc.optimizer.infrastructure.network.RemoteStorage;
import com.sgcities.tdc.optimizer.utilities.ExportData;
import com.google.gson.Gson;
import com.travijuu.numberpicker.library.NumberPicker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

import lombok.Getter;

@Getter
public class TrackerActivity extends TrackableBaseActivity
        implements RemoteStorage<BusStop, BusStopRepository> {

    private Application appContext;
    private String endpointUrl;
    private String postParamName;

    private String TAG = this.getClass().getSimpleName();
    private TextView latLongTextView;
    private TextView directionsTextView;
    private TextView totalPassengersTextView;
    private Button saveButton;
    private Button beginStopButton;
    private RadioGroup rgStopType;
    private RadioButton rbStopType;
    private NumberPicker numberPickerUp;
    private NumberPicker numberPickerDown;
    private GPSLocationViewModel gpsLocationViewModel;
    private List<GPSLocation> gpsLocationList;
    private Date beginStopInstant;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private GPSLocationRepository gpsLocationRepository;
    private BusStopRepository repository;
    private APIClient apiClient;
    private Metadata currentMetadata;
    private int totalNumberOfPassengers;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(getApplicationContext(), "No disponible",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.finishRoute:
                apiClient.postGpsLocationInBatch(gpsLocationList, gpsLocationRepository);
                Intent myIntent = new Intent(TrackerActivity.this, HomeActivity.class);
                TrackerActivity.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tracker_activity);

        apiClient = APIClient.builder().app(getApplication()).build();
        Bundle extras = getIntent().getExtras();
        appContext = getApplication();
        endpointUrl = "/app/api/persist/routeBusStop";
        postParamName = "busStopData";

        if (extras != null) {
            currentMetadata = new Gson().fromJson(
                    extras.getString(TrackerFormActivity.METADATA_PROPERTY), Metadata.class);
            totalNumberOfPassengers = currentMetadata.getInitialPassengers();
        }

        // Initialize repositories
        gpsLocationRepository = new GPSLocationRepository(getApplication());
        repository = new BusStopRepository(getApplication());

        //Bind layout components
        latLongTextView = findViewById(R.id.latLong);
        directionsTextView = findViewById(R.id.directions);
        totalPassengersTextView = findViewById(R.id.totalPassengers);
        saveButton = findViewById(R.id.btnSave);
        beginStopButton = findViewById(R.id.btnStopBegin);
        rgStopType = findViewById(R.id.rgStopType);
        rbStopType = findViewById(rgStopType.getCheckedRadioButtonId());
        rgStopType.setOnCheckedChangeListener((RadioGroup group, int checkedId) ->
            rbStopType = findViewById(rgStopType.getCheckedRadioButtonId()));

        totalPassengersTextView.setText(" Total pasajeros: " + totalNumberOfPassengers + " ");

        numberPickerUp = findViewById(R.id.number_pickerUP);
        numberPickerDown = findViewById(R.id.number_pickerDown);

        gpsLocationViewModel = ViewModelProviders.of(this).get(GPSLocationViewModel.class);

        gpsLocationList = new LinkedList<>();
        gpsLocationViewModel.findGPSLocationsByMetadataId(
                currentMetadata.getAssignmentId()).observe(this, new Observer<List<GPSLocation>>() {
            @Override
            public void onChanged(@Nullable List<GPSLocation> gpsLocations) {
                gpsLocationList = gpsLocations;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                persistBusStop();
                disableControls();
                beginStopButton.setEnabled(true);
            }
        });

        beginStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                beginStopInstant = Calendar.getInstance().getTime();
                enableControls();
                beginStopButton.setEnabled(false);
            }
        });

        disableControls();
        requestPermissions();
    }

    private void disableControls(){
        numberPickerDown.setEnabled(false);
        numberPickerUp.setEnabled(false);
        rgStopType.setEnabled(false);
        saveButton.setEnabled(false);

        for(int i = 0; i < rgStopType.getChildCount(); i++){
            (rgStopType.getChildAt(i)).setEnabled(false);
        }
    }

    private void enableControls(){
        numberPickerDown.setEnabled(true);
        numberPickerUp.setEnabled(true);
        rgStopType.setEnabled(true);
        saveButton.setEnabled(true);

        for(int i = 0; i < rgStopType.getChildCount(); i++){
            (rgStopType.getChildAt(i)).setEnabled(true);
        }
    }

    public void updateLocationOnView(Location loc) {

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    directionsTextView.setText(list.get(0).getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location){
        super.onLocationChanged(location);

        String Text = String.format("Lat = %1$,.10f \n Long = %1$,.10f",
                currentLocation.getLat(), currentLocation.getLon());
        latLongTextView.setText(Text);
        updateLocationOnView(location);

        persistGPSFix();
    }

    private void persistGPSFix(){

        currentLocation.setAssignmentId(currentMetadata.getAssignmentId());
        long gpsLocationId = gpsLocationRepository.insert(currentLocation);
        currentLocation.setId((int)gpsLocationId);

        ExportData.createFile(String.format("%s-%s-PuntosGPS-%d.txt", currentMetadata.getRoute(),
                currentMetadata.getEconomicNumber(), currentMetadata.getAssignmentId()),
                currentLocation.toString());
    }

    private void persistBusStop() {

        totalNumberOfPassengers += numberPickerUp.getValue();
        totalNumberOfPassengers -= numberPickerDown.getValue();
        String stopType = rbStopType.getText().toString();
        boolean isOfficial = stopType.equals("Parada");

        BusStop newBusStop = BusStop.builder()
                .stopType(stopType)
                .assignmentId(currentMetadata.getAssignmentId())
                .deviceId(deviceId)
                .isOfficial(isOfficial)
                .lat(currentLocation != null ? currentLocation.getLat() : 0.0)
                .lon(currentLocation != null ? currentLocation.getLon(): 0.0)
                .passengersDown(numberPickerDown.getValue())
                .passengersUp(numberPickerUp.getValue())
                .totalPassengers(totalNumberOfPassengers)
                .stopBegin(DATE_FORMAT.format(beginStopInstant))
                .stopEnd(DATE_FORMAT.format(new Date()))
                .backedUpRemotely(0)
                .build();

        int id = (int)repository.insert(newBusStop);
        newBusStop.setId(id);
        postItemsInBatch(Collections.singletonList(newBusStop));

        // Create BusStop file
        ExportData.createFile(String.format("%s-%s-Paradas-%d.txt",currentMetadata.getRoute(),
                currentMetadata.getEconomicNumber(), currentMetadata.getAssignmentId()),
                newBusStop.toString());

        Toast.makeText(getApplicationContext(), "Guardado",Toast.LENGTH_SHORT).show();
        clearAfterSave();
    }

    private void clearAfterSave() {

        numberPickerUp.setValue(0);
        numberPickerDown.setValue(0);
        rgStopType.check(R.id.radiobtnOficialStop);
        totalPassengersTextView.setText(" Total pasajeros: " + totalNumberOfPassengers + " ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestPermissions())
            locationStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestPermissions())
            locationStart();
    }
}
