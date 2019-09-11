package com.example.josuerey.helloworld.application.vehicularcap;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.domain.movement.Movement;
import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacity;
import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacityRepository;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.infrastructure.network.APIClient;
import com.example.josuerey.helloworld.infrastructure.network.VehicularCapAssignmentResponse;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class VehicularCapacityForm extends AppCompatActivity {

    private VehicularCapacityRepository vehicularCapacityRepository;
    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private APIClient apiClient;
    private TextView viaOfStudyTextView;
    private TextView laneDirectionTextView;
    private CheckBox straightCheckbox;
    private CheckBox turnLeftCheckbox;
    private CheckBox turnRightCheckbox;
    private CheckBox returnCheckbox;
    private String androidDeviceId;
    private List<CheckBox> listOfMovements;
    private final String TAG = this.getClass().getSimpleName();
    private VehicularCapAssignmentResponse assignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicular_capacity_form);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignment = new Gson().fromJson(
                    extras.getString("vehicCapAssignment"), VehicularCapAssignmentResponse.class);
        }

        androidDeviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        viaOfStudyTextView = findViewById(R.id.via_of_study_value);
        viaOfStudyTextView.setText(this.assignment.getBeginAtPlace());
        laneDirectionTextView = findViewById(R.id.lane_direction_value);
        laneDirectionTextView.setText(this.assignment.getPointOfStudy().getDescription());
        straightCheckbox = findViewById(R.id.straightCheckbox);
        turnLeftCheckbox = findViewById(R.id.turnLeftCheckbox);
        turnRightCheckbox = findViewById(R.id.turnRightCheckbox);
        returnCheckbox = findViewById(R.id.returnCheckbox);

        vehicularCapacityRepository = new VehicularCapacityRepository(getApplication());
        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();

        listOfMovements = new LinkedList<>();
        listOfMovements.add(straightCheckbox);
        listOfMovements.add(turnLeftCheckbox);
        listOfMovements.add(turnRightCheckbox);
        listOfMovements.add(returnCheckbox);

        setAssignmentMovements();
        checkForRecordsPendingToBackup();
    }

    private void setAssignmentMovements() {
        for (Movement m : this.assignment.getMovements()) {
            switch (m.getMovement_name()) {
                case "Retorno" :
                    changeCheckBoxColor(returnCheckbox);
                    break;
                case "Izquierda":
                    changeCheckBoxColor(turnLeftCheckbox);
                    break;
                case "Derecha":
                    changeCheckBoxColor(turnRightCheckbox);
                    break;
                case "Derecho":
                    changeCheckBoxColor(straightCheckbox);
                    break;
            }
        }
    }

    private void changeCheckBoxColor(CheckBox movementCheckBox) {
        movementCheckBox.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        movementCheckBox.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void checkForRecordsPendingToBackup() {

        VehicularCapacity[] recordsPendingToBackup =
                vehicularCapacityRepository.findRecordsPendingToBackup();
        if (recordsPendingToBackup.length > 0) {
            Log.d(TAG, "Retrying to backup " + recordsPendingToBackup.length + " records");
            apiClient.postVehicularCapMeta(Arrays.asList(recordsPendingToBackup), vehicularCapacityRepository);
        } else {
            Log.d(TAG, "There are no metadata pending to backup");
        }

        List<VehicularCapacityRecord> vehicularRecordsPendingToBackup =
                vehicularCapacityRecordRepository.findRecordsPendingToBackUp();
        if (!vehicularRecordsPendingToBackup.isEmpty()) {
            Log.d(TAG, "Retrying to backup " + vehicularRecordsPendingToBackup.size() + " records");
            apiClient.postVehicularCapRecord(vehicularRecordsPendingToBackup, vehicularCapacityRecordRepository);
        } else {
            Log.d(TAG, "There are no records pending to backup");
        }
    }

    public void onClick(View view) {
        VehicularCapacity vehicularCapacity = backup();
        Intent studyIntent;
        if (this.assignment.getMovements().size() > 2)
            studyIntent = new Intent(VehicularCapacityForm.this, VehicularCapacityExtendedActivity.class);
        else
            studyIntent = new Intent(VehicularCapacityForm.this, VehicularCapacityActivity.class);

        studyIntent.putExtra("visualOccMetadata", new Gson().toJson(vehicularCapacity));
        studyIntent.putExtra("vehicularCapAssignment", new Gson().toJson(assignment));
        VehicularCapacityForm.this.startActivity(studyIntent);
    }

    private VehicularCapacity backup() {

        VehicularCapacity vehicularCapacity = vehicularCapacityRepository.save(this.assignment);
        Log.d(TAG, String.format("%s %s", "Successfully persisted: ", vehicularCapacity.toString()));
        apiClient.postVehicularCapMeta(Lists.newArrayList(vehicularCapacity), vehicularCapacityRepository);

        return vehicularCapacity;
    }
}
