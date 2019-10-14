package com.sgcities.tdc.optimizer.application.visualocc;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import lombok.Getter;

@Getter
public class VisualOccupationActivity extends TrackableBaseActivity
        implements RemoteStorage<BusOccupation, BusOccupationRepository> {

    private Application appContext;
    private String endpointUrl;
    private String postParamName;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String TAG = this.getClass().getSimpleName();
    private List<String> busRoutes;
    private HashMap<String, Integer> busRoutesMap;
    private VisualOccupationMetadata visualOccupationMetadata;
    private VisualOccupationAssignmentResponse assignment;

    private Spinner spinnerRoute;
    private Spinner spinnerRoute2;
    private Spinner spinnerRoute3;

    private Spinner spinnerOccupationLevel;
    private Spinner spinnerOccupationLevel2;
    private Spinner spinnerOccupationLevel3;

    private Spinner spinnerVehicleType;
    private Spinner spinnerVehicleType2;
    private Spinner spinnerVehicleType3;

    private EditText econNumEditText;
    private EditText econNumEditText2;
    private EditText econNumEditText3;

    private BusOccupationRepository repository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.visual_occupation_activity);

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

        busRoutes = new LinkedList<>();
        busRoutesMap = new HashMap<>();
        busRoutes.add("Ruta");

        for (RouteBusPayload availableBusRoute : assignment.getRoutes()) {
            String routeName = String.format("%s %s", availableBusRoute.getRoute(), availableBusRoute.getVia());
            busRoutes.add(routeName);
            busRoutesMap.put(routeName, availableBusRoute.getId());
        }
        Log.d(TAG, assignment.getRoutes().toString());

        // Populate route spinner with routes associated to point of study
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, busRoutes.toArray(new String[busRoutes.size()]));
        spinnerRoute = findViewById(R.id.routeSpinner);
        spinnerRoute2 = findViewById(R.id.routeSpinner2);
        spinnerRoute3 = findViewById(R.id.routeSpinner3);

        spinnerRoute.setAdapter(adapter);
        spinnerRoute2.setAdapter(adapter);
        spinnerRoute3.setAdapter(adapter);

        spinnerOccupationLevel = findViewById(R.id.occupation_level_spinner);
        spinnerOccupationLevel2 = findViewById(R.id.occupation_level_spinner2);
        spinnerOccupationLevel3 = findViewById(R.id.occupation_level_spinner3);

        spinnerVehicleType = findViewById(R.id.vehicleTypeSpinner);
        spinnerVehicleType2 = findViewById(R.id.vehicleTypeSpinner2);
        spinnerVehicleType3 = findViewById(R.id.vehicleTypeSpinner3);

        econNumEditText = findViewById(R.id.econNum_edit_text);
        econNumEditText2 = findViewById(R.id.econNum_edit_text2);
        econNumEditText3 = findViewById(R.id.econNum_edit_text3);

        requestPermissions();
    }

    private boolean validateSpinners(int positionSpinnerRoute,
                                  int positionSpinnerOccLevel,
                                  int positionSpinnerVehiType){
        if (positionSpinnerRoute < 1) {
            Toast.makeText(getApplicationContext(), "Ingresa una ruta",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (positionSpinnerOccLevel < 1) {
            Toast.makeText(getApplicationContext(), "Ingresa un nivel de ocupación",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (positionSpinnerVehiType < 1) {
            Toast.makeText(getApplicationContext(), "Ingresa un tipo de vehiculo",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onSave(View v) {
        BusOccupation.BusOccupationBuilder busOccupationBuilder = BusOccupation.builder();
        int formNumberSaved = 0;

        switch (v.getId()) {
            case R.id.save_button:
                if (validateSpinners(spinnerRoute.getSelectedItemPosition(),
                        spinnerOccupationLevel.getSelectedItemPosition(),
                        spinnerVehicleType.getSelectedItemPosition())) {

                    busOccupationBuilder.routeId(busRoutesMap.get(spinnerRoute.getSelectedItem().toString()))
                            .visOccAssignmentId(assignment.getId())
                            .occupationLevel(spinnerOccupationLevel.getSelectedItem().toString())
                            .economicNumber(econNumEditText.getText().toString())
                            .busType(spinnerVehicleType.getSelectedItem().toString());
                    formNumberSaved = 1;
                }
                break;
            case R.id.save_button2:
                if (validateSpinners(spinnerRoute2.getSelectedItemPosition(),
                        spinnerOccupationLevel2.getSelectedItemPosition(),
                        spinnerVehicleType2.getSelectedItemPosition())) {
                    busOccupationBuilder.routeId(busRoutesMap.get(spinnerRoute2.getSelectedItem().toString()))
                            .occupationLevel(spinnerOccupationLevel2.getSelectedItem().toString())
                            .visOccAssignmentId(assignment.getId())
                            .economicNumber(econNumEditText2.getText().toString())
                            .busType(spinnerVehicleType2.getSelectedItem().toString());
                    formNumberSaved = 2;
                }
                break;
            case R.id.save_button3:
                if (validateSpinners(spinnerRoute3.getSelectedItemPosition(),
                        spinnerOccupationLevel3.getSelectedItemPosition(),
                        spinnerVehicleType3.getSelectedItemPosition())) {
                    busOccupationBuilder.routeId(busRoutesMap.get(spinnerRoute3.getSelectedItem().toString()))
                            .occupationLevel(spinnerOccupationLevel3.getSelectedItem().toString())
                            .visOccAssignmentId(assignment.getId())
                            .economicNumber(econNumEditText3.getText().toString())
                            .busType(spinnerVehicleType3.getSelectedItem().toString());
                    formNumberSaved = 3;
                }
                break;
        }

        if (formNumberSaved > 0) {
            busOccupationBuilder
                    .backedUpRemotely(0)
                    .timeStamp(DATE_FORMAT.format(Calendar.getInstance().getTime()));

            busOccupationBuilder.lat(currentLocation != null ? currentLocation.getLat() : 0.0);
            busOccupationBuilder.lon(currentLocation != null ? currentLocation.getLon(): 0.0);
            BusOccupation busOccupation = busOccupationBuilder.build();
            backUpRecord(busOccupation);
            cleanForm(formNumberSaved);
        }
    }

    private void backUpRecord(BusOccupation busOccupationRecord) {
        long busOccupationId = repository.save(busOccupationRecord);
        busOccupationRecord.setId((int) busOccupationId);

        Log.d(TAG, "Saving new busOccupation with id: " + busOccupationId);

        ExportData.createFile(String.format("Ocupacion-visual-%s-%d.txt",
                visualOccupationMetadata.getViaOfStudy(),
                visualOccupationMetadata.getAssignmentId()),
                busOccupationRecord.toString());

        postItemsInBatch(Collections.singletonList(busOccupationRecord));
    }

    private void cleanForm(int formPosition) {
        switch (formPosition) {
            case 1:
                econNumEditText.setText("");
                spinnerRoute.setSelection(0);
                spinnerOccupationLevel.setSelection(0);
                spinnerVehicleType.setSelection(0);
                break;
            case 2:
                econNumEditText2.setText("");
                spinnerRoute2.setSelection(0);
                spinnerOccupationLevel2.setSelection(0);
                spinnerVehicleType2.setSelection(0);
                break;
            case 3:
                econNumEditText3.setText("");
                spinnerRoute3.setSelection(0);
                spinnerOccupationLevel3.setSelection(0);
                spinnerVehicleType3.setSelection(0);
                break;
        }
        Toast.makeText(getApplicationContext(), "Registro guardado",Toast.LENGTH_SHORT).show();
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