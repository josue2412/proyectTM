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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuerey.helloworld.network.VisualOccupationAssignmentResponse;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.example.josuerey.helloworld.utilities.VisualOccAssignmentListAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import lombok.Getter;

@Getter
public class VisualOccupationAssignmentsActivity extends AppCompatActivity
        implements AssignmentsDisplay<VisualOccupationAssignmentResponse>{

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private String requestUrl;
    private TextView retrieveAssignmentsStatus;
    private TextView retryRetrieveAssignments;
    private ListView assignmentsListView;
    private TextView capturistTextView;

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
                Intent myIntent = new Intent(VisualOccupationAssignmentsActivity.this, LoginActivity.class);
                VisualOccupationAssignmentsActivity.this.startActivity(myIntent);
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
                "http://u856955919.hostingerapp.com/api/capturistVisualOccupationAssignments?capturist_id=" +
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
        callRetrieveAssignments();
    }

    @Override
    public void callRetrieveAssignments() {
        retrieveAssignments((List assignments) -> {
            List<VisualOccupationAssignmentResponse> castedAssignments =
                    gson.fromJson(gson.toJson(assignments), new TypeToken<List<VisualOccupationAssignmentResponse>>() {}.getType());

            setAssignmentsAdapter(new VisualOccAssignmentListAdapter(castedAssignments, this.getContext()));
        });
    }

    public void setAssignmentsAdapter(VisualOccAssignmentListAdapter customAdapter) {
        getAssignmentsListView().setAdapter(customAdapter);
        getAssignmentsListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClickedAssignment((VisualOccupationAssignmentResponse)parent.getAdapter().getItem(position));
            }
        });
    }

    @Override
    public void handleClickedAssignment(VisualOccupationAssignmentResponse assignmentResponse) {
        Intent fillFormIntent = new Intent(VisualOccupationAssignmentsActivity.this,
                VisualOccupationFormActivity.class);
        fillFormIntent.putExtra("visOccAssignment", gson.toJson(assignmentResponse));
        Log.d(TAG, "Handling VisualOccupationAssignment: " + assignmentResponse.getId());
        VisualOccupationAssignmentsActivity.this.startActivity(fillFormIntent);
    }
}
