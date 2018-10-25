package com.example.josuerey.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class VehicularCapacityActivity extends AppCompatActivity {
    private int carCounter;
    private int globalCarCounter;
    private EditText carCounterEditText;
    private ImageButton carCounterBtn;

    private int busCounter;
    private int globalBusCounter;
    private EditText busCounterEditText;
    private ImageButton busCounterBtn;

    private int bikeCounter;
    private int globalBikeCounter;
    private EditText bikeCounterEditText;
    private ImageButton bikeCounterBtn;

    private int motorcycleCounter;
    private int globalMotorcycleCounter;
    private EditText motorcycleCounterEditText;
    private ImageButton motorcycleCounterBtn;

    private int pedestrianCounter;
    private int globalPedestrianCounter;
    private EditText pedestrianCounterEditText;
    private ImageButton pedestrianCounterBtn;

    private int truckCounter;
    private int globalTruckCounter;
    private EditText truckCounterEditText;
    private ImageButton truckCounterBtn;

    private TextView lastSavedRecord;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date beginTimeInterval;
    private Date endTimeInterval;

    private String composeId;
    private String android_device_id;
    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private APIClient apiClient;

    private final static int INTERVAL_TIME = 60000;

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
                stopTimerTask();
                Intent myIntent = new Intent(VehicularCapacityActivity.this, HomeActivity.class);
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            composeId = extras.getString("composedId");
        }

        carCounterEditText = (EditText) findViewById(R.id.carCounterEditText);
        carCounterBtn = (ImageButton) findViewById(R.id.carCounterBtn);

        busCounterEditText = (EditText) findViewById(R.id.busCounterEditText);
        busCounterBtn = (ImageButton) findViewById(R.id.busCounterBtn);

        bikeCounterEditText = (EditText) findViewById(R.id.bikeCounterEditText);
        bikeCounterBtn = (ImageButton) findViewById(R.id.bikeCounterBtn);

        motorcycleCounterEditText = (EditText) findViewById(R.id.motorcycleCounterEditText);
        motorcycleCounterBtn = (ImageButton) findViewById(R.id.motorcycleCounterBtn);

        pedestrianCounterEditText = (EditText) findViewById(R.id.pedestrianCounterEditText);
        pedestrianCounterBtn = (ImageButton) findViewById(R.id.pedestrianCounterBtn);

        truckCounterEditText = (EditText) findViewById(R.id.truckCounterEditText);
        truckCounterBtn = (ImageButton) findViewById(R.id.truckCounterBtn);

        lastSavedRecord = (TextView) findViewById(R.id.lastSavedRecord);

        this.bikeCounter = 0;
        this.busCounter = 0;
        this.motorcycleCounter = 0;
        this.pedestrianCounter = 0;
        this.truckCounter = 0;
        this.carCounter = 0;

        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();
        startTimer();
    }

    private void startTimer() {
        beginTimeInterval = Calendar.getInstance().getTime();
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimerTask();
    }

    private void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void generateVehicularCapacityRecord() {

        endTimeInterval = Calendar.getInstance().getTime();
        VehicularCapacityRecord vehicularCapacityRecord = VehicularCapacityRecord.builder()
                .backedUpRemotely(0)
                .deviceId(android_device_id)
                .beginTimeInterval(DATE_FORMAT.format(beginTimeInterval))
                .endTimeInterval(DATE_FORMAT.format(endTimeInterval))
                .numberOfCars(carCounter)
                .numberOfBikes(bikeCounter)
                .numberOfBusses(busCounter)
                .numberOfMotorcycles(motorcycleCounter)
                .numberOfPedestrians(pedestrianCounter)
                .numberOfTrucks(truckCounter)
                .composedId(composeId)
                .build();

        long generatedId = vehicularCapacityRecordRepository.save(vehicularCapacityRecord);
        vehicularCapacityRecord.setId((int) generatedId);

        apiClient.postVehicularCapRecord(Lists.newArrayList(vehicularCapacityRecord),
                vehicularCapacityRecordRepository);

        lastSavedRecord.setText(vehicularCapacityRecord.toString());

        resetCounters();
    }

    private void resetCounters() {
        beginTimeInterval = endTimeInterval;
        this.bikeCounter = 0;
        this.busCounter = 0;
        this.motorcycleCounter = 0;
        this.pedestrianCounter = 0;
        this.truckCounter = 0;
        this.carCounter = 0;

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
                break;
            case R.id.busCounterBtn:
                busCounter = busCounter + 1;
                globalBusCounter = globalBusCounter + 1;
                busCounterEditText.setText(String.valueOf(globalBusCounter));
                break;
            case R.id.motorcycleCounterBtn:
                motorcycleCounter = motorcycleCounter + 1;
                globalMotorcycleCounter = globalMotorcycleCounter + 1;
                motorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                break;
            case R.id.pedestrianCounterBtn:
                pedestrianCounter = pedestrianCounter + 1;
                globalPedestrianCounter = globalPedestrianCounter + 1;
                pedestrianCounterEditText.setText(String.valueOf(globalPedestrianCounter));
                break;
            case R.id.truckCounterBtn:
                truckCounter = truckCounter + 1;
                globalTruckCounter = globalTruckCounter + 1;
                truckCounterEditText.setText(String.valueOf(globalTruckCounter));
                break;
            case R.id.bikeCounterBtn:
                bikeCounter = bikeCounter + 1;
                globalBikeCounter = globalBikeCounter + 1;
                bikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                break;
        }
    }
}
