package com.example.josuerey.helloworld.application;

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
import android.widget.RelativeLayout;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.infrastructure.preferencesmanagement.SaveSharedPreference;


public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private EditText username;
    private EditText usernameKey;
    private Button submitBtn;
    private RelativeLayout loginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        username = findViewById(R.id.usernameText);
        usernameKey = findViewById(R.id.user_key_value);
        submitBtn = findViewById(R.id.submit);
        loginForm = findViewById(R.id.loginForm);

        // Check if UserResponse is Already Logged In
        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            requestPermissions();
            loginForm.setVisibility(View.VISIBLE);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Make form visible
                userLogin(username.getText().toString(), usernameKey.getText().toString());
            }
        });
    }

    private void userLogin(String username, String usernameKey) {

        SaveSharedPreference.setLoggedIn(getApplicationContext(), true);
        SaveSharedPreference.setUserName(getApplicationContext(), username);
        SaveSharedPreference.setUserNameKey(getApplicationContext(), usernameKey);

        Log.d(TAG, String.format("User logged as %s and key %s", username, usernameKey));
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
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
