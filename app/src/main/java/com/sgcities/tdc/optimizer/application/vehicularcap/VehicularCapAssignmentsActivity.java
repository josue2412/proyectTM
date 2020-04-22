package com.sgcities.tdc.optimizer.application.vehicularcap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.shared.AssignmentsDisplay;
import com.sgcities.tdc.optimizer.application.shared.BaseActivity;
import com.sgcities.tdc.optimizer.infrastructure.network.VehicularCapAssignmentResponse;
import com.sgcities.tdc.optimizer.infrastructure.preferencesmanagement.SaveSharedPreference;
import com.sgcities.tdc.optimizer.utilities.VehicularCapAssignmentListAdapter;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.List;

import lombok.Getter;

@Getter
public class VehicularCapAssignmentsActivity extends BaseActivity
    implements AssignmentsDisplay<VehicularCapAssignmentResponse> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private String requestUrl;
    private ListView assignmentsListView;
    private TextView capturistTextView;
    private TextView retrieveAssignmentsStatus;
    private Button retryRetrieveAssignments;
    private ProgressBar downloadAssignmentsPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_assignments);

        requestUrl = String.format("%s%s%d", this.HOST_ASSIGNMENTS_SOURCE,
                this.getResources().getString(R.string.vehicular_cap_assignment_api_url),
                SaveSharedPreference.getUserId(getApplicationContext()));

        context = getApplicationContext();
        assignmentsListView = findViewById(R.id.listOfAssignments);
        capturistTextView = findViewById(R.id.capturist_name);
        retrieveAssignmentsStatus = findViewById(R.id.app_status);
        retryRetrieveAssignments = findViewById(R.id.retry_assignments);
        downloadAssignmentsPB = findViewById(R.id.download_assignments_progress_bar);

        capturistTextView.setText(SaveSharedPreference.getUserName(getApplicationContext()));
        callRetrieveAssignments();
    }

    @Override
    public void callRetrieveAssignments() {
        retrieveAssignments((List assignments) -> {
            List<VehicularCapAssignmentResponse> castedAssignments =
                    gson.fromJson(gson.toJson(assignments),
                            new TypeToken<List<VehicularCapAssignmentResponse>>() {}.getType());

            setAssignmentsAdapter(
                    new VehicularCapAssignmentListAdapter(castedAssignments, this.getContext()));
        });
    }

    public void setAssignmentsAdapter(VehicularCapAssignmentListAdapter customAdapter) {
        getAssignmentsListView().setAdapter(customAdapter);
        getAssignmentsListView().setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) ->
                        handleClickedAssignment(
                                (VehicularCapAssignmentResponse)parent.getAdapter().getItem(position)));
    }

    @Override
    public void handleClickedAssignment(VehicularCapAssignmentResponse assignmentResponse) {
        Intent fillFormIntent = new Intent(VehicularCapAssignmentsActivity.this,
                VehicularCapacityGenActivity.class);
        fillFormIntent.putExtra("vehicCapAssignment", gson.toJson(assignmentResponse));
        Log.d(TAG, "Handling VehicularCapacityAssignment: " + assignmentResponse.getId());
        VehicularCapAssignmentsActivity.this.startActivity(fillFormIntent);
    }
}
