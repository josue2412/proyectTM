package com.sgcities.tdc.optimizer.application.shared;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sgcities.tdc.optimizer.BuildConfig;
import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.LoginActivity;
import com.sgcities.tdc.optimizer.infrastructure.preferencesmanagement.SaveSharedPreference;

import lombok.Getter;

@Getter
public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected String deviceId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracker_activity_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String appBuildInfo = String.format("App version: %s \nGit commit: %s",
                BuildConfig.VERSION_NAME, BuildConfig.GitHash);
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(getApplicationContext(), appBuildInfo,Toast.LENGTH_SHORT).show();
                return true;
            case R.id.finishRoute:
                finish();
                return true;
            case R.id.changeUser:
                SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                getApplicationContext().startActivity(myIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}