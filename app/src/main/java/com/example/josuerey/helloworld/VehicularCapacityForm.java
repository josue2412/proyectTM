package com.example.josuerey.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacity;
import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacityRepository;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudyRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class VehicularCapacityForm extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private VehicularCapacityRepository vehicularCapacityRepository;
    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private ViaOfStudyRepository viaOfStudyRepository;
    private APIClient apiClient;
    private Spinner viaOfStudySpinner;
    private Spinner wayDirectionSpinner;
    private CheckBox straightCheckbox;
    private CheckBox turnLeftCheckbox;
    private CheckBox turnRightCheckbox;
    private CheckBox returnCheckbox;
    private String android_device_id;
    private List<CheckBox> listOfMovements;
    private final String TAG = this.getClass().getSimpleName();
    private String composedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vehicular_capacity_form);

        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        viaOfStudySpinner = (Spinner) findViewById(R.id.spinnerStudyVia);
        wayDirectionSpinner = (Spinner) findViewById(R.id.spinnerWayDirection);
        straightCheckbox = (CheckBox) findViewById(R.id.straightCheckbox);
        turnLeftCheckbox = (CheckBox) findViewById(R.id.turnLeftCheckbox);
        turnRightCheckbox = (CheckBox) findViewById(R.id.turnRightCheckbox);
        returnCheckbox = (CheckBox) findViewById(R.id.returnCheckbox);

        vehicularCapacityRepository = new VehicularCapacityRepository(getApplication());
        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();

        listOfMovements = new LinkedList<>();
        listOfMovements.add(straightCheckbox);
        listOfMovements.add(turnLeftCheckbox);
        listOfMovements.add(turnRightCheckbox);
        listOfMovements.add(returnCheckbox);

        setCheckBoxListeners();
        checkForRecordsPendingToBackup();

        viaOfStudyRepository = new ViaOfStudyRepository(getApplication());
        ViaOfStudy[] existingVias = viaOfStudyRepository.findAll();
        List<String> routes = new ArrayList<>();

        Log.d(TAG, "Existing vias: " + existingVias.length);

        //Added empty option
        routes.add("");
        for (ViaOfStudy via : existingVias) {
            routes.add(via.getVia());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, routes.toArray(new String[routes.size()]));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viaOfStudySpinner.setAdapter(adapter);
    }

    private void setCheckBoxListeners() {
        straightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (straightCheckbox.isChecked()) {
                    straightCheckbox.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    straightCheckbox.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    straightCheckbox.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                    straightCheckbox.setTextColor(getResources().getColor(R.color.colorBackground));
                }
            }
        });
        turnLeftCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (turnLeftCheckbox.isChecked()) {
                    turnLeftCheckbox.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    turnLeftCheckbox.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    turnLeftCheckbox.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                    turnLeftCheckbox.setTextColor(getResources().getColor(R.color.colorBackground));
                }
            }
        });
        turnRightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (turnRightCheckbox.isChecked()) {
                    turnRightCheckbox.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    turnRightCheckbox.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    turnRightCheckbox.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                    turnRightCheckbox.setTextColor(getResources().getColor(R.color.colorBackground));
                }
            }
        });
        returnCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (returnCheckbox.isChecked()) {
                    returnCheckbox.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    returnCheckbox.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    returnCheckbox.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                    returnCheckbox.setTextColor(getResources().getColor(R.color.colorBackground));
                }
            }
        });
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

        VehicularCapacityRecord[] vehicularRecordsPendingToBackup =
                vehicularCapacityRecordRepository.findRecordsPendingToBackup();
        if (vehicularRecordsPendingToBackup.length > 0) {
            Log.d(TAG, "Retrying to backup " + vehicularRecordsPendingToBackup.length + " records");
            apiClient.postVehicularCapRecord(Arrays.asList(vehicularRecordsPendingToBackup), vehicularCapacityRecordRepository);
        } else {
            Log.d(TAG, "There are no records pending to backup");
        }
    }

    public void onClick(View view) {
        if (validateFields()) {
            VehicularCapacity vehicularCapacity = backup();
            Intent myIntent = new Intent(VehicularCapacityForm.this, VehicularCapacityActivity.class);
            myIntent.putExtra("composedId", vehicularCapacity.getComposedId());
            myIntent.putExtra("movements", vehicularCapacity.getVehicleMove());
            VehicularCapacityForm.this.startActivity(myIntent);
            this.finish();
        }
    }

    private VehicularCapacity backup() {

        StringBuilder movementsSelected = new StringBuilder();
        for (CheckBox movement : listOfMovements) {
            if (movement.isChecked()) {
                movementsSelected.append(movement.getText().toString());
                movementsSelected.append(" ");
            }
        }

        VehicularCapacity vehicularCapacity = VehicularCapacity.builder()
                .capturist(SaveSharedPreference.getUserName(getApplicationContext()))
                .viaOfStudy(viaOfStudySpinner.getSelectedItem().toString())
                .vehicleMove(movementsSelected.toString())
                .directionLane(wayDirectionSpinner.getSelectedItem().toString())
                .backedUpRemotely(0)
                .deviceId(android_device_id)
                .timeStamp(DATE_FORMAT.format(Calendar.getInstance().getTime()))
                .clientId(1)
                .build();

        Log.d(TAG, vehicularCapacity.toString());
        long generatedId = vehicularCapacityRepository.save(vehicularCapacity);

        composedId = String.format("%s-%d%n-%d%n",
                android_device_id,
                generatedId,
                Calendar.getInstance().getTimeInMillis());

        vehicularCapacity.setId((int) generatedId);
        vehicularCapacity.setComposedId(composedId);

        vehicularCapacityRepository.updateInBatch(Lists.newArrayList(vehicularCapacity)
                .toArray(new VehicularCapacity[1]));

        //Backup in remote server
        apiClient.postVehicularCapMeta(
                Lists.newArrayList(vehicularCapacity),
                vehicularCapacityRepository);

        return vehicularCapacity;
    }

    private boolean validateFields() {

        if (viaOfStudySpinner.getSelectedItemPosition() < 1) {
            Toast.makeText(getApplicationContext(), "Ingresa una via de estudio",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (wayDirectionSpinner.getSelectedItemPosition() < 1) {
            Toast.makeText(getApplicationContext(), "Ingresa un sentido",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!straightCheckbox.isChecked()
                && !turnLeftCheckbox.isChecked()
                && !turnRightCheckbox.isChecked()
                && !returnCheckbox.isChecked()) {
            Toast.makeText(getApplicationContext(), "Selecciona al menos un movimiento",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
