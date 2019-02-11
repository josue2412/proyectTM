package com.example.josuerey.helloworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuerey.helloworld.network.AscDescAssignmentResponse;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.example.josuerey.helloworld.utilities.AssignmentListAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import lombok.Getter;

@Getter
public class AscDescAssignmentsActivity extends AppCompatActivity
        implements AssignmentsDisplay<AscDescAssignmentResponse> {

    private final String TAG = this.getClass().getSimpleName();
    private ListView assignmentsListView;
    private TextView capturistTextView;
    private TextView retrieveAssignmentsStatus;
    private Button retryRetrieveAssignments;
    private Context context;
    private String requestUrl;

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
                Intent myIntent = new Intent(AscDescAssignmentsActivity.this, LoginActivity.class);
                AscDescAssignmentsActivity.this.startActivity(myIntent);
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
        requestUrl =
                "http://u856955919.hostingerapp.com/api/capturistAscDescAssignments?capturist_id=" +
                        SaveSharedPreference.getUserNameKey(getApplicationContext());

        context = getApplicationContext();
        assignmentsListView = findViewById(R.id.listOfAssignments);
        capturistTextView = findViewById(R.id.capturist_name);
        retrieveAssignmentsStatus = findViewById(R.id.app_status);
        retryRetrieveAssignments = findViewById(R.id.retry_assignments);

        capturistTextView.setText(SaveSharedPreference.getUserName(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        retrieveAssignments((List assignments) -> {
            List<AscDescAssignmentResponse> castedAssignments =
                        gson.fromJson(gson.toJson(assignments), new TypeToken<List<AscDescAssignmentResponse>>() {}.getType());

            setAssignmentsAdapter(new AssignmentListAdapter(castedAssignments, this.getContext()));
        });
    }

    @Override
    public void setAssignmentsAdapter(AssignmentListAdapter customAdapter) {
        getAssignmentsListView().setAdapter(customAdapter);
        getAssignmentsListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClickedAssignment((AscDescAssignmentResponse)parent.getAdapter().getItem(position));
            }
        });
    }

    @Override
    public void handleClickedAssignment(AscDescAssignmentResponse assignmentResponse) {
        Intent fillFormIntent = new Intent(AscDescAssignmentsActivity.this, TrackerFormActivity.class);
        fillFormIntent.putExtra("ascDescAssignment", gson.toJson(assignmentResponse));
        Log.d(TAG, "Handling ascDescAssignment: " + assignmentResponse.getId());
        AscDescAssignmentsActivity.this.startActivity(fillFormIntent);
    }
}
