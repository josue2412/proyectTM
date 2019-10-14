package com.sgcities.tdc.optimizer.application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.ascdesc.AscDescAssignmentsActivity;
import com.sgcities.tdc.optimizer.application.origindestiny.OriginDestinyPollAssignmentsActivity;
import com.sgcities.tdc.optimizer.application.shared.BaseActivity;
import com.sgcities.tdc.optimizer.application.vehicularcap.VehicularCapAssignmentsActivity;
import com.sgcities.tdc.optimizer.application.visualocc.VisualOccupationAssignmentsActivity;
import com.sgcities.tdc.optimizer.infrastructure.preferencesmanagement.SaveSharedPreference;

public class HomeActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView capturistLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);
        requestPermissions();

        capturistLogged = findViewById(R.id.logged_capturist);
        capturistLogged.setText("Usuario: " + SaveSharedPreference.getUserName(getApplicationContext()));
    }

    public void onClick(View view) {

        Intent studyIntent = null;
        switch (view.getId()) {
            case R.id.btnAscDescPassengers:
                studyIntent = new Intent(this, AscDescAssignmentsActivity.class);
                break;
            case R.id.btnVisualOccupation:
                studyIntent = new Intent(this, VisualOccupationAssignmentsActivity.class);
                break;
            case R.id.btnVehicularCap:
                studyIntent = new Intent(this, VehicularCapAssignmentsActivity.class);
                break;
            case R.id.btnOriginDestinyStudy:
                studyIntent = new Intent(this, OriginDestinyPollAssignmentsActivity.class);
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
