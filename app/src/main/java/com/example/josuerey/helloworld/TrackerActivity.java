package com.example.josuerey.helloworld;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.josuerey.helloworld.entidades.BusStop;
import com.example.josuerey.helloworld.entidades.BusStopRepository;
import com.example.josuerey.helloworld.entidades.GPSLocation;
import com.example.josuerey.helloworld.entidades.GPSLocationRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackerActivity extends AppCompatActivity {

    private TextView latLongTextView;
    private TextView directionsTextView;
    private Button saveButton;
    private LocationManager mlocManager;
    private MyLocationListener mlocListener;

    private GPSLocation currentLocation;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private GPSLocationRepository gpsLocationRepository;
    private BusStopRepository busStopRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_activity);
        gpsLocationRepository = new GPSLocationRepository(getApplication());
        busStopRepository = new BusStopRepository(getApplication());

        //Bind layout components
        latLongTextView = findViewById(R.id.latLong);
        directionsTextView = findViewById(R.id.directions);
        saveButton = this.findViewById(R.id.btnSave);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarparadas();
            }
        });

        // Check for GPS usage permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();

        }
    }

    @Override
    public void onPause(){

        mlocManager.removeUpdates(mlocListener);
        super.onPause();
    }

    @Override
    protected void onResume() {

        locationStart();

        super.onResume();
    }


    private void locationStart() {

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);
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
            Log.i("debud","Going back :(");
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                (LocationListener) mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                (LocationListener) mlocListener);
        latLongTextView.setText("Localizacion agregada");
        directionsTextView.setText("");
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    directionsTextView.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class MyLocationListener implements LocationListener {
        TrackerActivity mainActivity;
        public TrackerActivity getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(TrackerActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            currentLocation = new GPSLocation(1,
                    DATE_FORMAT.format(new Date(loc.getTime())),
                    loc.getLatitude(),
                    loc.getLongitude());

            String Text = "Lat = "+ currentLocation.getLat() + "\n Long = " + currentLocation.getLon();

            latLongTextView.setText(Text);
            this.mainActivity.setLocation(loc);

            registrarcoordenadas();
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            latLongTextView.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            latLongTextView.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    private void registrarcoordenadas(){

        Log.i("New location stored:",  currentLocation.toString() );
        gpsLocationRepository.insert(currentLocation);

    }

    private void registrarparadas(){

        int metadata = 1;

        BusStop newBusStop = new BusStop(
                metadata,
                currentLocation.getTimeStamp(),
                "semaphore", 5,
                6, 12,
                currentLocation.getLat(), currentLocation.getLon(),
                true
        );

        busStopRepository.insert(newBusStop);
        Log.i("New stop bus stored:",  currentLocation.toString() );
    }

}
