package com.sgcities.tdc.optimizer.application.visualocc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.shared.AssignmentsDisplay;
import com.sgcities.tdc.optimizer.application.shared.BaseActivity;
import com.sgcities.tdc.optimizer.infrastructure.network.VisualOccupationAssignmentResponse;
import com.sgcities.tdc.optimizer.infrastructure.preferencesmanagement.SaveSharedPreference;
import com.sgcities.tdc.optimizer.utilities.VisualOccAssignmentListAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import lombok.Getter;

@Getter
public class VisualOccupationAssignmentsActivity extends BaseActivity
        implements AssignmentsDisplay<VisualOccupationAssignmentResponse> {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private String requestUrl;
    private TextView retrieveAssignmentsStatus;
    private TextView retryRetrieveAssignments;
    private ListView assignmentsListView;
    private TextView capturistTextView;
    private ProgressBar downloadAssignmentsPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_assignments);
        requestUrl = String.format("%s%s%d", this.serverIp,
                "/app/api/capturistVisualOccupationAssignments?capturist_id=",
                SaveSharedPreference.getUserId(getApplicationContext()));

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
            List<VisualOccupationAssignmentResponse> castedAssignments =
                    gson.fromJson(gson.toJson(assignments),
                            new TypeToken<List<VisualOccupationAssignmentResponse>>() {}.getType());

            setAssignmentsAdapter(new VisualOccAssignmentListAdapter(castedAssignments, this.getContext()));
        });
    }

    public void setAssignmentsAdapter(VisualOccAssignmentListAdapter customAdapter) {
        getAssignmentsListView().setAdapter(customAdapter);
        getAssignmentsListView().setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) ->
                    handleClickedAssignment(
                            (VisualOccupationAssignmentResponse)parent.getAdapter().getItem(position))
        );
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
