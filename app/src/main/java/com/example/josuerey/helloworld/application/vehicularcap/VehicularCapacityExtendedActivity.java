package com.example.josuerey.helloworld.application.vehicularcap;

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

import com.example.josuerey.helloworld.application.LoginActivity;
import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.domain.assignment.AssignmentRepository;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.movement.Movement;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.infrastructure.network.APIClient;
import com.example.josuerey.helloworld.infrastructure.preferencesmanagement.SaveSharedPreference;
import com.example.josuerey.helloworld.utilities.MovementConverter;
import com.example.josuerey.helloworld.utilities.StudyDuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import static com.example.josuerey.helloworld.utilities.UiUtils.canDecreaseBadge;

public class VehicularCapacityExtendedActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private ImageButton carCounterBtn1;
    private ImageButton carCounterBtn2;
    private ImageButton carCounterBtn3;
    private ImageButton carCounterBtn4;
    private TextView badgeCar1;
    private TextView badgeCar2;
    private TextView badgeCar3;
    private TextView badgeCar4;

    private ImageButton busCounterBtn1;
    private ImageButton busCounterBtn2;
    private ImageButton busCounterBtn3;
    private ImageButton busCounterBtn4;
    private TextView badgeBus1;
    private TextView badgeBus2;
    private TextView badgeBus3;
    private TextView badgeBus4;

    private ImageButton motorcycleCounterBtn1;
    private ImageButton motorcycleCounterBtn2;
    private ImageButton motorcycleCounterBtn3;
    private ImageButton motorcycleCounterBtn4;
    private TextView badgeMotorcycle1;
    private TextView badgeMotorcycle2;
    private TextView badgeMotorcycle3;
    private TextView badgeMotorcycle4;

    private ImageButton truckCounterBtn1;
    private ImageButton truckCounterBtn2;
    private ImageButton truckCounterBtn3;
    private ImageButton truckCounterBtn4;
    private TextView badgeTruck1;
    private TextView badgeTruck2;
    private TextView badgeTruck3;
    private TextView badgeTruck4;

    private ImageButton bikeCounterBtn1;
    private ImageButton bikeCounterBtn2;
    private ImageButton bikeCounterBtn3;
    private ImageButton bikeCounterBtn4;
    private TextView badgeBike1;
    private TextView badgeBike2;
    private TextView badgeBike3;
    private TextView badgeBike4;

    private EditText globalCarCounterEditText;
    private EditText globalBusCounterEditText;
    private EditText globalMotorcycleCounterEditText;
    private EditText globalTruckCounterEditText;
    private EditText globalBikeCounterEditText;

    private EditText movementCounterEditText4;
    private EditText movementCounterEditText3;
    private EditText movementCounterEditText2;
    private EditText movementCounterEditText1;

    private int movementCounter4;
    private int movementCounter3;
    private int movementCounter2;
    private int movementCounter1;

    private int globalCarCounter;
    private int globalBusCounter;
    private int globalMotorcycleCounter;
    private int globalTruckCounter;
    private int globalBikeCounter;

    private ImageView directionImageView1;
    private ImageView directionImageView2;
    private ImageView directionImageView3;
    private ImageView directionImageView4;

    private Date beginningOfTheStudy;
    private String remainingTime;
    private String spentTime;
    private List<Movement> movements;
    private int assignmentId;
    private int serverId;
    private int numberOfMovements;

    private TextView beginningTimeTextView;
    private TextView movementsTextView;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date beginTimeInterval;
    private Date endTimeInterval;

    private final static int INTERVAL_TIME = 60000;
    private boolean countingChanged;
    private HashMap<String, Integer> countersList;

    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;
    private AssignmentRepository assignmentRepository;
    private String android_device_id;

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

    /**
     * Defines the behavior of the app menu on this activity.
     *
     * @param item clicked item
     * @return true if the event was consumed and handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(getApplicationContext(), "No disponible", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.finishRoute:
                Intent myIntent = new Intent(VehicularCapacityExtendedActivity.this, VehicularCapAssignmentsActivity.class);
                VehicularCapacityExtendedActivity.this.startActivity(myIntent);
                finish();
                return true;
            case R.id.changeUser:
                SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
                Intent myIntent2 = new Intent(VehicularCapacityExtendedActivity.this, LoginActivity.class);
                VehicularCapacityExtendedActivity.this.startActivity(myIntent2);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicular_capacity_extended);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bindViews();

        beginningOfTheStudy = Calendar.getInstance().getTime();
        beginningTimeTextView.setText(new SimpleDateFormat("HH:mm:ss").format(beginningOfTheStudy));

        apiClient = APIClient.builder().app(getApplication()).build();
        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        assignmentRepository = new AssignmentRepository(getApplication());
        countersList = new HashMap<>();
        countersList.put("carCounter1", 0);
        countersList.put("carCounter2", 0);
        countersList.put("carCounter3", 0);
        countersList.put("carCounter4", 0);
        countersList.put("busCounter1", 0);
        countersList.put("busCounter2", 0);
        countersList.put("busCounter3", 0);
        countersList.put("busCounter4", 0);
        countersList.put("motorcycleCounter1", 0);
        countersList.put("motorcycleCounter2", 0);
        countersList.put("motorcycleCounter3", 0);
        countersList.put("motorcycleCounter4", 0);
        countersList.put("truckCounter1", 0);
        countersList.put("truckCounter2", 0);
        countersList.put("truckCounter3", 0);
        countersList.put("truckCounter4", 0);
        countersList.put("bikeCounter1", 0);
        countersList.put("bikeCounter2", 0);
        countersList.put("bikeCounter3", 0);
        countersList.put("bikeCounter4", 0);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignmentId = Integer.valueOf(extras.getString("assignmentId"));
            movements = new MovementConverter().toMovementList(extras.getString("movements"));
            remainingTime = extras.getString("remainingTime");
            serverId = Integer.valueOf(extras.getString("serverId"));
            numberOfMovements = movements.size();
            setMovementsImages(movements);
        }

        spentTime = remainingTime;
        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        requestPermissions();
        startTimer();
    }

    private void startTimer() {
        beginTimeInterval = Calendar.getInstance().getTime();
        timer = new Timer();

        Log.d(TAG, String.format("Schedule task started at: %s", DATE_FORMAT.format(beginTimeInterval)));
        initializeTimerTask();

        timer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME);
    }

    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (countingChanged) {
                            persistVehicularCapacityRecord();
                        }
                    }
                });
            }
        };
    }

    private void stopTimerTask() {
        if (timer != null) {
            Log.d(TAG, String.format("Stopping timer task %s", timer.toString()));
            timer.cancel();
            timer = null;
        }
    }

    /**
     * @param moveNumber
     * @return new instance of VehicularCapacityRecord
     */
    private VehicularCapacityRecord buildVehicularCapacityRecord(int moveNumber) {

        return VehicularCapacityRecord.builder()
                .backedUpRemotely(0)
                .deviceId(android_device_id)
                .beginTimeInterval(DATE_FORMAT.format(beginTimeInterval))
                .endTimeInterval(DATE_FORMAT.format(endTimeInterval))
                .numberOfCars(countersList.get(String.format("carCounter%d", moveNumber)))
                .numberOfBikes(countersList.get(String.format("bikeCounter%d", moveNumber)))
                .numberOfBusses(countersList.get(String.format("busCounter%d", moveNumber)))
                .numberOfMotorcycles(countersList.get(String.format("motorcycleCounter%d", moveNumber)))
                .numberOfTrucks(countersList.get(String.format("truckCounter%d", moveNumber)))
                .movementId(movements.get(moveNumber - 1).getId())
                .lat(currentLocation.getLat())
                .lon(currentLocation.getLon())
                .build();
    }

    /**
     * This method both persist the records related to the current time interval on the device
     * database and on remote database.
     */
    private void persistVehicularCapacityRecord() {

        endTimeInterval = Calendar.getInstance().getTime();

        Log.d(TAG, String.format("Pack vehicular capacity record from %s to %s",
                DATE_FORMAT.format(beginTimeInterval), DATE_FORMAT.format(endTimeInterval)));
        List<VehicularCapacityRecord> records = new LinkedList<>();

        for (int i = 1; i <= numberOfMovements; i++) {

            VehicularCapacityRecord vehicularCapacityRecord =
                    buildVehicularCapacityRecord(i);
            long generatedId = vehicularCapacityRecordRepository.save(vehicularCapacityRecord);
            vehicularCapacityRecord.setId((int) generatedId);

            records.add(vehicularCapacityRecord);
        }

        apiClient.postVehicularCapRecord(records, vehicularCapacityRecordRepository);
        resetCounters();
        calculateRemainingStudyTime();
        countingChanged = false;
        beginTimeInterval = endTimeInterval;

    }

    private void calculateRemainingStudyTime() {
        long difference = StudyDuration.getDateDiff(beginningOfTheStudy, endTimeInterval, TimeUnit.MINUTES);
        spentTime = StudyDuration.remainingTime((int) difference, remainingTime);
    }

    public void interruptStudy(View target) {
        Log.d(TAG, String.format("Study interrupted at: %s", DATE_FORMAT.format(Calendar.getInstance().getTime())));
        assignmentRepository.updateAssignmentRemainingTime(spentTime, serverId);
        finish();
    }

    public void emergencyNotification(View target) {
        Log.d(TAG, String.format("Study interrupted by an emergency at: %s", DATE_FORMAT.format(Calendar.getInstance().getTime())));
        assignmentRepository.updateAssignmentRemainingTime(spentTime, serverId);

        Intent myIntent = new Intent(VehicularCapacityExtendedActivity.this, EmergencyNotificationActivity.class);
        myIntent.putExtra("assignmentId", String.valueOf(assignmentId));
        VehicularCapacityExtendedActivity.this.startActivity(myIntent);
        finish();
    }

    /**
     * Set movements images to ImageViews given the current movements.
     *
     * @param movements
     */
    private void setMovementsImages(List<Movement> movements) {

        String movementsFormatted = String.format("%s %s %s", movements.get(2).getMovement_name(),
                movements.get(1).getMovement_name(), movements.get(0).getMovement_name());
        directionImageView1.setBackgroundResource(deriveMoveSrc(movements.get(0)));
        directionImageView2.setBackgroundResource(deriveMoveSrc(movements.get(1)));
        directionImageView3.setBackgroundResource(deriveMoveSrc(movements.get(2)));

        if (movements.size() > 3) {
            directionImageView4.setBackgroundResource(deriveMoveSrc(movements.get(3)));
            movementsFormatted = String.format("%s %s", movements.get(3).getMovement_name(), movementsFormatted);
        } else {
            disableUnneededMoveButtons();
        }

        movementsTextView.setText(movementsFormatted);
    }

    /**
     * Bind views objects to resource view objects.
     */
    private void bindViews() {
        View.OnLongClickListener decrementCounterOnLongClickListener = longClickListener();

        carCounterBtn1 = findViewById(R.id.carCounterBtn1);
        carCounterBtn2 = findViewById(R.id.carCounterBtn2);
        carCounterBtn3 = findViewById(R.id.carCounterBtn3);
        carCounterBtn4 = findViewById(R.id.carCounterBtn4);
        busCounterBtn1 = findViewById(R.id.busCounterBtn1);
        busCounterBtn2 = findViewById(R.id.busCounterBtn2);
        busCounterBtn3 = findViewById(R.id.busCounterBtn3);
        busCounterBtn4 = findViewById(R.id.busCounterBtn4);
        motorcycleCounterBtn1 = findViewById(R.id.motorcycleCounterBtn1);
        motorcycleCounterBtn2 = findViewById(R.id.motorcycleCounterBtn2);
        motorcycleCounterBtn3 = findViewById(R.id.motorcycleCounterBtn3);
        motorcycleCounterBtn4 = findViewById(R.id.motorcycleCounterBtn4);
        truckCounterBtn1 = findViewById(R.id.truckCounterBtn1);
        truckCounterBtn2 = (ImageButton) findViewById(R.id.truckCounterBtn2);
        truckCounterBtn3 = (ImageButton) findViewById(R.id.truckCounterBtn3);
        truckCounterBtn4 = (ImageButton) findViewById(R.id.truckCounterBtn4);
        bikeCounterBtn1 = (ImageButton) findViewById(R.id.bikeCounterBtn1);
        bikeCounterBtn2 = (ImageButton) findViewById(R.id.bikeCounterBtn2);
        bikeCounterBtn3 = (ImageButton) findViewById(R.id.bikeCounterBtn3);
        bikeCounterBtn4 = (ImageButton) findViewById(R.id.bikeCounterBtn4);

        globalCarCounterEditText = (EditText) findViewById(R.id.carCounterEditText);
        globalBusCounterEditText = (EditText) findViewById(R.id.busCounterEditText);
        globalMotorcycleCounterEditText = (EditText) findViewById(R.id.motorcycleCounterEditText);
        globalTruckCounterEditText = (EditText) findViewById(R.id.truckCounterEditText);
        globalBikeCounterEditText = (EditText) findViewById(R.id.bikeCounterEditText);

        movementCounterEditText4 = (EditText) findViewById(R.id.movementCounterEditText4);
        movementCounterEditText3 = (EditText) findViewById(R.id.movementCounterEditText3);
        movementCounterEditText2 = (EditText) findViewById(R.id.movementCounterEditText2);
        movementCounterEditText1 = (EditText) findViewById(R.id.movementCounterEditText1);

        carCounterBtn1.setOnLongClickListener(decrementCounterOnLongClickListener);
        carCounterBtn2.setOnLongClickListener(decrementCounterOnLongClickListener);
        carCounterBtn3.setOnLongClickListener(decrementCounterOnLongClickListener);
        carCounterBtn4.setOnLongClickListener(decrementCounterOnLongClickListener);
        busCounterBtn1.setOnLongClickListener(decrementCounterOnLongClickListener);
        busCounterBtn2.setOnLongClickListener(decrementCounterOnLongClickListener);
        busCounterBtn3.setOnLongClickListener(decrementCounterOnLongClickListener);
        busCounterBtn4.setOnLongClickListener(decrementCounterOnLongClickListener);
        motorcycleCounterBtn1.setOnLongClickListener(decrementCounterOnLongClickListener);
        motorcycleCounterBtn2.setOnLongClickListener(decrementCounterOnLongClickListener);
        motorcycleCounterBtn3.setOnLongClickListener(decrementCounterOnLongClickListener);
        motorcycleCounterBtn4.setOnLongClickListener(decrementCounterOnLongClickListener);
        truckCounterBtn1.setOnLongClickListener(decrementCounterOnLongClickListener);
        truckCounterBtn2.setOnLongClickListener(decrementCounterOnLongClickListener);
        truckCounterBtn3.setOnLongClickListener(decrementCounterOnLongClickListener);
        truckCounterBtn4.setOnLongClickListener(decrementCounterOnLongClickListener);
        bikeCounterBtn1.setOnLongClickListener(decrementCounterOnLongClickListener);
        bikeCounterBtn2.setOnLongClickListener(decrementCounterOnLongClickListener);
        bikeCounterBtn3.setOnLongClickListener(decrementCounterOnLongClickListener);
        bikeCounterBtn4.setOnLongClickListener(decrementCounterOnLongClickListener);

        directionImageView1 = (ImageView) findViewById(R.id.direction1);
        directionImageView2 = (ImageView) findViewById(R.id.direction2);
        directionImageView3 = (ImageView) findViewById(R.id.direction3);
        directionImageView4 = (ImageView) findViewById(R.id.direction4);

        beginningTimeTextView = (TextView) findViewById(R.id.beginningTimeValueTextView);
        movementsTextView = (TextView) findViewById(R.id.movementsValueTextView);

        badgeCar1 = findViewById(R.id.badge_car_1);
        badgeCar2 = findViewById(R.id.badge_car_2);
        badgeCar3 = findViewById(R.id.badge_car_3);
        badgeCar4 = findViewById(R.id.badge_car_4);

        badgeBus1 = findViewById(R.id.badge_bus_1);
        badgeBus2 = findViewById(R.id.badge_bus_2);
        badgeBus3 = findViewById(R.id.badge_bus_3);
        badgeBus4 = findViewById(R.id.badge_bus_4);

        badgeMotorcycle1 = findViewById(R.id.badge_motorcycle_1);
        badgeMotorcycle2 = findViewById(R.id.badge_motorcycle_2);
        badgeMotorcycle3 = findViewById(R.id.badge_motorcycle_3);
        badgeMotorcycle4 = findViewById(R.id.badge_motorcycle_4);

        badgeTruck1 = findViewById(R.id.badge_truck_1);
        badgeTruck2 = findViewById(R.id.badge_truck_2);
        badgeTruck3 = findViewById(R.id.badge_truck_3);
        badgeTruck4 = findViewById(R.id.badge_truck_4);

        badgeBike1 = findViewById(R.id.badge_bike_1);
        badgeBike2 = findViewById(R.id.badge_bike_2);
        badgeBike3 = findViewById(R.id.badge_bike_3);
        badgeBike4 = findViewById(R.id.badge_bike_4);

    }

    /**
     * After a counting interval is closed, the counters must be restarted.
     */
    private void resetCounters() {
        countersList.put("carCounter1", 0);
        countersList.put("carCounter2", 0);
        countersList.put("carCounter3", 0);
        countersList.put("carCounter4", 0);
        countersList.put("busCounter1", 0);
        countersList.put("busCounter2", 0);
        countersList.put("busCounter3", 0);
        countersList.put("busCounter4", 0);
        countersList.put("motorcycleCounter1", 0);
        countersList.put("motorcycleCounter2", 0);
        countersList.put("motorcycleCounter3", 0);
        countersList.put("motorcycleCounter4", 0);
        countersList.put("truckCounter1", 0);
        countersList.put("truckCounter2", 0);
        countersList.put("truckCounter3", 0);
        countersList.put("truckCounter4", 0);
        countersList.put("bikeCounter1", 0);
        countersList.put("bikeCounter2", 0);
        countersList.put("bikeCounter3", 0);
        countersList.put("bikeCounter4", 0);
    }

    private void updateBadgeCount(TextView badge, int value) {
        int currentCount = Integer.parseInt(badge.getText().toString());
        badge.setText(String.valueOf(currentCount + value));
    }

    /**
     * This method either increments or decrements the counter associated to the given view.
     *
     * @param view      view.
     * @param increment if true, increments the counter, otherwise decrements.
     */
    public void updateCounters(View view, boolean increment) {
        int incrementValue = (increment) ? 1 : -1;
        String vehicle = "";
        switch (view.getId()) {
            case R.id.carCounterBtn1:
                if (!increment) {
                    if (!canDecreaseBadge(badgeCar1)) {
                        return;
                    }
                }
                countersList.put("carCounter1", countersList.get("carCounter1") + incrementValue);
                globalCarCounter = globalCarCounter + incrementValue;
                globalCarCounterEditText.setText(String.valueOf(globalCarCounter));
                movementCounter1 = movementCounter1 + incrementValue;
                movementCounterEditText1.setText(String.valueOf(movementCounter1));
                vehicle = "carro";
                updateBadgeCount(badgeCar1, incrementValue);
                break;

            case R.id.carCounterBtn2:
                if (!increment) {
                    if (!canDecreaseBadge(badgeCar2)) {
                        return;
                    }
                }
                countersList.put("carCounter2", countersList.get("carCounter2") + incrementValue);
                globalCarCounter = globalCarCounter + incrementValue;
                globalCarCounterEditText.setText(String.valueOf(globalCarCounter));
                movementCounter2 = movementCounter2 + incrementValue;
                movementCounterEditText2.setText(String.valueOf(movementCounter2));
                vehicle = "carro";
                updateBadgeCount(badgeCar2, incrementValue);
                break;

            case R.id.carCounterBtn3:
                if (!increment) {
                    if (!canDecreaseBadge(badgeCar3)) {
                        return;
                    }
                }
                countersList.put("carCounter3", countersList.get("carCounter3") + incrementValue);
                globalCarCounter = globalCarCounter + incrementValue;
                globalCarCounterEditText.setText(String.valueOf(globalCarCounter));
                movementCounter3 = movementCounter3 + incrementValue;
                movementCounterEditText3.setText(String.valueOf(movementCounter3));
                vehicle = "carro";
                updateBadgeCount(badgeCar3, incrementValue);
                break;

            case R.id.carCounterBtn4:
                if (!increment) {
                    if (!canDecreaseBadge(badgeCar4)) {
                        return;
                    }
                }
                countersList.put("carCounter4", countersList.get("carCounter4") + incrementValue);
                globalCarCounter = globalCarCounter + incrementValue;
                globalCarCounterEditText.setText(String.valueOf(globalCarCounter));
                movementCounter4 = movementCounter4 + incrementValue;
                movementCounterEditText4.setText(String.valueOf(movementCounter4));
                vehicle = "carro";
                updateBadgeCount(badgeCar4, incrementValue);
                break;

            case R.id.busCounterBtn1:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBus1)) {
                        return;
                    }
                }
                countersList.put("busCounter1", countersList.get("busCounter1") + incrementValue);
                globalBusCounter = globalBusCounter + incrementValue;
                globalBusCounterEditText.setText(String.valueOf(globalBusCounter));
                movementCounter1 = movementCounter1 + incrementValue;
                movementCounterEditText1.setText(String.valueOf(movementCounter1));
                vehicle = "autobus";
                updateBadgeCount(badgeBus1, incrementValue);
                break;

            case R.id.busCounterBtn2:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBus2)) {
                        return;
                    }
                }
                countersList.put("busCounter2", countersList.get("busCounter2") + incrementValue);
                globalBusCounter = globalBusCounter + incrementValue;
                globalBusCounterEditText.setText(String.valueOf(globalBusCounter));
                movementCounter2 = movementCounter2 + incrementValue;
                movementCounterEditText2.setText(String.valueOf(movementCounter2));
                vehicle = "autobus";
                updateBadgeCount(badgeBus2, incrementValue);
                break;

            case R.id.busCounterBtn3:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBus3)) {
                        return;
                    }
                }
                countersList.put("busCounter3", countersList.get("busCounter3") + incrementValue);
                globalBusCounter = globalBusCounter + incrementValue;
                globalBusCounterEditText.setText(String.valueOf(globalBusCounter));
                movementCounter3 = movementCounter3 + incrementValue;
                movementCounterEditText3.setText(String.valueOf(movementCounter3));
                vehicle = "autobus";
                updateBadgeCount(badgeBus3, incrementValue);
                break;

            case R.id.busCounterBtn4:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBus4)) {
                        return;
                    }
                }
                countersList.put("busCounter4", countersList.get("busCounter4") + incrementValue);
                globalBusCounter = globalBusCounter + incrementValue;
                globalBusCounterEditText.setText(String.valueOf(globalBusCounter));
                movementCounter4 = movementCounter4 + incrementValue;
                movementCounterEditText4.setText(String.valueOf(movementCounter4));
                vehicle = "autobus";
                updateBadgeCount(badgeBus4, incrementValue);
                break;

            case R.id.motorcycleCounterBtn1:
                if (!increment) {
                    if (!canDecreaseBadge(badgeMotorcycle1)) {
                        return;
                    }
                }
                countersList.put("motorcycleCounter1", countersList.get("motorcycleCounter1") + incrementValue);
                globalMotorcycleCounter = globalMotorcycleCounter + incrementValue;
                globalMotorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                movementCounter1 = movementCounter1 + incrementValue;
                movementCounterEditText1.setText(String.valueOf(movementCounter1));
                vehicle = "motocicleta";
                updateBadgeCount(badgeMotorcycle1, incrementValue);
                break;

            case R.id.motorcycleCounterBtn2:
                if (!increment) {
                    if (!canDecreaseBadge(badgeMotorcycle2)) {
                        return;
                    }
                }
                countersList.put("motorcycleCounter2", countersList.get("motorcycleCounter2") + incrementValue);
                globalMotorcycleCounter = globalMotorcycleCounter + incrementValue;
                globalMotorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                movementCounter2 = movementCounter2 + incrementValue;
                movementCounterEditText2.setText(String.valueOf(movementCounter2));
                vehicle = "motocicleta";
                updateBadgeCount(badgeMotorcycle2, incrementValue);
                break;

            case R.id.motorcycleCounterBtn3:
                if (!increment) {
                    if (!canDecreaseBadge(badgeMotorcycle3)) {
                        return;
                    }
                }
                countersList.put("motorcycleCounter3", countersList.get("motorcycleCounter3") + incrementValue);
                globalMotorcycleCounter = globalMotorcycleCounter + incrementValue;
                globalMotorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                movementCounter3 = movementCounter3 + incrementValue;
                movementCounterEditText3.setText(String.valueOf(movementCounter3));
                vehicle = "motocicleta";
                updateBadgeCount(badgeMotorcycle3, incrementValue);
                break;

            case R.id.motorcycleCounterBtn4:
                if (!increment) {
                    if (!canDecreaseBadge(badgeMotorcycle4)) {
                        return;
                    }
                }
                countersList.put("motorcycleCounter4", countersList.get("motorcycleCounter4") + incrementValue);
                globalMotorcycleCounter = globalMotorcycleCounter + incrementValue;
                globalMotorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                movementCounter4 = movementCounter4 + incrementValue;
                movementCounterEditText4.setText(String.valueOf(movementCounter4));
                vehicle = "motocicleta";
                updateBadgeCount(badgeMotorcycle4, incrementValue);
                break;

            case R.id.truckCounterBtn1:
                if (!increment) {
                    if (!canDecreaseBadge(badgeTruck1)) {
                        return;
                    }
                }
                countersList.put("truckCounter1", countersList.get("truckCounter1") + incrementValue);
                globalTruckCounter = globalTruckCounter + incrementValue;
                globalTruckCounterEditText.setText(String.valueOf(globalTruckCounter));
                movementCounter1 = movementCounter1 + incrementValue;
                movementCounterEditText1.setText(String.valueOf(movementCounter1));
                vehicle = "camion";
                updateBadgeCount(badgeTruck1, incrementValue);
                break;

            case R.id.truckCounterBtn2:
                if (!increment) {
                    if (!canDecreaseBadge(badgeTruck2)) {
                        return;
                    }
                }
                countersList.put("truckCounter2", countersList.get("truckCounter2") + incrementValue);
                globalTruckCounter = globalTruckCounter + incrementValue;
                globalTruckCounterEditText.setText(String.valueOf(globalTruckCounter));
                movementCounter2 = movementCounter2 + incrementValue;
                movementCounterEditText2.setText(String.valueOf(movementCounter2));
                vehicle = "camion";
                updateBadgeCount(badgeTruck2, incrementValue);
                break;

            case R.id.truckCounterBtn3:
                if (!increment) {
                    if (!canDecreaseBadge(badgeTruck3)) {
                        return;
                    }
                }
                countersList.put("truckCounter3", countersList.get("truckCounter3") + incrementValue);
                globalTruckCounter = globalTruckCounter + incrementValue;
                globalTruckCounterEditText.setText(String.valueOf(globalTruckCounter));
                movementCounter3 = movementCounter3 + incrementValue;
                movementCounterEditText3.setText(String.valueOf(movementCounter3));
                vehicle = "camion";
                updateBadgeCount(badgeTruck3, incrementValue);
                break;

            case R.id.truckCounterBtn4:
                if (!increment) {
                    if (!canDecreaseBadge(badgeTruck4)) {
                        return;
                    }
                }
                countersList.put("truckCounter4", countersList.get("truckCounter4") + incrementValue);
                globalTruckCounter = globalTruckCounter + incrementValue;
                globalTruckCounterEditText.setText(String.valueOf(globalTruckCounter));
                movementCounter4 = movementCounter4 + incrementValue;
                movementCounterEditText4.setText(String.valueOf(movementCounter4));
                vehicle = "camion";
                updateBadgeCount(badgeTruck4, incrementValue);
                break;

            case R.id.bikeCounterBtn1:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBike1)) {
                        return;
                    }
                }
                countersList.put("bikeCounter1", countersList.get("bikeCounter1") + incrementValue);
                globalBikeCounter = globalBikeCounter + incrementValue;
                globalBikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                movementCounter1 = movementCounter1 + incrementValue;
                movementCounterEditText1.setText(String.valueOf(movementCounter1));
                vehicle = "bicicleta";
                updateBadgeCount(badgeBike1, incrementValue);
                break;

            case R.id.bikeCounterBtn2:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBike2)) {
                        return;
                    }
                }
                countersList.put("bikeCounter2", countersList.get("bikeCounter2") + incrementValue);
                globalBikeCounter = globalBikeCounter + incrementValue;
                globalBikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                movementCounter2 = movementCounter2 + incrementValue;
                movementCounterEditText2.setText(String.valueOf(movementCounter2));
                vehicle = "bicicleta";
                updateBadgeCount(badgeBike2, incrementValue);
                break;

            case R.id.bikeCounterBtn3:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBike3)) {
                        return;
                    }
                }
                countersList.put("bikeCounter3", countersList.get("bikeCounter3") + incrementValue);
                globalBikeCounter = globalBikeCounter + incrementValue;
                globalBikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                movementCounter3 = movementCounter3 + incrementValue;
                movementCounterEditText3.setText(String.valueOf(movementCounter3));
                vehicle = "bicicleta";
                updateBadgeCount(badgeBike3, incrementValue);
                break;

            case R.id.bikeCounterBtn4:
                if (!increment) {
                    if (!canDecreaseBadge(badgeBike4)) {
                        return;
                    }
                }
                countersList.put("bikeCounter4", countersList.get("bikeCounter4") + incrementValue);
                globalBikeCounter = globalBikeCounter + incrementValue;
                globalBikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                movementCounter4 = movementCounter4 + incrementValue;
                movementCounterEditText4.setText(String.valueOf(movementCounter4));
                vehicle = "bicicleta";
                updateBadgeCount(badgeBike4, incrementValue);
                break;
        }
        if (!increment) {
            Toast.makeText(getApplicationContext(), String.format("-1 %s", vehicle), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Increments counter of the view that was clicked.
     *
     * @param view associated with click event
     */
    public void onClick(View view) {
        if (view.isClickable()) {
            updateCounters(view, true);
            countingChanged = true;
        }
    }

    /**
     * Defines the OnLongClick event.
     *
     * @return true if the OnLongClick event is consumed.
     */
    private View.OnLongClickListener longClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                updateCounters(view, false);
                return true;
            }
        };
    }

    /**
     * Disable set of buttons when the number of movement does not required all of them.
     */
    private void disableUnneededMoveButtons() {
        carCounterBtn4.setClickable(false);
        carCounterBtn4.setBackgroundResource(R.color.colorBackground);
        carCounterBtn4.setImageResource(R.color.colorBackground);

        busCounterBtn4.setClickable(false);
        busCounterBtn4.setImageResource(R.color.colorBackground);
        busCounterBtn4.setBackgroundResource(R.color.colorBackground);

        bikeCounterBtn4.setClickable(false);
        bikeCounterBtn4.setImageResource(R.color.colorBackground);
        bikeCounterBtn4.setBackgroundResource(R.color.colorBackground);

        motorcycleCounterBtn4.setClickable(false);
        motorcycleCounterBtn4.setBackgroundResource(R.color.colorBackground);
        motorcycleCounterBtn4.setImageResource(R.color.colorBackground);

        truckCounterBtn4.setClickable(false);
        truckCounterBtn4.setImageResource(R.color.colorBackground);
        truckCounterBtn4.setBackgroundResource(R.color.colorBackground);
    }

    private int deriveMoveSrc(@Nonnull Movement movement) {
        String move = movement.getMovement_name().toLowerCase();
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

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            currentLocation = GPSLocation.builder()
                    .lat(loc.getLatitude())
                    .lon(loc.getLongitude())
                    .timeStamp(DATE_FORMAT.format(new Date(loc.getTime())))
                    .deviceId(android_device_id)
                    .build();

            Log.d(TAG, String.format("Location change: Lat = %s, Lon= %s, Ts = %s",
                    String.valueOf(currentLocation.getLat()),
                    String.valueOf(currentLocation.getLon()),
                    currentLocation.getTimeStamp()));
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplicationContext(), "GPS desactivado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "GPS activado", Toast.LENGTH_SHORT).show();
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

    private boolean requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
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

    private void locationStart() {
        Log.d(TAG, "Starting location updates");
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
            Log.d(TAG, "Going back, no permission :(");
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 20,
                (LocationListener) mlocListener);
    }

    private void locationStop() {
        if (mlocManager != null) {
            mlocManager.removeUpdates(mlocListener);
            Log.d(TAG, String.format("Stopping location updates %s", mlocManager.toString()));
            mlocManager = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestPermissions())
            locationStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestPermissions())
            locationStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationStop();
        stopTimerTask();
    }
}
