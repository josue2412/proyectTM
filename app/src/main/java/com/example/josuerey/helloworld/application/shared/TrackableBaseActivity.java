package com.example.josuerey.helloworld.application.shared;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TrackableBaseActivity extends BaseActivity implements LocationListener {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected int updatesLocationDistance = 100;
    protected GPSLocation currentLocation;
    private LocationManager mlocManager;

    @Override
    public void onLocationChanged(Location location) {

        currentLocation = GPSLocation.builder()
                .lat(location.getLatitude())
                .lon(location.getLongitude())
                .timeStamp(DATE_FORMAT.format(new Date(location.getTime())))
                .deviceId(deviceId)
                .build();

        Log.d(TAG, String.format("Location change: Lat = %s, Lon= %s, Ts = %s",
                String.valueOf(currentLocation.getLat()),
                String.valueOf(currentLocation.getLon()),
                currentLocation.getTimeStamp()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d(TAG, "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d(TAG, "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d(TAG, "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    protected void locationStop() {
        if (mlocManager != null) {
            mlocManager.removeUpdates(this);
            Log.d(TAG, String.format("Stopping location updates %s", mlocManager.toString()));
            mlocManager = null;
        }
    }

    protected void locationStart() {
        Log.d(TAG, "Starting location updates");
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
            Log.d(TAG, "Going back, no permission :(");
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, updatesLocationDistance, this);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "GPS provider enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "GPS provider disabled");
    }

    protected boolean requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return false;
        }
        return true;
    }
}
