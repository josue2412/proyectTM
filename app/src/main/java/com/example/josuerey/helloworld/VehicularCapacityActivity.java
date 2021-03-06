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
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuerey.helloworld.domain.assignment.AssignmentRepository;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.example.josuerey.helloworld.utilities.StudyDuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import lombok.Setter;

public class VehicularCapacityActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private int carCounter;
    private int carCounter2;
    private int globalCarCounter;
    private EditText carCounterEditText;
    private ImageButton carCounterBtn;

    private int busCounter;
    private int busCounter2;
    private int globalBusCounter;
    private EditText busCounterEditText;
    private ImageButton busCounterBtn;

    private int bikeCounter;
    private int bikeCounter2;
    private int globalBikeCounter;
    private EditText bikeCounterEditText;
    private ImageButton bikeCounterBtn;

    private int motorcycleCounter;
    private int motorcycleCounter2;
    private int globalMotorcycleCounter;
    private EditText motorcycleCounterEditText;
    private ImageButton motorcycleCounterBtn;

    private int truckCounter;
    private int truckCounter2;
    private int globalTruckCounter;
    private EditText truckCounterEditText;
    private ImageButton truckCounterBtn;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date beginTimeInterval;
    private Date endTimeInterval;

    private int assignmentId;
    private String android_device_id;
    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private AssignmentRepository assignmentRepository;
    private APIClient apiClient;

    private int numberOfMovements;
    private String[] movements;

    private ImageView mainMove;
    private ImageView secondaryMove;

    private EditText mainMoveEditText;
    private EditText secondaryMoveEditText;

    private TextView spentTimeTextView;
    private TextView beginningTimeTextView;
    private TextView movementsTextView;

    private int mainMovementCounter;
    private int secondaryMovementCounter;

    private Date beginningOfTheStudy;
    private String remainingTime;
    private int serverId;

    private final static int INTERVAL_TIME = 60000;

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
                Intent myIntent = new Intent(VehicularCapacityActivity.this, AssignmentsActivity.class);
                VehicularCapacityActivity.this.startActivity(myIntent);
                finish();
                return true;
            case R.id.changeUser:
                SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
                Intent myIntent2 = new Intent(VehicularCapacityActivity.this, LoginActivity.class);
                VehicularCapacityActivity.this.startActivity(myIntent2);
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
        setContentView(R.layout.vehicular_capacity);
        bindViews();

        beginningOfTheStudy = Calendar.getInstance().getTime();
        beginningTimeTextView.setText(new SimpleDateFormat("HH:mm:ss").format(beginningOfTheStudy));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignmentId = Integer.valueOf(extras.getString("assignmentId"));
            movements = extras.getString("movements").split(" ");
            remainingTime = extras.getString("remainingTime");
            serverId = Integer.valueOf(extras.getString("serverId"));
            numberOfMovements = movements.length;
            manageSecondMove(movements);
        }

        spentTimeTextView.setText(remainingTime);
        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        assignmentRepository = new AssignmentRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();

        requestPermissions();
        startTimer();
    }

    private void bindViews() {

        carCounterEditText = (EditText) findViewById(R.id.carCounterEditText);
        busCounterEditText = (EditText) findViewById(R.id.busCounterEditText);
        bikeCounterEditText = (EditText) findViewById(R.id.bikeCounterEditText);
        motorcycleCounterEditText = (EditText) findViewById(R.id.motorcycleCounterEditText);
        truckCounterEditText = (EditText) findViewById(R.id.truckCounterEditText);

        mainMove = (ImageView) findViewById(R.id.direction);
        secondaryMove = (ImageView) findViewById(R.id.direction2);

        mainMoveEditText = (EditText) findViewById(R.id.mainMovementCounterEditText);
        secondaryMoveEditText = (EditText) findViewById(R.id.secondMovementCounterEditText);
        spentTimeTextView = (TextView) findViewById(R.id.spentTimeValueTextView);
        beginningTimeTextView = (TextView) findViewById(R.id.beginningTimeValueTextView);
        movementsTextView = (TextView) findViewById(R.id.movementsValueTextView);

    }

    private void manageSecondMove(@Nonnull String[] movements) {

        if (movements.length == 2) {
            mainMove.setBackgroundResource(deriveMoveSrc(movements[0]));
            secondaryMove.setBackgroundResource(deriveMoveSrc(movements[1]));
            movementsTextView.setText(movements[1] + "/" + movements[0]);
        } else if (movements.length == 1) {
            mainMove.setBackgroundResource(deriveMoveSrc(movements[0]));
            movementsTextView.setText(movements[0]);
            disableSecondMoveButtons();
        }
    }

    private void disableSecondMoveButtons() {
        carCounterBtn = (ImageButton) findViewById(R.id.carCounterBtn2);
        carCounterBtn.setClickable(false);
        carCounterBtn.setBackgroundResource(R.color.colorBackground);

        busCounterBtn = (ImageButton) findViewById(R.id.busCounterBtn2);
        busCounterBtn.setClickable(false);
        busCounterBtn.setBackgroundResource(R.color.colorBackground);

        bikeCounterBtn = (ImageButton) findViewById(R.id.bikeCounterBtn2);
        bikeCounterBtn.setClickable(false);
        bikeCounterBtn.setBackgroundResource(R.color.colorBackground);

        motorcycleCounterBtn = (ImageButton) findViewById(R.id.motorcycleCounterBtn2);
        motorcycleCounterBtn.setClickable(false);
        motorcycleCounterBtn.setBackgroundResource(R.color.colorBackground);

        truckCounterBtn = (ImageButton) findViewById(R.id.truckCounterBtn2);
        truckCounterBtn.setClickable(false);
        truckCounterBtn.setBackgroundResource(R.color.colorBackground);
    }

    private int deriveMoveSrc(@Nonnull String move) {
        switch (move) {
            case "derecho":
                return R.drawable.straight_no_background;
            case "izquierda":
                return R.drawable.turn_left_no_background;
            case "derecha":
                return R.drawable.turn_right_no_background;
            case "retorno":
                return R.drawable.return_no_background;
            default:
                return R.drawable.straight_no_background;
        }
    }

    private void startTimer() {
        beginTimeInterval = Calendar.getInstance().getTime();
        timer = new Timer();

        Log.d(TAG, String.format("Schedule task started at: %s", DATE_FORMAT.format(beginTimeInterval)));
        initializeTimerTask();

        timer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME);
    }

    @Override
    protected void onPause() {
        locationStop();
        super.onPause();
    }

    @Override
    protected void onResume(){

        super.onResume();
        locationStart();
    }

    private void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void generateVehicularCapacityRecord() {

        endTimeInterval = Calendar.getInstance().getTime();

        Log.d(TAG, String.format("Pack vehicular capacity record from %s to %s",
                DATE_FORMAT.format(beginTimeInterval), DATE_FORMAT.format(endTimeInterval)));
        List<VehicularCapacityRecord> records = new LinkedList<>();

        VehicularCapacityRecord vehicularCapacityRecord = VehicularCapacityRecord.builder()
                .backedUpRemotely(0)
                .deviceId(android_device_id)
                .movement(movements[0])
                .beginTimeInterval(DATE_FORMAT.format(beginTimeInterval))
                .endTimeInterval(DATE_FORMAT.format(endTimeInterval))
                .numberOfCars(carCounter)
                .numberOfBikes(bikeCounter)
                .numberOfBusses(busCounter)
                .numberOfMotorcycles(motorcycleCounter)
                .numberOfTrucks(truckCounter)
                .assignmentId(assignmentId)
                .lat(currentLocation.getLat())
                .lon(currentLocation.getLon())
                .build();

        long generatedId = vehicularCapacityRecordRepository.save(vehicularCapacityRecord);
        vehicularCapacityRecord.setId((int) generatedId);

        records.add(vehicularCapacityRecord);

        if (numberOfMovements > 1){
            VehicularCapacityRecord vehicularCapacityRecord2 = VehicularCapacityRecord.builder()
                    .backedUpRemotely(0)
                    .deviceId(android_device_id)
                    .movement(movements[1])
                    .beginTimeInterval(DATE_FORMAT.format(beginTimeInterval))
                    .endTimeInterval(DATE_FORMAT.format(endTimeInterval))
                    .numberOfCars(carCounter2)
                    .numberOfBikes(bikeCounter2)
                    .numberOfBusses(busCounter2)
                    .numberOfMotorcycles(motorcycleCounter2)
                    .numberOfTrucks(truckCounter2)
                    .assignmentId(assignmentId)
                    .lat(currentLocation.getLat())
                    .lon(currentLocation.getLon())
                    .build();

            long generatedId2 = vehicularCapacityRecordRepository.save(vehicularCapacityRecord2);
            vehicularCapacityRecord2.setId((int) generatedId2);

            records.add(vehicularCapacityRecord2);
        }

        apiClient.postVehicularCapRecord(records, vehicularCapacityRecordRepository);

        long difference = StudyDuration.getDateDiff(beginningOfTheStudy, endTimeInterval, TimeUnit.MINUTES);
        String localRemainingTime = StudyDuration.remainingTime((int) difference, remainingTime);
        spentTimeTextView.setText(localRemainingTime);

        resetCounters();
    }

    private void resetCounters() {
        beginTimeInterval = endTimeInterval;
        this.bikeCounter = 0;
        this.bikeCounter2 = 0;
        this.busCounter = 0;
        this.busCounter2 = 0;
        this.motorcycleCounter = 0;
        this.motorcycleCounter2 = 0;
        this.truckCounter = 0;
        this.truckCounter2 = 0;
        this.carCounter = 0;
        this.carCounter2 = 0;

    }

    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        generateVehicularCapacityRecord();
                    }
                });
            }
        };
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.carCounterBtn:
                carCounter = carCounter + 1;
                globalCarCounter = globalCarCounter + 1;
                carCounterEditText.setText(String.valueOf(globalCarCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                break;

            case R.id.carCounterBtn2:
                carCounter2 = carCounter2 + 1;
                globalCarCounter = globalCarCounter + 1;
                carCounterEditText.setText(String.valueOf(globalCarCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                break;

            case R.id.busCounterBtn:
                busCounter = busCounter + 1;
                globalBusCounter = globalBusCounter + 1;
                busCounterEditText.setText(String.valueOf(globalBusCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                break;

            case R.id.busCounterBtn2:
                busCounter2 = busCounter2 + 1;
                globalBusCounter = globalBusCounter + 1;
                busCounterEditText.setText(String.valueOf(globalBusCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                break;

            case R.id.motorcycleCounterBtn:
                motorcycleCounter = motorcycleCounter + 1;
                globalMotorcycleCounter = globalMotorcycleCounter + 1;
                motorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                break;

            case R.id.motorcycleCounterBtn2:
                motorcycleCounter2 = motorcycleCounter2 + 1;
                globalMotorcycleCounter = globalMotorcycleCounter + 1;
                motorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                break;

            case R.id.truckCounterBtn:
                truckCounter = truckCounter + 1;
                globalTruckCounter = globalTruckCounter + 1;
                truckCounterEditText.setText(String.valueOf(globalTruckCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                break;

            case R.id.truckCounterBtn2:
                truckCounter2 = truckCounter2 + 1;
                globalTruckCounter = globalTruckCounter + 1;
                truckCounterEditText.setText(String.valueOf(globalTruckCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                break;

            case R.id.bikeCounterBtn:
                bikeCounter = bikeCounter + 1;
                globalBikeCounter = globalBikeCounter + 1;
                bikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                break;

            case R.id.bikeCounterBtn2:
                bikeCounter2 = bikeCounter2 + 1;
                globalBikeCounter = globalBikeCounter + 1;
                bikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                break;
        }
    }

    public void interruptStudy(View target) {
        Log.d(TAG, String.format("Study interrupted at: %s", DATE_FORMAT.format(Calendar.getInstance().getTime())));
        assignmentRepository.updateAssignmentRemainingTime(spentTimeTextView.getText().toString(), serverId);

        Intent myIntent = new Intent(VehicularCapacityActivity.this, AssignmentsActivity.class);
        VehicularCapacityActivity.this.startActivity(myIntent);
        finish();
    }

    // Location services methods
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

    @Setter
    public class MyLocationListener implements LocationListener {

        private VehicularCapacityActivity vehicularCapacityActivity;

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            currentLocation = GPSLocation.builder()
                    .lat(loc.getLatitude())
                    .lon(loc.getLongitude())
                    .timeStamp(DATE_FORMAT.format(new Date(loc.getTime())))
                    .deviceId(android_device_id)
                    .build();

            Log.d(TAG, String.format("Location change: Lat = %s, Lon= %s", String.valueOf(currentLocation.getLat()),
                    String.valueOf(currentLocation.getLon())));
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplicationContext(), "GPS desactivado",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "GPS activado",Toast.LENGTH_SHORT).show();
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
        mlocListener = new MyLocationListener();
        mlocListener.setVehicularCapacityActivity(this);
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
    }

    private void locationStop() {
        if (mlocManager != null) {
            mlocManager.removeUpdates(mlocListener);
        }
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, String.format("Activity destroyed, stopping location services and" +
                "timer task"));
        locationStop();
        stopTimerTask();
        super.onDestroy();
    }
}
