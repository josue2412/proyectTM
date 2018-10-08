package com.example.josuerey.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudyRepository;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadata;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadataRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.utilidades.ExportData;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class VisualOccupationFormActivity extends AppCompatActivity {

    private Spinner spinnerStudyVia;
    private EditText editTextWayDirection;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.visual_occupation_form_activity);
        spinnerStudyVia = (Spinner) findViewById(R.id.spinnerStudyVia);
        editTextWayDirection = (EditText) findViewById(R.id.editTextWayDirection);
        editTextWaterConditions = (EditText) findViewById(R.id.editTextWaterConditions);
        editTextCross = (EditText) findViewById(R.id.editTextCross);
        editTextEnc = (EditText) findViewById(R.id.editTextEnc);
        editTextObservations = (EditText) findViewById(R.id.editTextObservations);
        btnStartStudy = (Button) findViewById(R.id.btnStartStudy);

        viaOfStudyRepository = new ViaOfStudyRepository(getApplication());
        visualOccupationMetadataRepository = new VisualOccupationMetadataRepository(getApplication());

        ViaOfStudy[] existingVias = viaOfStudyRepository.findAll();
        List<String> routes = new ArrayList<>();

        Log.d(TAG, "Existing vias: " + existingVias.length);

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
    }

    private VisualOccupationMetadata backup() {

        VisualOccupationMetadata visualOccMetadata = VisualOccupationMetadata.builder()
                .capturist(editTextEnc.getText().toString())
                .viaOfStudy(spinnerStudyVia.getSelectedItem().toString())
                .directionLane(editTextWayDirection.getText().toString())
                .crossroads(editTextCross.getText().toString())
                .observations(editTextObservations.getText().toString())
                .waterConditions(editTextWaterConditions.getText().toString())
                .backedUpRemotely(0)
                .deviceId(android_device_id)
                .build();

        //Persist in database
        long generatedId = visualOccupationMetadataRepository.save(visualOccMetadata);
        visualOccMetadata.setId((int)generatedId);
        //apiClient.postVisualOccStudyMetadata(Lists.newArrayList(visualOccMetadata),
                //visualOccupationMetadataRepository);
        // Create Metadata file
        //ExportData.createFile(String.format("OcupacionVisual-%d", generatedId), visualOccMetadata.toString());
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
                studyIntent.putExtra("ViaOfStudyId", String.valueOf(visualOccMetadata.getId()));

                this.startActivity(studyIntent);
                this.finish();
            }
        }
    }

    private boolean fieldsValidateSuccess() {

        if(TextUtils.isEmpty(editTextWayDirection.getText().toString())) {
            editTextWayDirection.setError("Favor de ingresar un carril / sentido");
            editTextWayDirection.requestFocus();
            return false;
        }

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

        return true;
    }
}