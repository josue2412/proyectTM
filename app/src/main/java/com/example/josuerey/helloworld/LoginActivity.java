package com.example.josuerey.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;


public class LoginActivity extends AppCompatActivity {

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
            Intent intent = new Intent(getApplicationContext(), AssignmentsActivity.class);
            startActivity(intent);
            finish();
        } else {
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

        Intent intent = new Intent(getApplicationContext(), AssignmentsActivity.class);
        startActivity(intent);
        finish();
    }
}
