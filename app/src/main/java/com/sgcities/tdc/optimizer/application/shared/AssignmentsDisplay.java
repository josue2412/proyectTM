package com.sgcities.tdc.optimizer.application.shared;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.infrastructure.network.AssignmentRetrievedCallback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface AssignmentsDisplay<T> {
    String HOST_ASSIGNMENTS_SOURCE = "https://sgcities.com";
    Gson gson = new Gson();
    String getTAG();
    Context getContext();
    String getRequestUrl();
    TextView getRetrieveAssignmentsStatus();
    TextView getRetryRetrieveAssignments();
    ListView getAssignmentsListView();
    ProgressBar getDownloadAssignmentsPB();


    void handleClickedAssignment(T assignment);

    void callRetrieveAssignments();

    /**
     * Tells the user the status of the assignments request.
     *
     * @param msg indicating the reason why assignments were not retrieved.
     * @param succeed true if assignments were found
     */
    default void setStatusMsg(int msg, boolean succeed) {
        addOnclickListenerToRetryRetrieveAssignments();
        if (succeed) {
            getRetrieveAssignmentsStatus().setVisibility(View.INVISIBLE);
            getRetryRetrieveAssignments().setVisibility(View.INVISIBLE);
        } else {
            getRetryRetrieveAssignments().setVisibility(View.VISIBLE);
            getRetrieveAssignmentsStatus().setVisibility(View.VISIBLE);
            getRetrieveAssignmentsStatus().setText(msg);
        }
    }

    default void addOnclickListenerToRetryRetrieveAssignments() {
        getRetryRetrieveAssignments().setOnClickListener((View v) -> {
                Log.d(getTAG(), String.format("Retry retrieving assignments from: %s",
                        getRequestUrl()));
                callRetrieveAssignments();
            });
    }

    default void retrieveAssignments(final AssignmentRetrievedCallback callback){
        Log.d(getTAG(), String.format("Retrieving assignments from: %s", getRequestUrl()));
        getDownloadAssignmentsPB().setVisibility(ProgressBar.VISIBLE);
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, getRequestUrl(), (String response) -> {
                        List<T> assignmentResponse =
                                gson.fromJson(response, new TypeToken<List<T>>() {}.getType());

                        if (assignmentResponse.isEmpty()) {
                            setStatusMsg(R.string.volley_request_no_assignments_found, false);
                        } else {
                            setStatusMsg(0, true);
                        }
                        callback.onSuccess(assignmentResponse);
                        Log.d(getTAG(), String.format("Number of assignments retrieved: %d",
                                assignmentResponse.size()));
                        getDownloadAssignmentsPB().setVisibility(ProgressBar.INVISIBLE);
                    }
                , (VolleyError error) -> {
                        Log.e(getTAG(), String.format("Something went wrong with call to %s",
                                getRequestUrl()));
                        error.printStackTrace();
                        setStatusMsg(R.string.volley_request_no_internet_connection, false);
                        getDownloadAssignmentsPB().setVisibility(ProgressBar.INVISIBLE);
                    }){};
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(getContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(15),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}