package com.example.josuerey.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);
    }

    public void onClick(View view) {

        Intent studyIntent = null;
        switch (view.getId()) {
            case R.id.btnAscDescPassengers:
                studyIntent = new Intent(HomeActivity.this, TrackerFormActivity.class);
                break;
            case R.id.btnVisualOccupation:
                studyIntent = new Intent(HomeActivity.this, VisualOccupationFormActivity.class);
                break;
        }

        if (studyIntent != null)
            this.startActivity(studyIntent);
    }
}
