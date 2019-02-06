package com.example.josuerey.helloworld;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.network.AssignmentRetrievedCallback;
import com.example.josuerey.helloworld.utilities.AssignmentListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface AssignmentsDisplay<T> {
    Gson gson = new Gson();
    String getTAG();
    Context getContext();
    String getRequestUrl();
    TextView getRetrieveAssignmentsStatus();
    TextView getRetryRetrieveAssignments();
    ListView getAssignmentsListView();

    /**
     * Fills the input ArrayAdapter with the retrieved assignments.
     * @param customAdapter
     */
    void setAssignmentsAdapter(AssignmentListAdapter customAdapter);

    void handleClickedAssignment(T assignment);

    /**
     * Tells the user the status of the assignments request.
     *
     * @param msg indicating the reason why assignments were not retrieved.
     * @param succeed true if assignments were found
     */
    default void setStatusMsg(String msg, boolean succeed) {
        if (succeed) {
            getRetrieveAssignmentsStatus().setVisibility(View.INVISIBLE);
            getRetryRetrieveAssignments().setVisibility(View.INVISIBLE);
        } else {
            getRetryRetrieveAssignments().setVisibility(View.VISIBLE);
            getRetrieveAssignmentsStatus().setVisibility(View.VISIBLE);
            getRetrieveAssignmentsStatus().setText(msg);
        }
    }

    default void retrieveAssignments(final AssignmentRetrievedCallback callback){
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, getRequestUrl(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<T> assignmentResponse =
                                gson.fromJson(response, new TypeToken<List<T>>() {}.getType());

                        if (assignmentResponse.isEmpty()) {
                            setStatusMsg("No se encontraron tareas para tu usuario", false);
                        } else {
                            setStatusMsg(null, true);
                        }
                        callback.onSuccess(assignmentResponse);
                        Log.d(getTAG(), String.format("Number of assignments retrieved: %d", assignmentResponse.size()));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(getTAG(), "Volley error response");
                        error.printStackTrace();
                        setStatusMsg("Falló la conexión a Internet", false);
                    }
                }){
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(getContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(15),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}