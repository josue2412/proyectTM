package com.example.josuerey.helloworld;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuerey.helloworld.domain.busstop.BusStop;
import com.example.josuerey.helloworld.domain.busstop.BusStopRepository;
import com.example.josuerey.helloworld.domain.busstop.BusStopViewModel;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocationRepository;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocationViewModel;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.utilidades.ExportData;
import com.google.common.collect.Lists;
import com.travijuu.numberpicker.library.NumberPicker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

public class TrackerActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private TextView latLongTextView;
    private TextView directionsTextView;
    private TextView totalPassengersTextView;
    private Button saveButton;
    private Button beginStopButton;
    private RadioGroup rgStopType;
    private RadioButton rbStopType;
    private NumberPicker numberPickerUp;
    private NumberPicker numberPickerDown;
    private LocationManager mlocManager;
    private MyLocationListener mlocListener;
    private BusStopViewModel busStopViewModel;
    private GPSLocationViewModel gpsLocationViewModel;
    private List<BusStop> busStopsList;
    private List<GPSLocation> gpsLocationList;
    private int currentMetadataId;
    private Date beginStopInstant;

    private int totalNumberOfPassengers;
    private GPSLocation currentLocation;
    private String composedId;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private GPSLocationRepository gpsLocationRepository;
    private BusStopRepository busStopRepository;

    private APIClient apiClient;

    private String route;
    private String econNumber;
    private String android_device_id;

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
                mlocManager.removeUpdates(mlocListener);
                apiClient.postGpsLocationInBatch(gpsLocationList, gpsLocationRepository);
                Intent myIntent = new Intent(TrackerActivity.this, HomeActivity.class);
                TrackerActivity.this.startActivity(myIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tracker_activity);

        apiClient = APIClient.builder().app(getApplication()).build();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            currentMetadataId = Integer.valueOf(extras
                    .getString(TrackerFormActivity.METADATA_ID_PROPERTY));
            route = extras.getString("Route");
            econNumber = extras.getString("econNumber");
            totalNumberOfPassengers = Integer.valueOf(extras.getString("initialPassengers"));
            composedId = extras.getString("composedId");
        }

        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // Initialize repositories
        gpsLocationRepository = new GPSLocationRepository(getApplication());
        busStopRepository = new BusStopRepository(getApplication());

        //Bind layout components
        latLongTextView = findViewById(R.id.latLong);
        directionsTextView = findViewById(R.id.directions);
        totalPassengersTextView = findViewById(R.id.totalPassengers);
        saveButton = findViewById(R.id.btnSave);
        beginStopButton = findViewById(R.id.btnStopBegin);
        rgStopType = (RadioGroup) findViewById(R.id.rgStopType);
        rbStopType = (RadioButton) findViewById(rgStopType.getCheckedRadioButtonId());
        rgStopType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbStopType = (RadioButton) findViewById(rgStopType.getCheckedRadioButtonId());
            }
        });

        totalPassengersTextView.setText(" Total pasajeros: " + totalNumberOfPassengers + " ");

        numberPickerUp = (NumberPicker) findViewById(R.id.number_pickerUP);
        numberPickerDown = (NumberPicker) findViewById(R.id.number_pickerDown);

        busStopViewModel = ViewModelProviders.of(this).get(BusStopViewModel.class);
        gpsLocationViewModel = ViewModelProviders.of(this).get(GPSLocationViewModel.class);

        busStopsList = new LinkedList<BusStop>();
        busStopViewModel.findBusStopsByMetadata(currentMetadataId).observe(this, new Observer<List<BusStop>>() {
            @Override
            public void onChanged(@Nullable List<BusStop> busStops) {
                busStopsList = busStops;
            }
        });

        gpsLocationList = new LinkedList<GPSLocation>();
        gpsLocationViewModel.findGPSLocationsByMetadataId(currentMetadataId).observe(this, new Observer<List<GPSLocation>>() {
            @Override
            public void onChanged(@Nullable List<GPSLocation> gpsLocations) {
                gpsLocationList = gpsLocations;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                persistBusStop();
                disableControls();
                beginStopButton.setEnabled(true);
            }
        });

        beginStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                beginStopInstant = Calendar.getInstance().getTime();
                enableControls();
                beginStopButton.setEnabled(false);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        disableControls();
        requestPermissions();
    }

    private void disableControls(){
        numberPickerDown.setEnabled(false);
        numberPickerUp.setEnabled(false);
        rgStopType.setEnabled(false);
        saveButton.setEnabled(false);

        for(int i = 0; i < rgStopType.getChildCount(); i++){
            ((RadioButton)rgStopType.getChildAt(i)).setEnabled(false);
        }
    }

    private void enableControls(){
        numberPickerDown.setEnabled(true);
        numberPickerUp.setEnabled(true);
        rgStopType.setEnabled(true);
        saveButton.setEnabled(true);

        for(int i = 0; i < rgStopType.getChildCount(); i++){
            ((RadioButton)rgStopType.getChildAt(i)).setEnabled(true);
        }
    }

    private void requestPermissions() {
        // Request external file write permission
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSIONS, 112);

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
            Log.d(TAG,"Going back, no permission :(");
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 20,
                (LocationListener) mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 20,
                (LocationListener) mlocListener);
        latLongTextView.setText("Localizacion agregada");
        directionsTextView.setText("");
    }

    public void updateLocationOnView(Location loc) {
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

            currentLocation = GPSLocation.builder()
                    .idMetadata(currentMetadataId)
                    .lat(loc.getLatitude())
                    .lon(loc.getLongitude())
                    .timeStamp(DATE_FORMAT.format(new Date(loc.getTime())))
                    .deviceId(android_device_id)
                    .backedUpRemotely(0)
                    .composedId(composedId)
                    .build();

            String Text = "Lat = "+ currentLocation.getLat() + "\n Long = " + currentLocation.getLon();

            latLongTextView.setText(Text);
            this.mainActivity.updateLocationOnView(loc);

            persistGPSFix();
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

    private void persistGPSFix(){

        Log.d(TAG, "New location stored:" + currentLocation.toString() );
        long gpsLocationId = gpsLocationRepository.insert(currentLocation);
        currentLocation.setId((int)gpsLocationId);

        ExportData.createFile(route + "-" + econNumber + "-PuntosGPS-"
                        + String.valueOf(currentMetadataId) + ".txt", currentLocation.toString());

    }

    private void persistBusStop(){

        totalNumberOfPassengers += numberPickerUp.getValue();
        totalNumberOfPassengers -= numberPickerDown.getValue();
        String stopType = rbStopType.getText().toString();
        boolean isOfficial = stopType.equals("Parada");

        BusStop newBusStop = BusStop.builder()
                .stopType(stopType)
                .idMetadata(currentMetadataId)
                .deviceId(android_device_id)
                .isOfficial(isOfficial)
                .lat(currentLocation.getLat())
                .lon(currentLocation.getLon())
                .passengersDown(numberPickerDown.getValue())
                .passengersUp(numberPickerUp.getValue())
                .totalPassengers(totalNumberOfPassengers)
                .stopBegin(DATE_FORMAT.format(beginStopInstant))
                .stopEnd(DATE_FORMAT.format(new Date()))
                .composedId(composedId)
                .backedUpRemotely(0)
                .build();

        int id = (int)busStopRepository.insert(newBusStop);
        newBusStop.setId(id);
        apiClient.postBusStopInBatch(Lists.newArrayList(newBusStop), busStopRepository);

        // Create BusStop file
        ExportData.createFile(route + "-" + econNumber + "-Paradas-"
                + String.valueOf(currentMetadataId) + ".txt", newBusStop.toString());

        Toast.makeText(getApplicationContext(), "Guardado",Toast.LENGTH_SHORT).show();
        clearAfterSave();
        Log.d(TAG, "New busStop stored: " + newBusStop.toString() );
    }

    private void clearAfterSave() {

        numberPickerUp.setValue(0);
        numberPickerDown.setValue(0);
        rgStopType.check(R.id.radiobtnOficialStop);
        totalPassengersTextView.setText(" Total pasajeros: " + totalNumberOfPassengers + " ");
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
}
