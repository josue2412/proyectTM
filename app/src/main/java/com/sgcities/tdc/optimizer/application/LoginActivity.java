package com.sgcities.tdc.optimizer.application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.shared.AssignmentsDisplay;
import com.sgcities.tdc.optimizer.domain.capturist.Capturist;
import com.sgcities.tdc.optimizer.infrastructure.network.UserLoginRetrievedCallback;
import com.sgcities.tdc.optimizer.infrastructure.preferencesmanagement.SaveSharedPreference;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private EditText username;
    private EditText usernameKey;
    private Button submitBtn;
    private RelativeLayout loginForm;
    private final static String LOGIN_REQUEST_URL = "/app/api/uservalidation?email=%s&pass=%s";
    private final Gson gson = new Gson();
    private String user;
    private String password;
    private ProgressBar progressBarValidateUser;
    private TextView loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        username = findViewById(R.id.usernameText);
        usernameKey = findViewById(R.id.user_key_value);
        submitBtn = findViewById(R.id.submit);
        loginForm = findViewById(R.id.loginForm);
        progressBarValidateUser = findViewById(R.id.validate_user_progress_bar);
        loginStatus = findViewById(R.id.login_status);

        // Check if UserResponse is Already Logged In
        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            requestPermissions();
            loginForm.setVisibility(View.VISIBLE);
        }

        submitBtn.setOnClickListener((View v) ->
                userLogin(username.getText().toString(), usernameKey.getText().toString()));
    }

    private void userLogin(String username, String usernameKey) {
        this.user = username;
        this.password = usernameKey;

        validateLogin((Object c) -> {
            Capturist cap = (Capturist) c;
            beginSession(cap);
        });
    }

    private void beginSession(Capturist cap) {
        SaveSharedPreference.setLoggedIn(getApplicationContext(), true);
        SaveSharedPreference.setUserEmail(getApplicationContext(), cap.getEmail());
        SaveSharedPreference.setUserPassword(getApplicationContext(), cap.getPassword());
        SaveSharedPreference.setUserName(getApplicationContext(), cap.getName());
        SaveSharedPreference.setUserId(getApplicationContext(), cap.getId());

        Log.d(TAG, String.format("User logged as %s and key %s", username, usernameKey));
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }


    private void validateLogin(final UserLoginRetrievedCallback callback) {

        Log.d(TAG, "Validating user...");
        progressBarValidateUser.setVisibility(ProgressBar.VISIBLE);
        loginStatus.setVisibility(View.INVISIBLE);
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, String.format("%s%s",
                        AssignmentsDisplay.serverIp,
                        String.format(LOGIN_REQUEST_URL, user, password)), (String response) -> {
                    Capturist capturistFetched = gson.fromJson(response, Capturist.class);

                    progressBarValidateUser.setVisibility(ProgressBar.INVISIBLE);
                    callback.onSuccess(capturistFetched);
                }, (VolleyError error) -> {
                    progressBarValidateUser.setVisibility(ProgressBar.INVISIBLE);
                    loginStatus.setVisibility(View.VISIBLE);
                    Log.d(TAG, String.format("Login request status code: %d", error.networkResponse.statusCode));
                    switch(error.networkResponse.statusCode) {
                        case 404:
                            loginStatus.setText(R.string.login_failed_user_unknown);
                            break;
                        case 401:
                            loginStatus.setText(R.string.login_failed_wrong_password);
                            break;
                        default:
                            loginStatus.setText(R.string.login_failed_unknown_reason);
                    }
                    Log.d(TAG, "Something went wrong during validation");
                });
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(15),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void requestPermissions() {
        // Request external file write permission
        String [] permissions = new String[2];
        boolean requestPermissions = false;

        // Check for GPS usage permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            requestPermissions = true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            requestPermissions = true;
        }

        if (requestPermissions) {
            Log.d(TAG, "Requesting permissions");
            ActivityCompat.requestPermissions(this, permissions, 112);
        }
    }
}
