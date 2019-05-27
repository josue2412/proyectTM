package com.example.josuerey.helloworld.application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.application.ascdesc.AscDescAssignmentsActivity;
import com.example.josuerey.helloworld.application.vehicularcap.VehicularCapAssignmentsActivity;
import com.example.josuerey.helloworld.application.visualocc.VisualOccupationAssignmentsActivity;
import com.example.josuerey.helloworld.infrastructure.preferencesmanagement.SaveSharedPreference;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Button passengersAscDes;
    private Button visualOcStudy;
    private TextView capturistLogged;

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
                Intent myIntent = new Intent(HomeActivity.this, LoginActivity.class);
                HomeActivity.this.startActivity(myIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);
        requestPermissions();

        passengersAscDes = findViewById(R.id.btnAscDescPassengers);
        visualOcStudy = findViewById(R.id.btnVisualOccupation);
        capturistLogged = findViewById(R.id.logged_capturist);

        capturistLogged.setText("Usuario: " + SaveSharedPreference.getUserName(getApplicationContext()));
    }

    public void onClick(View view) {

        Intent studyIntent = null;
        switch (view.getId()) {
            case R.id.btnAscDescPassengers:
                studyIntent = new Intent(HomeActivity.this, AscDescAssignmentsActivity.class);
                break;
            case R.id.btnVisualOccupation:
                studyIntent = new Intent(HomeActivity.this, VisualOccupationAssignmentsActivity.class);
                break;
            case R.id.btnVehicularCap:
                studyIntent = new Intent(HomeActivity.this, VehicularCapAssignmentsActivity.class);
                break;
        }

        if (studyIntent != null)
            this.startActivity(studyIntent);
    }

    private void requestPermissions() {
        // Request external file write permission
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSIONS, 112);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        // Check for GPS usage permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        }
    }
}
