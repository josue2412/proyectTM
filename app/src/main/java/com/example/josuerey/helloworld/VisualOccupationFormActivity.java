package com.example.josuerey.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.josuerey.helloworld.domain.busoccupation.BusOccupation;
import com.example.josuerey.helloworld.domain.busoccupation.BusOccupationRepository;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudyRepository;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadata;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadataRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.utilidades.ExportData;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class VisualOccupationFormActivity extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Spinner spinnerStudyVia;
    private Spinner spinnerWayDirection;
    private EditText editTextWaterConditions;
    private EditText editTextCross;
    private EditText editTextEnc;
    private EditText editTextObservations;
    private Button btnStartStudy;
    private ViaOfStudyRepository viaOfStudyRepository;
    private String android_device_id;
    private VisualOccupationMetadataRepository visualOccupationMetadataRepository;
    private APIClient apiClient;
    private final String TAG = this.getClass().getSimpleName();
    private BusOccupationRepository busOccupationRepository;
    private String composedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.visual_occupation_form_activity);
        spinnerStudyVia = (Spinner) findViewById(R.id.spinnerStudyVia);
        spinnerWayDirection = (Spinner) findViewById(R.id.spinnerWayDirection);
        editTextWaterConditions = (EditText) findViewById(R.id.editTextWaterConditions);
        editTextCross = (EditText) findViewById(R.id.editTextCross);
        editTextEnc = (EditText) findViewById(R.id.editTextEnc);
        editTextObservations = (EditText) findViewById(R.id.editTextObservations);
        btnStartStudy = (Button) findViewById(R.id.btnStartStudy);

        viaOfStudyRepository = new ViaOfStudyRepository(getApplication());
        visualOccupationMetadataRepository = new VisualOccupationMetadataRepository(getApplication());
        busOccupationRepository = new BusOccupationRepository(getApplication());

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
        spinnerStudyVia.setAdapter(adapter);

        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        apiClient = APIClient.builder().app(getApplication()).build();

        checkForRecordsPendingToBackup();
    }

    private void checkForRecordsPendingToBackup() {

        BusOccupation[] recordsPendingToBackup = busOccupationRepository.findPendingToBackup();
        if (recordsPendingToBackup.length > 0) {

            Log.d(TAG, String.format("Retrying to backup %d busOccupationMetadata records",
                    recordsPendingToBackup.length));
            apiClient.postBusOccupation(Arrays.asList(recordsPendingToBackup), busOccupationRepository);
        } else {

            Log.d(TAG, "There are no busOccupation records to update");
        }

        VisualOccupationMetadata[] metaRecordsPendingToBackup =
                visualOccupationMetadataRepository.findPendingToBackup();

        if (metaRecordsPendingToBackup.length > 0) {

            Log.d(TAG, String.format("Retrying to backup %d busOccupation records",
                    metaRecordsPendingToBackup.length));
            apiClient.postBusOccupationMeta(Arrays.asList(metaRecordsPendingToBackup),
                    visualOccupationMetadataRepository);
        } else {
            Log.d(TAG, "There are no busOccupationMeta records to update");
        }

    }

    private VisualOccupationMetadata backup() {

        VisualOccupationMetadata visualOccMetadata = VisualOccupationMetadata.builder()
                .capturist(editTextEnc.getText().toString())
                .viaOfStudy(spinnerStudyVia.getSelectedItem().toString())
                .directionLane(spinnerWayDirection.getSelectedItem().toString())
                .crossroads(editTextCross.getText().toString())
                .observations(editTextObservations.getText().toString())
                .waterConditions(editTextWaterConditions.getText().toString())
                .timeStamp(DATE_FORMAT.format(Calendar.getInstance().getTime()))
                .backedUpRemotely(0)
                .deviceId(android_device_id)
                .build();

        //Persist in database
        long generatedId = visualOccupationMetadataRepository.save(visualOccMetadata);
        visualOccMetadata.setId((int)generatedId);

        composedId = String.format("%s-%d%n-%d%n",
                android_device_id,
                generatedId,
                Calendar.getInstance().getTimeInMillis());

        visualOccMetadata.setComposedId(composedId);
        visualOccupationMetadataRepository.updateVisualOccMetadata(
                Lists.newArrayList(visualOccMetadata).toArray(new VisualOccupationMetadata[1]));


        // Backup in remote server
        apiClient.postBusOccupationMeta(Lists.newArrayList(visualOccMetadata), visualOccupationMetadataRepository);
        //apiClient.postVisOccMeta(visualOccMetadata, visualOccupationMetadataRepository);
        // Create Metadata file
        ExportData.createFile(String.format("OcupacionVisual-%d.txt", generatedId), visualOccMetadata.toString());
        return visualOccMetadata;
    }


    public void onClick(View v) {

        if (v.getId() == R.id.btnStartStudy) {
            if (fieldsValidateSuccess()) {
                VisualOccupationMetadata visualOccMetadata = backup();

                Intent studyIntent = new Intent(
                        VisualOccupationFormActivity.this,
                        VisualOccupationActivity.class);
                studyIntent.putExtra("ViaOfStudy", visualOccMetadata.getViaOfStudy());
                studyIntent.putExtra("ViaOfStudyId",
                        String.valueOf(spinnerStudyVia.getSelectedItemId()));
                studyIntent.putExtra("studyMetadataId",
                        String.valueOf(visualOccMetadata.getId()));
                studyIntent.putExtra("composedId", composedId);

                this.startActivity(studyIntent);
                this.finish();
            }
        }
    }

    private boolean fieldsValidateSuccess() {
        if(TextUtils.isEmpty(editTextCross.getText().toString())) {
            editTextCross.setError("Favor de ingresar un cruce");
            editTextCross.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(editTextEnc.getText().toString())) {
            editTextEnc.setError("Favor de ingresar un nombre");
            editTextEnc.requestFocus();
            return false;
        }

        if (spinnerStudyVia.getSelectedItemPosition() < 1){
            spinnerStudyVia.requestFocus();
            return false;
        }

        if (spinnerWayDirection.getSelectedItemPosition() < 1){
            return false;
        }

        return true;
    }
}
