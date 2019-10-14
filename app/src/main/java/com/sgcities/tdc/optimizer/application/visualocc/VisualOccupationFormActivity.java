package com.sgcities.tdc.optimizer.application.visualocc;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.shared.BaseActivity;
import com.sgcities.tdc.optimizer.domain.busoccupation.BusOccupation;
import com.sgcities.tdc.optimizer.domain.busoccupation.BusOccupationRepository;
import com.sgcities.tdc.optimizer.domain.visualoccupation.VisualOccupationMetadata;
import com.sgcities.tdc.optimizer.domain.visualoccupation.VisualOccupationMetadataRepository;
import com.sgcities.tdc.optimizer.infrastructure.network.APIClient;
import com.sgcities.tdc.optimizer.infrastructure.network.VisualOccupationAssignmentResponse;
import com.sgcities.tdc.optimizer.utilities.ExportData;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class VisualOccupationFormActivity extends BaseActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private EditText editTextStudyVia;
    private EditText editTextWayDirection;
    private EditText editTextWaterConditions;
    private EditText editTextCross;
    private EditText editTextObservations;
    private EditText durationEditText;
    private EditText beginAtDateEditText;
    private EditText beginAtPlaceEditText;
    private String android_device_id;
    private VisualOccupationMetadataRepository visualOccupationMetadataRepository;
    private APIClient apiClient;
    private final String TAG = this.getClass().getSimpleName();
    private BusOccupationRepository busOccupationRepository;
    private VisualOccupationAssignmentResponse assignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visual_occupation_form_activity);
        editTextStudyVia = findViewById(R.id.via_of_study_value);
        editTextWayDirection = findViewById(R.id.lane_direction_value);
        editTextWaterConditions = findViewById(R.id.editTextWaterConditions);
        editTextCross = findViewById(R.id.crossroad_under_study_value);
        editTextObservations = findViewById(R.id.editTextObservations);
        beginAtDateEditText = findViewById(R.id.begin_at_date_edit_text);
        beginAtPlaceEditText = findViewById(R.id.begin_at_place_edit_text);
        durationEditText = findViewById(R.id.duration_edit_text);

        visualOccupationMetadataRepository = new VisualOccupationMetadataRepository(getApplication());
        busOccupationRepository = new BusOccupationRepository(getApplication());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignment = new Gson().fromJson(
                    extras.getString("visOccAssignment"), VisualOccupationAssignmentResponse.class);
        }

        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        apiClient = APIClient.builder().app(getApplication()).build();

        checkForRecordsPendingToBackup();
    }


    private void checkForRecordsPendingToBackup() {

        List<BusOccupation> recordsPendingToBackup = busOccupationRepository.findRecordsPendingToBackUp();
        if (!recordsPendingToBackup.isEmpty()) {

            Log.d(TAG, String.format("Retrying to backup %d busOccupationMetadata records",
                    recordsPendingToBackup.size()));
            apiClient.postBusOccupation(recordsPendingToBackup, busOccupationRepository);
        } else {

            Log.d(TAG, "There are no busOccupation records to update");
        }
    }

    private VisualOccupationMetadata getProperVisualOccupationMetadata() {
        Log.d(TAG, "Searching for VisualOccupationMetadata with assignmentId: " + this.assignment.getId());

        VisualOccupationMetadata existingMetadata = visualOccupationMetadataRepository
                .findByAssignmentId(this.assignment.getId());

        if (existingMetadata != null) {
            // update local assignment with data from incoming assignment
            if (this.assignment.getIsEditable() == 1) {
                Log.d(TAG, "Updating VisualOccupationMetadata with assignmentId: " + existingMetadata.getAssignmentId());
                return visualOccupationMetadataRepository.updateMetadataFromAssignment(
                        this.assignment, existingMetadata.getId());
            }
            // return existing assignment
            Log.d(TAG, "Using VisualOccupationMetadata with assignmentId: " + existingMetadata.getAssignmentId());
            return existingMetadata;
        } else {
            return saveMetadata();
        }
    }

    private VisualOccupationMetadata saveMetadata() {

        VisualOccupationMetadata visualOccMetadata = VisualOccupationMetadata.builder()
                .viaOfStudy(editTextStudyVia.getText().toString())
                .directionLane(editTextWayDirection.getText().toString())
                .crossroads(editTextCross.getText().toString())
                .observations(editTextObservations.getText().toString())
                .waterConditions(editTextWaterConditions.getText().toString())
                .timeStamp(DATE_FORMAT.format(Calendar.getInstance().getTime()))
                .durationInHours(Integer.valueOf(durationEditText.getText().toString()))
                .assignmentId(this.assignment.getId())
                .beginAtDate(beginAtDateEditText.getText().toString())
                .beginAtPlace(beginAtPlaceEditText.getText().toString())
                .backedUpRemotely(0)
                .deviceId(android_device_id)
                .build();

        //Persist in database
        long generatedId = visualOccupationMetadataRepository.save(visualOccMetadata);
        visualOccMetadata.setId((int)generatedId);
        Log.d(TAG, "New VisualOccMetadata created: " + visualOccMetadata.toString());

        // Create Metadata file
        ExportData.createFile(String.format("OcupacionVisual-%d.txt", generatedId), visualOccMetadata.toString());
        return visualOccMetadata;
    }

    private void setFieldsValues() {
        editTextStudyVia.setText(this.assignment.getViaOfStudy());
        editTextCross.setText(this.assignment.getCrossroads());
        editTextWayDirection.setText(this.assignment.getDirectionLane());
        beginAtDateEditText.setText(this.assignment.getBeginAtDate());
        beginAtPlaceEditText.setText(this.assignment.getBeginAtPlace());
        durationEditText.setText(String.valueOf(this.assignment.getDurationInHours()));
        if (this.assignment.getIsEditable() == 0) {
            editTextWaterConditions.setEnabled(false);
            editTextObservations.setEnabled(false);
        }
        editTextObservations.setText(this.assignment.getObservations());
        editTextWaterConditions.setText(this.assignment.getWaterConditions());
    }

    public void onClick(View v) {

        if (v.getId() == R.id.btnStartStudy) {
            VisualOccupationMetadata visualOccMetadata = getProperVisualOccupationMetadata();
            apiClient.postBusOccupationMeta(Lists.newArrayList(visualOccMetadata), visualOccupationMetadataRepository);

            Intent studyIntent = new Intent(VisualOccupationFormActivity.this,
                    VisualOccupationActivity.class);
            studyIntent.putExtra("visualOccMetadata", new Gson().toJson(visualOccMetadata));
            studyIntent.putExtra("visualOccAssignment", new Gson().toJson(assignment));
            this.startActivity(studyIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFieldsValues();
    }
}