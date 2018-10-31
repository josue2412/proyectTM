package com.example.josuerey.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

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

    private int pedestrianCounter;
    private int pedestrianCounter2;
    private int globalPedestrianCounter;
    private EditText pedestrianCounterEditText;
    private ImageButton pedestrianCounterBtn;

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

    private String composeId;
    private String android_device_id;
    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private APIClient apiClient;

    private int numberOfMovements;
    private String[] movements;

    private ImageView mainMove;
    private ImageView secondaryMove;

    private EditText mainMoveEditText;
    private EditText secondaryMoveEditText;

    private TextView spentTimeTextView;
    private TextView beginningTimeTextView;

    private int mainMovementCounter;
    private int secondaryMovementCounter;

    private Date beginningOfTheStudy;

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

        carCounterEditText = (EditText) findViewById(R.id.carCounterEditText);
        busCounterEditText = (EditText) findViewById(R.id.busCounterEditText);
        bikeCounterEditText = (EditText) findViewById(R.id.bikeCounterEditText);
        motorcycleCounterEditText = (EditText) findViewById(R.id.motorcycleCounterEditText);
        pedestrianCounterEditText = (EditText) findViewById(R.id.pedestrianCounterEditText);
        truckCounterEditText = (EditText) findViewById(R.id.truckCounterEditText);

        mainMove = (ImageView) findViewById(R.id.direction);
        secondaryMove = (ImageView) findViewById(R.id.direction2);

        mainMoveEditText = (EditText) findViewById(R.id.mainMovementCounterEditText);
        secondaryMoveEditText = (EditText) findViewById(R.id.secondMovementCounterEditText);
        spentTimeTextView = (TextView) findViewById(R.id.spentTimeTextView);
        beginningTimeTextView = (TextView) findViewById(R.id.beginningTimeTextView);

        beginningOfTheStudy = Calendar.getInstance().getTime();

        beginningTimeTextView.setText(beginningTimeTextView.getText() +
                new SimpleDateFormat("HH:mm:ss").format(beginningOfTheStudy));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            composeId = extras.getString("composedId");
            movements = extras.getString("movements").split(" ");
            numberOfMovements = movements.length;
            manageSecondMove(movements);
        }

        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();
        startTimer();
    }

    private void manageSecondMove(@Nonnull String[] movements) {

        if (movements.length == 2) {
            mainMove.setBackgroundResource(deriveMoveSrc(movements[0]));
            secondaryMove.setBackgroundResource(deriveMoveSrc(movements[1]));
        } else if (movements.length == 1) {
            mainMove.setBackgroundResource(deriveMoveSrc(movements[0]));
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

        pedestrianCounterBtn = (ImageButton) findViewById(R.id.pedestrianCounterBtn2);
        pedestrianCounterBtn.setClickable(false);
        pedestrianCounterBtn.setBackgroundResource(R.color.colorBackground);

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

        Log.d(TAG, "Schedule task started");
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
                .numberOfPedestrians(pedestrianCounter)
                .numberOfTrucks(truckCounter)
                .composedId(composeId)
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
                    .numberOfPedestrians(pedestrianCounter2)
                    .numberOfTrucks(truckCounter2)
                    .composedId(composeId)
                    .build();

            long generatedId2 = vehicularCapacityRecordRepository.save(vehicularCapacityRecord2);
            vehicularCapacityRecord2.setId((int) generatedId2);

            records.add(vehicularCapacityRecord2);
        }

        apiClient.postVehicularCapRecord(records, vehicularCapacityRecordRepository);

        long difference = getDateDiff(beginningOfTheStudy, endTimeInterval, TimeUnit.MINUTES);
        spentTimeTextView.setText("Tiempo de estudio: " + String.valueOf(difference) + " minutos");

        resetCounters();
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private void resetCounters() {
        beginTimeInterval = endTimeInterval;
        this.bikeCounter = 0;
        this.bikeCounter2 = 0;
        this.busCounter = 0;
        this.busCounter2 = 0;
        this.motorcycleCounter = 0;
        this.motorcycleCounter2 = 0;
        this.pedestrianCounter = 0;
        this.pedestrianCounter2 = 0;
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

            case R.id.pedestrianCounterBtn:
                pedestrianCounter = pedestrianCounter + 1;
                globalPedestrianCounter = globalPedestrianCounter + 1;
                pedestrianCounterEditText.setText(String.valueOf(globalPedestrianCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                break;

            case R.id.pedestrianCounterBtn2:
                pedestrianCounter2 = pedestrianCounter2 + 1;
                globalPedestrianCounter = globalPedestrianCounter + 1;
                pedestrianCounterEditText.setText(String.valueOf(globalPedestrianCounter));
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
}
