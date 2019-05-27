package com.example.josuerey.helloworld.application.vehicularcap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.application.LoginActivity;
import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.infrastructure.preferencesmanagement.SaveSharedPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EmergencyNotificationActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private String assignmentId;
    private EditText messageNotification;
    private TextView sendingFailTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracker_activity_menu, menu);
        return true;
    }

    /**
     * Defines the behavior of the app menu on this activity.
     * @param item clicked item
     * @return true if the event was consumed and handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(getApplicationContext(), "No disponible",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.finishRoute:
                Intent myIntent = new Intent(EmergencyNotificationActivity.this, VehicularCapAssignmentsActivity.class);
                EmergencyNotificationActivity.this.startActivity(myIntent);
                finish();
                return true;
            case R.id.changeUser:
                SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
                Intent myIntent2 = new Intent(EmergencyNotificationActivity.this, LoginActivity.class);
                EmergencyNotificationActivity.this.startActivity(myIntent2);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_notification);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignmentId = extras.getString("assignmentId");
        }
        messageNotification = findViewById(R.id.emergency_message);
        sendingFailTextView = findViewById(R.id.sendingFail);
    }

    public void sendNotification(View view) {

        postEmergencyMessage(messageNotification.getText().toString());
    }

    public void postEmergencyMessage(final String msg) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/capturistNotification";
        final ProgressDialog pdLoading = new ProgressDialog(this);
        pdLoading.setMessage("\tEnviando...");
        pdLoading.show();
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d(TAG, String.format("Emergency message successfully posted: %s", response));
                        Toast.makeText(getApplicationContext(), "Enviado",Toast.LENGTH_SHORT).show();
                        messageNotification.setText("");
                        if (sendingFailTextView.getVisibility() == View.VISIBLE) {
                            sendingFailTextView.setVisibility(View.INVISIBLE);
                        }
                        pdLoading.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        sendingFailTextView.setVisibility(View.VISIBLE);
                        pdLoading.dismiss();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> postMap = new HashMap<>();
                        postMap.put("message", msg);
                        postMap.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(Calendar.getInstance().getTime()));
                        postMap.put("assignmentId", assignmentId);
                        Log.i(TAG, String.format("Posting message : %s with assignmentId: %s date: %s", msg, assignmentId, postMap.get("timestamp")));

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(getApplication()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(15),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
