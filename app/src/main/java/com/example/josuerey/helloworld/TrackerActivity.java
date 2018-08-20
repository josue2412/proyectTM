package com.example.josuerey.helloworld;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
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
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuerey.helloworld.entidades.BusStop;
import com.example.josuerey.helloworld.entidades.BusStopRepository;
import com.example.josuerey.helloworld.entidades.BusStopViewModel;
import com.example.josuerey.helloworld.entidades.GPSLocation;
import com.example.josuerey.helloworld.entidades.GPSLocationRepository;
import com.example.josuerey.helloworld.entidades.GPSLocationViewModel;
import com.example.josuerey.helloworld.entidades.Metadata;
import com.example.josuerey.helloworld.entidades.MetadataRepository;
import com.example.josuerey.helloworld.entidades.MetadataModel;
import com.travijuu.numberpicker.library.NumberPicker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class TrackerActivity extends AppCompatActivity {

    private TextView latLongTextView;
    private TextView directionsTextView;
    private TextView totalPassengersTextView;
    private Button saveButton;
    private Button finishButton;
    private RadioGroup rgStopType;
    private RadioButton rbStopType;
    private NumberPicker numberPickerUp;
    private NumberPicker numberPickerDown;
    private LocationManager mlocManager;
    private MyLocationListener mlocListener;
    private BusStopViewModel busStopViewModel;
    private MetadataModel metadataModel;
    private GPSLocationViewModel gpsLocationViewModel;
    private List<BusStop> busStopsList;
    private List<GPSLocation> gpsLocationList;
    private int currentMetadataId;
    private String metadata;

    private int totalNumberOfPassengers;
    private GPSLocation currentLocation;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private GPSLocationRepository gpsLocationRepository;
    private BusStopRepository busStopRepository;
    private MetadataRepository metadataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tracker_activity);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            currentMetadataId = Integer.valueOf(extras
                    .getString(MainActivity.METADATA_ID_PROPERTY));
            metadata = extras.getString(MainActivity.METADATA_PROPERTY);
        }

        // Initialize repositories
        gpsLocationRepository = new GPSLocationRepository(getApplication());
        busStopRepository = new BusStopRepository(getApplication());
        metadataRepository = new MetadataRepository(getApplication());

        //Bind layout components
        latLongTextView = findViewById(R.id.latLong);
        directionsTextView = findViewById(R.id.directions);
        totalPassengersTextView = findViewById(R.id.totalPassengers);
        saveButton = findViewById(R.id.btnSave);
        finishButton = findViewById(R.id.btnFinish);
        rgStopType = (RadioGroup) findViewById(R.id.rgStopType);
        rbStopType = (RadioButton) findViewById(rgStopType.getCheckedRadioButtonId());
        rgStopType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbStopType = (RadioButton) findViewById(rgStopType.getCheckedRadioButtonId());
            }
        });

        numberPickerUp = (NumberPicker) findViewById(R.id.number_pickerUP);
        numberPickerDown = (NumberPicker) findViewById(R.id.number_pickerDown);
        totalNumberOfPassengers = 0;

        busStopViewModel = ViewModelProviders.of(this).get(BusStopViewModel.class);
        metadataModel = ViewModelProviders.of(this).get(MetadataModel.class);
        gpsLocationViewModel = ViewModelProviders.of(this).get(GPSLocationViewModel.class);

        busStopsList = new LinkedList<BusStop>();
        busStopViewModel.findBusStopsByMetadata(currentMetadataId).observe(this, new Observer<List<BusStop>>() {
            @Override
            public void onChanged(@Nullable List<BusStop> busStops) {
                busStopsList = busStops;
            }
        });

        metadataModel.findMetadataById(currentMetadataId).observe(this, new Observer<Metadata>() {
            @Override
            public void onChanged(@Nullable Metadata metadata) {
                metadata = metadata;
            }
        });

        gpsLocationList = new LinkedList<GPSLocation>();
        gpsLocationViewModel.findBusStopsByMetadata(currentMetadataId).observe(this, new Observer<List<GPSLocation>>() {
            @Override
            public void onChanged(@Nullable List<GPSLocation> gpsLocations) {
                gpsLocationList =gpsLocations;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarparadas();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportData();

                Intent myIntent = new Intent(TrackerActivity.this, MainActivity.class);
                TrackerActivity.this.startActivity(myIntent);

                finish();

            }
        });

        requestPermissions();
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

            currentLocation = new GPSLocation(currentMetadataId,
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

    private void createFile(String sFileName, String payload) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Backup");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);

            writer.append(payload.toString());

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportData() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String sFileName = "testFile1.txt";

            StringBuilder payload = new StringBuilder();
            Iterator i = busStopsList.iterator();

            while (i.hasNext()){
                payload.append(i.next().toString() + "\n");
            }

            StringBuilder payloadGPSLocations = new StringBuilder();
            Iterator i2 = gpsLocationList.iterator();

            while (i2.hasNext()){
                payloadGPSLocations.append(i2.next().toString() + "\n");
            }

            // Create stopBus fiel
            createFile("Paradas-" + String.valueOf(currentMetadataId) + ".txt",
                    payload.toString());

            // Create Metadata file
            createFile("Recorrido-" + String.valueOf(currentMetadataId) + ".txt",
                    metadata);

            // Create GPSLocations file
            createFile("PuntosGPS-" + String.valueOf(currentMetadataId) + ".txt",
                    payloadGPSLocations.toString());

            Toast.makeText(this, "Archivos guardados", Toast.LENGTH_SHORT).show();

        }
    }

    private void registrarcoordenadas(){

        Log.i("New location stored:",  currentLocation.toString() );
        gpsLocationRepository.insert(currentLocation);

    }

    private void clearAfterSave() {
        numberPickerUp.setValue(0);
        numberPickerDown.setValue(0);
        rgStopType.check(R.id.radiobtnstop);
        totalPassengersTextView.setText(" " + totalNumberOfPassengers + " ");
    }

    private void registrarparadas(){

        totalNumberOfPassengers += numberPickerUp.getValue();
        totalNumberOfPassengers -= numberPickerDown.getValue();
        String stopType = rbStopType.getText().toString();
        boolean isOfficial = stopType.equals("Parada");

        BusStop newBusStop = new BusStop(
                currentMetadataId,
                currentLocation.getTimeStamp(),
                stopType, numberPickerUp.getValue(),
                numberPickerDown.getValue(), totalNumberOfPassengers,
                currentLocation.getLat(), currentLocation.getLon(),
                isOfficial
        );

        busStopRepository.insert(newBusStop);
        Toast.makeText(getApplicationContext(), "Guardado",Toast.LENGTH_SHORT).show();
        clearAfterSave();
        Log.i("New stop bus stored:",  newBusStop.toString() );
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
