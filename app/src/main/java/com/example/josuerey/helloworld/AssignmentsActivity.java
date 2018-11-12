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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacity;
import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacityRepository;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.network.AssignmentResponse;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.example.josuerey.helloworld.utilidades.CustomAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AssignmentsActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ListView assignmentsListView;
    private TextView capturistTextView;

    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private APIClient apiClient;

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
        apiClient = APIClient.builder().app(getApplication()).build();

        checkForRecordsPendingToBackup();
    }

    private void setAssignments(final ArrayList<AssignmentResponse> assignments) {

        CustomAdapter customAdapter = new CustomAdapter(assignments, getApplicationContext());
        assignmentsListView.setAdapter(customAdapter);
        assignmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AssignmentResponse dataModel= assignments.get(position);

                Intent myIntent = new Intent(AssignmentsActivity.this, VehicularCapacityActivity.class);
                myIntent.putExtra("composedId", String.valueOf(dataModel.getId()));
                myIntent.putExtra("movements", dataModel.getMovement());
                AssignmentsActivity.this.startActivity(myIntent);
                finish();
            }
        });
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
                            setAssignments(new ArrayList<AssignmentResponse>(Arrays.asList(assignmentResponse)));
                            pdLoading.dismiss();
                            Log.d(TAG, "Assignments: " + assignmentResponse[0].getMovement());
                        } catch (IOException e) {
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
    }
}
