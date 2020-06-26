package com.sgcities.tdc.optimizer.application.visualocc;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.shared.TrackableBaseActivity;
import com.sgcities.tdc.optimizer.domain.busoccupation.BusOccupation;
import com.sgcities.tdc.optimizer.domain.busoccupation.BusOccupationRepository;
import com.sgcities.tdc.optimizer.domain.busroute.RouteBusPayload;
import com.sgcities.tdc.optimizer.domain.visualoccupation.VisualOccupationMetadata;
import com.sgcities.tdc.optimizer.infrastructure.network.RemoteStorage;
import com.sgcities.tdc.optimizer.infrastructure.network.VisualOccupationAssignmentResponse;
import com.sgcities.tdc.optimizer.utilities.ExportData;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

@Getter
public class VisualOccupationActivity extends TrackableBaseActivity
        implements RemoteStorage<BusOccupation, BusOccupationRepository> {

    private Application appContext;
    private String endpointUrl;
    private String postParamName;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String TAG = this.getClass().getSimpleName();
    private VisualOccupationMetadata visualOccupationMetadata;
    private VisualOccupationAssignmentResponse assignment;

    private BusOccupationRepository repository;
    private LinearLayout visualOccupationFormLayout;
    private AtomicInteger formIncrementallyId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.visual_occupation_activity);

        visualOccupationFormLayout = findViewById(R.id.visual_occupation_layout_holder);
        formIncrementallyId = new AtomicInteger();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            visualOccupationMetadata = new Gson().fromJson(
                    extras.getString("visualOccMetadata"), VisualOccupationMetadata.class);
            assignment = new Gson().fromJson(
                    extras.getString("visualOccAssignment"), VisualOccupationAssignmentResponse.class);
        }

        appContext = getApplication();
        endpointUrl = "/app/api/persist/busOccRecord";
        postParamName = "busOccData";
        repository = new BusOccupationRepository(getApplication());

        inflateForm();
    }

    /**
     * Builds and configure a new instance of visual_occupation_form_layout and add it to
     * visualOccupationFormLayout.
     */
    private void inflateForm() {
        LayoutInflater viewInflater = (
                LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View visualOccFormLayout =
                viewInflater.inflate(R.layout.visual_occupation_form_layout, null);

        final int currentId = formIncrementallyId.incrementAndGet();
        visualOccFormLayout.setId(currentId);
        Spinner routeSpinner = visualOccFormLayout.findViewById(R.id.route_spinner);
        ArrayAdapter routeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item);
        routeArrayAdapter.add("Ruta");
        routeArrayAdapter.addAll( assignment.getRoutes());
        routeSpinner.setAdapter(routeArrayAdapter);
        Button currentSaveButton = visualOccFormLayout.findViewById(R.id.save_button);
        currentSaveButton.setTag(currentId);

        visualOccupationFormLayout.addView(visualOccFormLayout, visualOccupationFormLayout.getChildCount());
    }

    /**
     * By clicking this button a new form is inflated and added to the current vissual occupation
     * linear layout.
     *
     * @param view
     */
    public void addNewForm(View view) {
        inflateForm();
    }

    private boolean validateSpinners(int positionSpinnerRoute,
                                  int positionSpinnerOccLevel,
                                  int positionSpinnerVehiType){
        if (positionSpinnerRoute < 1) {
            Toast.makeText(getApplicationContext(), R.string.input_validation_route,Toast.LENGTH_SHORT).show();
            return false;
        }
        if (positionSpinnerOccLevel < 1) {
            Toast.makeText(getApplicationContext(), R.string.input_validation_level,Toast.LENGTH_SHORT).show();
            return false;
        }
        if (positionSpinnerVehiType < 1) {
            Toast.makeText(getApplicationContext(), R.string.input_validation_type,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Builds a {@linkplain BusOccupation} based on current form information. Once the validation of
     * the spinner is success.
     * @param v
     */
    public void onSave(View v) {

        int viewTag = (int) v.getTag();
        View visualOccFormView  = findViewById(viewTag);

        Log.d(TAG, String.format("Processing visual occupation form id: %d ...", viewTag));
        Spinner route = visualOccFormView.findViewById(R.id.route_spinner);
        Spinner occLevel = visualOccFormView.findViewById(R.id.occupation_level_spinner);
        Spinner vehicleType = visualOccFormView.findViewById(R.id.vehicle_type_spinner);
        EditText economicNumber = visualOccFormView.findViewById(R.id.econ_num_edit_text);

        if (validateSpinners(route.getSelectedItemPosition(), occLevel.getSelectedItemPosition(),
                vehicleType.getSelectedItemPosition())) {

            RouteBusPayload selectedRoute = (RouteBusPayload) route.getSelectedItem();
            BusOccupation.BusOccupationBuilder busOccupationBuilder = BusOccupation.builder();
            busOccupationBuilder.visOccAssignmentId(this.assignment.getId())
                    .routeId(selectedRoute.getId())
                    .economicNumber(economicNumber.getText().toString())
                    .occupationLevel(occLevel.getSelectedItem().toString())
                    .busType(vehicleType.getSelectedItem().toString());

            busOccupationBuilder
                    .backedUpRemotely(0)
                    .timeStamp(DATE_FORMAT.format(Calendar.getInstance().getTime()));

            busOccupationBuilder.lat(currentLocation != null ? currentLocation.getLat() : 0.0);
            busOccupationBuilder.lon(currentLocation != null ? currentLocation.getLon(): 0.0);
            BusOccupation busOccupation = busOccupationBuilder.build();
            backUpRecord(busOccupation);
            cleanForm(viewTag);
        }
    }


    /**
     *
     * @param busOccupationRecord {@linkplain BusOccupation}
     */
    private void backUpRecord(BusOccupation busOccupationRecord) {
        long busOccupationId = repository.save(busOccupationRecord);
        busOccupationRecord.setId((int) busOccupationId);

        Log.d(TAG, "BusOccupation internally saved with id: " + busOccupationId);
        Toast.makeText(getApplicationContext(), R.string.record_saved,Toast.LENGTH_SHORT).show();

        ExportData.createFile(String.format("Ocupacion-visual-%s-%d.txt",
                visualOccupationMetadata.getViaOfStudy(),
                visualOccupationMetadata.getAssignmentId()),
                busOccupationRecord.toString());

        postItemsInBatch(Collections.singletonList(busOccupationRecord));
    }

    /**
     * Reset form values to default values
     * @param viewTag
     */
    private void cleanForm(int viewTag) {

        View visualOccFormView  = findViewById(viewTag);
        Spinner route = visualOccFormView.findViewById(R.id.route_spinner);
        route.setSelection(0);
        Spinner occLevel = visualOccFormView.findViewById(R.id.occupation_level_spinner);
        occLevel.setSelection(0);
        Spinner vehicleType = visualOccFormView.findViewById(R.id.vehicle_type_spinner);
        vehicleType.setSelection(0);
        EditText economicNumber = visualOccFormView.findViewById(R.id.econ_num_edit_text);
        economicNumber.setText("");
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