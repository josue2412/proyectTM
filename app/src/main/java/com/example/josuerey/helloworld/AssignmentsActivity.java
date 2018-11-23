package com.example.josuerey.helloworld;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.domain.assignment.Assignment;
import com.example.josuerey.helloworld.domain.assignment.AssignmentRepository;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.network.AssignmentResponse;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.example.josuerey.helloworld.utilities.CustomAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AssignmentsActivity extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String TAG = this.getClass().getSimpleName();
    private ListView assignmentsListView;
    private TextView capturistTextView;

    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private AssignmentRepository assignmentRepository;
    private APIClient apiClient;
    private List<Assignment> availableAssignments;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracker_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(getApplicationContext(), "No disponible",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.finishRoute:
                finish();
                return true;
            case R.id.changeUser:
                SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
                Intent myIntent = new Intent(AssignmentsActivity.this, LoginActivity.class);
                AssignmentsActivity.this.startActivity(myIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_assignments);

        assignmentsListView = (ListView) findViewById(R.id.listOfAssignments);
        capturistTextView = (TextView) findViewById(R.id.capturist_name);

        capturistTextView.setText(SaveSharedPreference.getUserName(getApplicationContext()));
        retrieveAssignments(SaveSharedPreference.getUserNameKey(getApplicationContext()));

        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        assignmentRepository = new AssignmentRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();

        availableAssignments = new ArrayList<>();
        checkForRecordsPendingToBackup();
    }

    /**
     * Check if either remote assignments aren't yet in the local database or if there were updated,
     * and merge them with local ones if it is the case.
     *
     * @param remoteAssignments
     */
    private void mergeAssignments(final ArrayList<AssignmentResponse> remoteAssignments) throws ParseException {

        for (AssignmentResponse remoteAssignment : remoteAssignments) {

            Optional<Assignment> existingAssignment = assignmentRepository
                    .findByServerId(remoteAssignment.getId());

            if (existingAssignment.isPresent()) {

                Date remoteLastUpdatedDate = DATE_FORMAT.parse(remoteAssignment.getUpdated_at());
                Date localLastUpdatedDate = DATE_FORMAT.parse(existingAssignment.get().getUpdatedAt());

                Assignment incomingAssignment = buildAssignment(remoteAssignment);
                // if remote assignment is newer, then replace the existing one
                if (remoteLastUpdatedDate.after(localLastUpdatedDate)) {

                    incomingAssignment.setId(existingAssignment.get().getId());

                    assignmentRepository.updateAssignment(incomingAssignment);
                    Log.d(TAG, "Merged assignment with id: " + String.valueOf(incomingAssignment.getId()));
                }

                if (incomingAssignment.getEnabled() == 1) {
                    incomingAssignment.setTimeOfStudy(existingAssignment.get().getTimeOfStudy());
                    availableAssignments.add(incomingAssignment);
                }
            } else {

                Assignment incomingAssignment = buildAssignment(remoteAssignment);
                availableAssignments.add(incomingAssignment);
                long generatedId = assignmentRepository.save(incomingAssignment);
                Log.d(TAG, "Inserted new assignment with id: " + String.valueOf(generatedId));
            }
        }

        setAssignments();
    }

    private Assignment buildAssignment(AssignmentResponse assignmentResponse) {

        return Assignment.builder()
                .serverId(assignmentResponse.getId())
                .capturistId(assignmentResponse.getCapturist_id())
                .projectId(assignmentResponse.getProject_id())
                .status("En proceso")
                .timeOfStudy("0" + String.valueOf(assignmentResponse.getDuration_in_hours()) + ":00:00")
                .beginAt(assignmentResponse.getBegin_at())
                .durationInHours(assignmentResponse.getDuration_in_hours())
                .streetFrom(assignmentResponse.getStreet_from())
                .streetTo(assignmentResponse.getStreet_to())
                .streetFromDirection(assignmentResponse.getStreet_from_direction())
                .streetToDirection(assignmentResponse.getStreet_to_direction())
                .streetFromCode(assignmentResponse.getStreet_from_code())
                .streetToCode(assignmentResponse.getStreet_to_code())
                .movement(assignmentResponse.getMovement())
                .movementCode(assignmentResponse.getMovement_code())
                .enabled(assignmentResponse.getEnabled())
                .createdAt(assignmentResponse.getCreated_at())
                .updatedAt(assignmentResponse.getUpdated_at())
                .build();
    }

    private void setAssignments() {

        final List<Assignment> groupedAssignments = groupAssignments(availableAssignments);

        CustomAdapter customAdapter = new CustomAdapter(groupedAssignments, getApplicationContext());
        assignmentsListView.setAdapter(customAdapter);
        assignmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Assignment dataModel= groupedAssignments.get(position);

                Intent myIntent = new Intent(AssignmentsActivity.this, VehicularCapacityActivity.class);
                myIntent.putExtra("assignmentId", String.valueOf(dataModel.getServerId()));
                myIntent.putExtra("movements", dataModel.getMovement());
                myIntent.putExtra("serverId", String.valueOf(dataModel.getServerId()));
                myIntent.putExtra("remainingTime", dataModel.getTimeOfStudy());
                myIntent.putExtra("studyDuration", String.valueOf(dataModel.getDurationInHours()));
                AssignmentsActivity.this.startActivity(myIntent);
                finish();
            }
        });
    }

    private List<Assignment> groupAssignments(List<Assignment> assignments) {
        HashMap<String, Assignment> assignmentHashMap = new HashMap<>();

        for (Assignment assignment : assignments) {

            if (!assignmentHashMap.containsKey(assignment.getBeginAt())) {

                StringBuilder origin = new StringBuilder();
                origin.append(assignment.getStreetFrom());
                origin.append(" " + assignment.getStreetFromDirection() + "\n");

                StringBuilder destiny = new StringBuilder();
                destiny.append(assignment.getStreetTo());
                destiny.append(" " + assignment.getStreetToDirection() + "\n");

                assignment.setNumberOfMovements(1);
                assignment.setStreetFrom(origin.toString());
                assignment.setStreetTo(destiny.toString());
                assignmentHashMap.put(assignment.getBeginAt(), assignment);
            } else {

                Assignment existingAssignment = assignmentHashMap.get(assignment.getBeginAt());
                int numberOfMovements = existingAssignment.getNumberOfMovements() + 1;

                StringBuilder origin = new StringBuilder();
                origin.append(existingAssignment.getStreetFrom() + assignment.getStreetFrom());
                origin.append(" " + assignment.getStreetFromDirection());

                StringBuilder destiny = new StringBuilder();
                destiny.append(existingAssignment.getStreetTo() + assignment.getStreetTo());
                destiny.append(" " + assignment.getStreetToDirection());

                String movements = existingAssignment.getMovement() + " " + assignment.getMovement();
                existingAssignment.setMovement(movements);
                existingAssignment.setNumberOfMovements(numberOfMovements);
                existingAssignment.setStreetFrom(origin.toString());
                existingAssignment.setStreetTo(destiny.toString());
            }
        }

        return new ArrayList<>(assignmentHashMap.values());
    }

    private void checkForRecordsPendingToBackup() {

        VehicularCapacityRecord[] vehicularRecordsPendingToBackup =
                vehicularCapacityRecordRepository.findRecordsPendingToBackup();
        if (vehicularRecordsPendingToBackup.length > 0) {
            Log.d(TAG, "Retrying to backup " + vehicularRecordsPendingToBackup.length + " records");
            apiClient.postVehicularCapRecord(Arrays.asList(vehicularRecordsPendingToBackup), vehicularCapacityRecordRepository);
        } else {
            Log.d(TAG, "There are no records pending to backup");
        }
    }

    public void retrieveAssignments(final String capturistId){
        final ObjectMapper mapper = new ObjectMapper();
        String requestUrl = "http://u856955919.hostingerapp.com/api/capturistAssignments?capturist_id=" + capturistId;
        final ProgressDialog pdLoading = new ProgressDialog(this);
        pdLoading.setMessage("\tLoading...");
        pdLoading.show();

        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AssignmentResponse[] assignmentResponse = mapper.readValue(response, AssignmentResponse[].class);
                            mergeAssignments(new ArrayList<AssignmentResponse>(Arrays.asList(assignmentResponse)));
                            pdLoading.dismiss();
                            Log.d(TAG, "Assignments: " + assignmentResponse[0].getMovement());
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        pdLoading.dismiss();
                    }
                }){
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(getApplication()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
