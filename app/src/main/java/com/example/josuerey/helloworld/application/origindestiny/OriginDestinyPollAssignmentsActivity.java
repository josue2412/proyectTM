package com.example.josuerey.helloworld.application.origindestiny;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView;
import android.util.Log;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.application.shared.AssignmentsDisplay;
import com.example.josuerey.helloworld.application.shared.BaseActivity;
import com.example.josuerey.helloworld.domain.origindestiny.OriginDestinyAssignmentResponse;
import com.example.josuerey.helloworld.infrastructure.preferencesmanagement.SaveSharedPreference;
import com.example.josuerey.helloworld.utilities.OriginDestinyAssignmentListAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import lombok.Getter;

@Getter
public class OriginDestinyPollAssignmentsActivity extends BaseActivity
        implements AssignmentsDisplay<OriginDestinyAssignmentResponse> {

    protected Context context;
    protected String requestUrl;
    protected TextView retrieveAssignmentsStatus;
    protected TextView retryRetrieveAssignments;
    protected ListView assignmentsListView;
    protected TextView capturistTextView;
    protected ProgressBar downloadAssignmentsPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_assignments);
        requestUrl = String.format("%s%s%s", this.serverIp,
                "api/capturistOriginDestinyAssignments?capturist_id=",
                SaveSharedPreference.getUserNameKey(getApplicationContext()));

        context = getApplicationContext();
        assignmentsListView = findViewById(R.id.listOfAssignments);
        capturistTextView = findViewById(R.id.capturist_name);
        retrieveAssignmentsStatus = findViewById(R.id.app_status);
        retryRetrieveAssignments = findViewById(R.id.retry_assignments);
        downloadAssignmentsPB = findViewById(R.id.download_assignments_progress_bar);

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
            List<OriginDestinyAssignmentResponse> castedAssignments =
                    gson.fromJson(gson.toJson(assignments), new TypeToken<List<OriginDestinyAssignmentResponse>>() {}.getType());
            setAssignmentsAdapter(new OriginDestinyAssignmentListAdapter(castedAssignments, this.getContext()));
        });
    }

    private void setAssignmentsAdapter(OriginDestinyAssignmentListAdapter customAdapter) {
        getAssignmentsListView().setAdapter(customAdapter);
        getAssignmentsListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleClickedAssignment((OriginDestinyAssignmentResponse)parent.getAdapter().getItem(position));
            }
        });
    }

    @Override
    public void handleClickedAssignment(OriginDestinyAssignmentResponse assignmentResponse) {
        Intent startPollIntent = new Intent(this.getContext(), OriginDestinyPollActivity.class);
        startPollIntent.putExtra("originDestinyAssignment", gson.toJson(assignmentResponse));
        Log.d(TAG, "Handling OriginDestinyAssignment: " + assignmentResponse.getId());
        getContext().startActivity(startPollIntent);
    }
}
