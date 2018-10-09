package com.example.josuerey.helloworld;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.josuerey.helloworld.domain.busoccupation.BusOccupation;
import com.example.josuerey.helloworld.domain.busoccupation.BusOccupationRepository;
import com.example.josuerey.helloworld.domain.busroute.BusRoute;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.routeviarelationship.RouteViaRelationshipRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.utilidades.ExportData;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VisualOccupationActivity extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String TAG = this.getClass().getSimpleName();
    private List<String> busRoutes;
    private RouteViaRelationshipRepository routeViaRelationshipRepository;

    private int viaOfStudyId;
    private int studyMetadataId;
    private String viaOfStudy;

    private Spinner spinnerRoute;
    private Spinner spinnerRoute2;
    private Spinner spinnerRoute3;

    private Spinner spinnerOccupationLevel;
    private Spinner spinnerOccupationLevel2;
    private Spinner spinnerOccupationLevel3;

    private Spinner spinnerVehicleType;
    private Spinner spinnerVehicleType2;
    private Spinner spinnerVehicleType3;

    private EditText econNumEditText;
    private EditText econNumEditText2;
    private EditText econNumEditText3;

    private BusOccupationRepository busOccupationRepository;
    private APIClient apiClient;
    private GPSLocation currentLocation;
    private MyLocationListener mlocListener;
    private LocationManager mlocManager;

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
                Intent myIntent = new Intent(VisualOccupationActivity.this, HomeActivity.class);
                VisualOccupationActivity.this.startActivity(myIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visual_occupation_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            viaOfStudyId = Integer.valueOf(extras
                    .getString("ViaOfStudyId"));
            studyMetadataId = Integer.valueOf(extras.getString("studyMetadataId"));
            viaOfStudy = extras.getString("ViaOfStudy");
        }

        routeViaRelationshipRepository = new RouteViaRelationshipRepository(getApplication());
        busOccupationRepository = new BusOccupationRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();

        // Autocomplete bus routes with database information
        BusRoute[] existingBusRoutes = routeViaRelationshipRepository.findRoutesByViaOfStudyId(viaOfStudyId);
        busRoutes = new ArrayList<>();
        Log.d(TAG, "Existing bus routes: " + existingBusRoutes.length + " associated with " +
                "via of study id:" + viaOfStudyId);

        for (BusRoute existingBusRoute : existingBusRoutes) {
            busRoutes.add(existingBusRoute.toString());
            Log.d(TAG, existingBusRoute.toString());
        }

        // Populate route spinner with routes associated to point of study
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, busRoutes.toArray(new String[busRoutes.size()]));
        spinnerRoute = (Spinner) findViewById(R.id.routeSpinner);
        spinnerRoute2 = (Spinner) findViewById(R.id.routeSpinner2);
        spinnerRoute3 = (Spinner) findViewById(R.id.routeSpinner3);

        spinnerRoute.setAdapter(adapter);
        spinnerRoute2.setAdapter(adapter);
        spinnerRoute3.setAdapter(adapter);

        spinnerOccupationLevel = (Spinner) findViewById(R.id.occupation_level_spinner);
        spinnerOccupationLevel2 = (Spinner) findViewById(R.id.occupation_level_spinner2);
        spinnerOccupationLevel3 = (Spinner) findViewById(R.id.occupation_level_spinner3);

        spinnerVehicleType = (Spinner) findViewById(R.id.vehicleTypeSpinner);
        spinnerVehicleType2 = (Spinner) findViewById(R.id.vehicleTypeSpinner2);
        spinnerVehicleType3 = (Spinner) findViewById(R.id.vehicleTypeSpinner3);

        econNumEditText = (EditText) findViewById(R.id.econNum_edit_text);
        econNumEditText2 = (EditText) findViewById(R.id.econNum_edit_text2);
        econNumEditText3 = (EditText) findViewById(R.id.econNum_edit_text3);

        requestPermissions();
    }

    public void onSave(View v) {
        BusOccupation.BusOccupationBuilder busOccupationBuilder = BusOccupation.builder();
        int formNumberSaved = 0;

        switch (v.getId()) {
            case R.id.save_button:
                busOccupationBuilder.route(spinnerRoute.getSelectedItem().toString())
                        .occupationLevel(spinnerOccupationLevel.getSelectedItem().toString())
                    .economicNumber(econNumEditText.getText().toString())
                    .busType(spinnerVehicleType.getSelectedItem().toString());
                formNumberSaved = 1;
                break;
            case R.id.save_button2:
                busOccupationBuilder.route(spinnerRoute2.getSelectedItem().toString())
                        .occupationLevel(spinnerOccupationLevel2.getSelectedItem().toString())
                        .economicNumber(econNumEditText2.getText().toString())
                        .busType(spinnerVehicleType2.getSelectedItem().toString());
                formNumberSaved = 2;
                break;
            case R.id.save_button3:
                busOccupationBuilder.route(spinnerRoute3.getSelectedItem().toString())
                        .occupationLevel(spinnerOccupationLevel3.getSelectedItem().toString())
                        .economicNumber(econNumEditText3.getText().toString())
                        .busType(spinnerVehicleType3.getSelectedItem().toString());
                formNumberSaved = 3;
                break;
        }

        busOccupationBuilder.backedUpRemotely(0)
                .timeStamp(DATE_FORMAT.format(Calendar.getInstance().getTime()))
                .studyMetadataId(studyMetadataId);

        if (currentLocation != null){
            busOccupationBuilder.lat(currentLocation.getLat());
            busOccupationBuilder.lon(currentLocation.getLon());
        }

        BusOccupation busOccupation = busOccupationBuilder.build();
        backUpRecord(busOccupation);
        cleanForm(formNumberSaved);
    }

    private void backUpRecord(BusOccupation busOccupationRecord) {
        long busOccupationId = busOccupationRepository.save(busOccupationRecord);
        busOccupationRecord.setId((int) busOccupationId);

        Log.d(TAG, "Saving new busOccupation with id: " + busOccupationId);

        ExportData.createFile(String.format("Ocupacion-visual-%s-%d.txt", viaOfStudy, studyMetadataId),
                busOccupationRecord.toString());

        apiClient.postBusOccupation(Lists.newArrayList(busOccupationRecord), busOccupationRepository);
    }

    private void cleanForm(int formPosition) {
        switch (formPosition) {
            case 1:
                econNumEditText.setText("");
                break;
            case 2:
                econNumEditText2.setText("");
                break;
            case 3:
                econNumEditText3.setText("");
                break;
        }
        Toast.makeText(getApplicationContext(), "Registro guardado",Toast.LENGTH_SHORT).show();
    }

    public class MyLocationListener implements LocationListener {
        VisualOccupationActivity mainActivity;
        public VisualOccupationActivity getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(VisualOccupationActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            currentLocation = GPSLocation.builder()
                    .lat(loc.getLatitude())
                    .lon(loc.getLongitude())
                    .timeStamp(DATE_FORMAT.format(new Date(loc.getTime())))
                    .build();

            String Text = "Lat = "+ currentLocation.getLat() + "\n Long = " + currentLocation.getLon();
            Log.d(TAG, "Location change: " + Text);

        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Log.d(TAG,"GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Log.d(TAG,"GPS Activado");
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
    }

    private void locationStart() {

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new VisualOccupationActivity.MyLocationListener();
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
            Log.d(TAG,"Going back, no permission :(");
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 20,
                (LocationListener) mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 20,
                (LocationListener) mlocListener);
    }

    private void requestPermissions() {

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

}