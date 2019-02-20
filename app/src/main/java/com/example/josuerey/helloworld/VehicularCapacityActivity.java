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
import android.widget.Button;
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
import com.example.josuerey.helloworld.network.AssignmentResponse;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.example.josuerey.helloworld.utilities.MovementConverter;
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

import static com.example.josuerey.helloworld.utilities.UiUtils.canDecreaseBadge;

public class VehicularCapacityActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private int carCounter1;
    private int carCounter2;
    private int globalCarCounter;
    private EditText carCounterEditText;
    private ImageButton carCounterBtn1;
    private ImageButton carCounterBtn2;
    private TextView badgeCar1;
    private TextView badgeCar2;

    private int busCounter1;
    private int busCounter2;
    private int globalBusCounter;
    private EditText busCounterEditText;
    private ImageButton busCounterBtn1;
    private ImageButton busCounterBtn2;
    private TextView badgeBus1;
    private TextView badgeBus2;

    private int bikeCounter1;
    private int bikeCounter2;
    private int globalBikeCounter;
    private EditText bikeCounterEditText;
    private ImageButton bikeCounterBtn1;
    private ImageButton bikeCounterBtn2;
    private TextView badgeBike1;
    private TextView badgeBike2;

    private int motorcycleCounter1;
    private int motorcycleCounter2;
    private int globalMotorcycleCounter;
    private EditText motorcycleCounterEditText;
    private ImageButton motorcycleCounterBtn1;
    private ImageButton motorcycleCounterBtn2;
    private TextView badgeMotorcycle1;
    private TextView badgeMotorcycle2;

    private int truckCounter1;
    private int truckCounter2;
    private int globalTruckCounter;
    private EditText truckCounterEditText;
    private ImageButton truckCounterBtn1;
    private ImageButton truckCounterBtn2;
    private TextView badgeTruck1;
    private TextView badgeTruck2;


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
    private List<AssignmentResponse.Movement> movements;

    private ImageView mainMove;
    private ImageView secondaryMove;

    private EditText mainMoveEditText;
    private EditText secondaryMoveEditText;

    private String spentTime;
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
    private Button emergencyBtn;
    private boolean countingChanged;

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
                Toast.makeText(getApplicationContext(), "No disponible", Toast.LENGTH_SHORT).show();
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
            movements = new MovementConverter().toMovementList(extras.getString("movements"));
            remainingTime = extras.getString("remainingTime");
            serverId = Integer.valueOf(extras.getString("serverId"));
            numberOfMovements = movements.size();
            manageSecondMove(movements);
        }

        spentTime = remainingTime;
        android_device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
        assignmentRepository = new AssignmentRepository(getApplication());
        apiClient = APIClient.builder().app(getApplication()).build();

        requestPermissions();
        startTimer();
    }

    private void bindViews() {
        View.OnLongClickListener onLongClickListener = longClickListener();

        carCounterBtn1 = (ImageButton) findViewById(R.id.carCounterBtn);
        carCounterBtn1.setOnLongClickListener(onLongClickListener);
        busCounterBtn1 = (ImageButton) findViewById(R.id.busCounterBtn);
        busCounterBtn1.setOnLongClickListener(onLongClickListener);
        bikeCounterBtn1 = (ImageButton) findViewById(R.id.bikeCounterBtn);
        bikeCounterBtn1.setOnLongClickListener(onLongClickListener);
        motorcycleCounterBtn1 = (ImageButton) findViewById(R.id.motorcycleCounterBtn);
        motorcycleCounterBtn1.setOnLongClickListener(onLongClickListener);
        truckCounterBtn1 = (ImageButton) findViewById(R.id.truckCounterBtn);
        truckCounterBtn1.setOnLongClickListener(onLongClickListener);

        carCounterBtn2 = (ImageButton) findViewById(R.id.carCounterBtn2);
        carCounterBtn2.setOnLongClickListener(onLongClickListener);
        busCounterBtn2 = (ImageButton) findViewById(R.id.busCounterBtn2);
        busCounterBtn2.setOnLongClickListener(onLongClickListener);
        bikeCounterBtn2 = (ImageButton) findViewById(R.id.bikeCounterBtn2);
        bikeCounterBtn2.setOnLongClickListener(onLongClickListener);
        motorcycleCounterBtn2 = (ImageButton) findViewById(R.id.motorcycleCounterBtn2);
        motorcycleCounterBtn2.setOnLongClickListener(onLongClickListener);
        truckCounterBtn2 = (ImageButton) findViewById(R.id.truckCounterBtn2);
        truckCounterBtn2.setOnLongClickListener(onLongClickListener);

        carCounterEditText = (EditText) findViewById(R.id.carCounterEditText);
        busCounterEditText = (EditText) findViewById(R.id.busCounterEditText);
        bikeCounterEditText = (EditText) findViewById(R.id.bikeCounterEditText);
        motorcycleCounterEditText = (EditText) findViewById(R.id.motorcycleCounterEditText);
        truckCounterEditText = (EditText) findViewById(R.id.truckCounterEditText);

        mainMove = (ImageView) findViewById(R.id.direction);
        secondaryMove = (ImageView) findViewById(R.id.direction2);

        mainMoveEditText = (EditText) findViewById(R.id.mainMovementCounterEditText);
        secondaryMoveEditText = (EditText) findViewById(R.id.secondMovementCounterEditText);
        beginningTimeTextView = (TextView) findViewById(R.id.beginningTimeValueTextView);
        movementsTextView = (TextView) findViewById(R.id.movementsValueTextView);

        emergencyBtn = (Button) findViewById(R.id.emergencyBtn);


        badgeCar1 = findViewById(R.id.badge_car_1);
        badgeCar2 = findViewById(R.id.badge_car_2);
        badgeBus1 = findViewById(R.id.badge_bus_1);
        badgeBus2 = findViewById(R.id.badge_bus_2);
        badgeMotorcycle1 = findViewById(R.id.badge_motorcycle_1);
        badgeMotorcycle2 = findViewById(R.id.badge_motorcycle_2);
        badgeTruck1 = findViewById(R.id.badge_truck_1);
        badgeTruck2 = findViewById(R.id.badge_truck_2);
        badgeBike1 = findViewById(R.id.badge_bike_1);
        badgeBike2 = findViewById(R.id.badge_bike_2);
    }

    private View.OnLongClickListener longClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String vehicle = "";
                switch (v.getId()) {
                    case R.id.carCounterBtn:
                        if (!canDecreaseBadge(badgeCar1)) return true;

                        carCounter1 = carCounter1 - 1;
                        decreaseBadgeCounter(badgeCar1);
                        globalCarCounter = globalCarCounter - 1;
                        carCounterEditText.setText(String.valueOf(globalCarCounter));
                        mainMovementCounter = mainMovementCounter - 1;
                        mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                        vehicle = "carro";
                        break;

                    case R.id.carCounterBtn2:
                        if (!canDecreaseBadge(badgeCar2)) return true;
                        carCounter2 = carCounter2 - 1;
                        decreaseBadgeCounter(badgeCar2);
                        globalCarCounter = globalCarCounter - 1;
                        carCounterEditText.setText(String.valueOf(globalCarCounter));
                        secondaryMovementCounter = secondaryMovementCounter - 1;
                        secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                        vehicle = "carro";
                        break;

                    case R.id.busCounterBtn:
                        if (!canDecreaseBadge(badgeBus1)) return true;
                        busCounter1 = busCounter1 - 1;
                        decreaseBadgeCounter(badgeBus1);
                        globalBusCounter = globalBusCounter - 1;
                        busCounterEditText.setText(String.valueOf(globalBusCounter));
                        mainMovementCounter = mainMovementCounter - 1;
                        mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                        vehicle = "autobus";
                        break;

                    case R.id.busCounterBtn2:
                        if (!canDecreaseBadge(badgeBus2)) return true;
                        busCounter2 = busCounter2 - 1;
                        decreaseBadgeCounter(badgeBus2);
                        globalBusCounter = globalBusCounter - 1;
                        busCounterEditText.setText(String.valueOf(globalBusCounter));
                        secondaryMovementCounter = secondaryMovementCounter - 1;
                        secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                        vehicle = "autobus";
                        break;

                    case R.id.motorcycleCounterBtn:
                        if (!canDecreaseBadge(badgeMotorcycle1)) return true;
                        motorcycleCounter1 = motorcycleCounter1 - 1;
                        decreaseBadgeCounter(badgeMotorcycle1);
                        globalMotorcycleCounter = globalMotorcycleCounter - 1;
                        motorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                        mainMovementCounter = mainMovementCounter - 1;
                        mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                        vehicle = "motocicleta";
                        break;

                    case R.id.motorcycleCounterBtn2:
                        if (!canDecreaseBadge(badgeMotorcycle2)) return true;
                        motorcycleCounter2 = motorcycleCounter2 - 1;
                        decreaseBadgeCounter(badgeMotorcycle2);
                        globalMotorcycleCounter = globalMotorcycleCounter - 1;
                        motorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                        secondaryMovementCounter = secondaryMovementCounter - 1;
                        secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                        vehicle = "motocicleta";
                        break;

                    case R.id.truckCounterBtn:
                        if (!canDecreaseBadge(badgeTruck1)) return true;
                        truckCounter1 = truckCounter1 - 1;
                        decreaseBadgeCounter(badgeTruck1);
                        globalTruckCounter = globalTruckCounter - 1;
                        truckCounterEditText.setText(String.valueOf(globalTruckCounter));
                        mainMovementCounter = mainMovementCounter - 1;
                        mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                        vehicle = "camion";
                        break;

                    case R.id.truckCounterBtn2:
                        if (!canDecreaseBadge(badgeTruck2)) return true;
                        truckCounter2 = truckCounter2 - 1;
                        decreaseBadgeCounter(badgeTruck2);
                        globalTruckCounter = globalTruckCounter - 1;
                        truckCounterEditText.setText(String.valueOf(globalTruckCounter));
                        secondaryMovementCounter = secondaryMovementCounter - 1;
                        secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                        vehicle = "camion";
                        break;

                    case R.id.bikeCounterBtn:
                        if (!canDecreaseBadge(badgeBike1)) return true;
                        bikeCounter1 = bikeCounter1 - 1;
                        decreaseBadgeCounter(badgeBike1);
                        globalBikeCounter = globalBikeCounter - 1;
                        bikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                        mainMovementCounter = mainMovementCounter - 1;
                        mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                        vehicle = "bicicleta";
                        break;

                    case R.id.bikeCounterBtn2:
                        if (!canDecreaseBadge(badgeBike2)) return true;
                        bikeCounter2 = bikeCounter2 - 1;
                        decreaseBadgeCounter(badgeBike2);
                        globalBikeCounter = globalBikeCounter - 1;
                        bikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                        secondaryMovementCounter = secondaryMovementCounter - 1;
                        secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                        vehicle = "bicicleta";
                        break;
                }
                Toast.makeText(getApplicationContext(), String.format("-1 %s", vehicle), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
                ;
    }

    private void manageSecondMove(@Nonnull List<AssignmentResponse.Movement> movements) {

        if (movements.size() == 2) {
            mainMove.setBackgroundResource(deriveMoveSrc(movements.get(0)));
            secondaryMove.setBackgroundResource(deriveMoveSrc(movements.get(1)));
            movementsTextView.setText(movements.get(1).getMovement_name() + "/" + movements.get(0).getMovement_name());
        } else if (movements.size() == 1) {
            mainMove.setBackgroundResource(deriveMoveSrc(movements.get(0)));
            movementsTextView.setText(movements.get(0).getMovement_name());
            disableSecondMoveButtons();
        }
    }

    private void disableSecondMoveButtons() {
        carCounterBtn2.setClickable(false);
        carCounterBtn2.setBackgroundResource(R.color.colorBackground);

        busCounterBtn2.setClickable(false);
        busCounterBtn2.setBackgroundResource(R.color.colorBackground);

        bikeCounterBtn2.setClickable(false);
        bikeCounterBtn2.setBackgroundResource(R.color.colorBackground);

        motorcycleCounterBtn2.setClickable(false);
        motorcycleCounterBtn2.setBackgroundResource(R.color.colorBackground);

        truckCounterBtn2.setClickable(false);
        truckCounterBtn2.setBackgroundResource(R.color.colorBackground);
    }

    private int deriveMoveSrc(@Nonnull AssignmentResponse.Movement movement) {
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

    private void startTimer() {
        beginTimeInterval = Calendar.getInstance().getTime();
        timer = new Timer();

        Log.d(TAG, String.format("Schedule task started at: %s", DATE_FORMAT.format(beginTimeInterval)));
        initializeTimerTask();

        timer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME);
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

    private void stopTimerTask() {
        if (timer != null) {
            Log.d(TAG, String.format("Stopping timer task %s", timer.toString()));
            if (timerTask != null) {
                timerTask.cancel();
            }
            timer.cancel();
            timer = null;
        }
    }

    private void generateVehicularCapacityRecord() {

        endTimeInterval = Calendar.getInstance().getTime();

        if (countingChanged) {
            Log.d(TAG, String.format("Pack vehicular capacity record from %s to %s",
                    DATE_FORMAT.format(beginTimeInterval), DATE_FORMAT.format(endTimeInterval)));
            List<VehicularCapacityRecord> records = new LinkedList<>();

            VehicularCapacityRecord vehicularCapacityRecord = VehicularCapacityRecord.builder()
                    .backedUpRemotely(0)
                    .deviceId(android_device_id)
                    .beginTimeInterval(DATE_FORMAT.format(beginTimeInterval))
                    .endTimeInterval(DATE_FORMAT.format(endTimeInterval))
                    .numberOfCars(carCounter1)
                    .numberOfBikes(bikeCounter1)
                    .numberOfBusses(busCounter1)
                    .numberOfMotorcycles(motorcycleCounter1)
                    .numberOfTrucks(truckCounter1)
                    .movementId(movements.get(0).getId())
                    .lat(currentLocation != null ? currentLocation.getLat() : 0.0)
                    .lon(currentLocation != null ? currentLocation.getLon(): 0.0)
                    .build();

            long generatedId = vehicularCapacityRecordRepository.save(vehicularCapacityRecord);
            vehicularCapacityRecord.setId((int) generatedId);

            records.add(vehicularCapacityRecord);

            if (numberOfMovements > 1) {
                VehicularCapacityRecord vehicularCapacityRecord2 = VehicularCapacityRecord.builder()
                        .backedUpRemotely(0)
                        .deviceId(android_device_id)
                        .beginTimeInterval(DATE_FORMAT.format(beginTimeInterval))
                        .endTimeInterval(DATE_FORMAT.format(endTimeInterval))
                        .numberOfCars(carCounter2)
                        .numberOfBikes(bikeCounter2)
                        .numberOfBusses(busCounter2)
                        .numberOfMotorcycles(motorcycleCounter2)
                        .numberOfTrucks(truckCounter2)
                        .movementId(movements.get(1).getId())
                        .lat(currentLocation != null ? currentLocation.getLat() : 0.0)
                        .lon(currentLocation != null ? currentLocation.getLon(): 0.0)
                        .build();

                long generatedId2 = vehicularCapacityRecordRepository.save(vehicularCapacityRecord2);
                vehicularCapacityRecord2.setId((int) generatedId2);

                records.add(vehicularCapacityRecord2);
            }

            apiClient.postVehicularCapRecord(records, vehicularCapacityRecordRepository);
            resetCounters();
            countingChanged = false;
        } else {
            Log.d(TAG, String.format("There were no changes, transmission delay to %s",
                    DATE_FORMAT.format(endTimeInterval)));
        }

        long difference = StudyDuration.getDateDiff(beginningOfTheStudy, endTimeInterval, TimeUnit.MINUTES);
        String localRemainingTime = StudyDuration.remainingTime((int) difference, remainingTime);
        spentTime = localRemainingTime;
    }

    private void increaseBadgeCounter(TextView badge) {
        int badgeCounter = Integer.parseInt(badge.getText().toString());
        badge.setText(String.valueOf(badgeCounter + 1));
    }

    private void decreaseBadgeCounter(TextView badge) {
        int badgeCounter = Integer.parseInt(badge.getText().toString());
        badge.setText(String.valueOf(badgeCounter - 1));
    }

    private void resetCounters() {
        beginTimeInterval = endTimeInterval;
        this.bikeCounter1 = 0;
        this.bikeCounter2 = 0;
        this.busCounter1 = 0;
        this.busCounter2 = 0;
        this.motorcycleCounter1 = 0;
        this.motorcycleCounter2 = 0;
        this.truckCounter1 = 0;
        this.truckCounter2 = 0;
        this.carCounter1 = 0;
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
                carCounter1 = carCounter1 + 1;
                globalCarCounter = globalCarCounter + 1;
                carCounterEditText.setText(String.valueOf(globalCarCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                increaseBadgeCounter(badgeCar1);
                break;

            case R.id.carCounterBtn2:
                carCounter2 = carCounter2 + 1;
                globalCarCounter = globalCarCounter + 1;
                carCounterEditText.setText(String.valueOf(globalCarCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                increaseBadgeCounter(badgeCar2);
                break;

            case R.id.busCounterBtn:
                busCounter1 = busCounter1 + 1;
                globalBusCounter = globalBusCounter + 1;
                busCounterEditText.setText(String.valueOf(globalBusCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                increaseBadgeCounter(badgeBus1);
                break;

            case R.id.busCounterBtn2:
                busCounter2 = busCounter2 + 1;
                globalBusCounter = globalBusCounter + 1;
                busCounterEditText.setText(String.valueOf(globalBusCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                increaseBadgeCounter(badgeBus2);
                break;

            case R.id.motorcycleCounterBtn:
                motorcycleCounter1 = motorcycleCounter1 + 1;
                globalMotorcycleCounter = globalMotorcycleCounter + 1;
                motorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                increaseBadgeCounter(badgeMotorcycle1);
                break;

            case R.id.motorcycleCounterBtn2:
                motorcycleCounter2 = motorcycleCounter2 + 1;
                globalMotorcycleCounter = globalMotorcycleCounter + 1;
                motorcycleCounterEditText.setText(String.valueOf(globalMotorcycleCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                increaseBadgeCounter(badgeMotorcycle2);
                break;

            case R.id.truckCounterBtn:
                truckCounter1 = truckCounter1 + 1;
                globalTruckCounter = globalTruckCounter + 1;
                truckCounterEditText.setText(String.valueOf(globalTruckCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                increaseBadgeCounter(badgeTruck1);
                break;

            case R.id.truckCounterBtn2:
                truckCounter2 = truckCounter2 + 1;
                globalTruckCounter = globalTruckCounter + 1;
                truckCounterEditText.setText(String.valueOf(globalTruckCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                increaseBadgeCounter(badgeTruck2);
                break;

            case R.id.bikeCounterBtn:
                bikeCounter1 = bikeCounter1 + 1;
                globalBikeCounter = globalBikeCounter + 1;
                bikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                mainMovementCounter = mainMovementCounter + 1;
                mainMoveEditText.setText(String.valueOf(mainMovementCounter));
                increaseBadgeCounter(badgeBike1);
                break;

            case R.id.bikeCounterBtn2:
                bikeCounter2 = bikeCounter2 + 1;
                globalBikeCounter = globalBikeCounter + 1;
                bikeCounterEditText.setText(String.valueOf(globalBikeCounter));
                secondaryMovementCounter = secondaryMovementCounter + 1;
                secondaryMoveEditText.setText(String.valueOf(secondaryMovementCounter));
                increaseBadgeCounter(badgeBike2);
                break;
        }
        countingChanged = true;
    }

    public void interruptStudy(View target) {
        Log.d(TAG, String.format("Study interrupted at: %s", DATE_FORMAT.format(Calendar.getInstance().getTime())));
        assignmentRepository.updateAssignmentRemainingTime(spentTime, serverId);

        finish();
    }

    public void emergencyNotification(View target) {
        Log.d(TAG, String.format("Study interrupted by an emergency at: %s", DATE_FORMAT.format(Calendar.getInstance().getTime())));
        assignmentRepository.updateAssignmentRemainingTime(spentTime, serverId);

        Intent myIntent = new Intent(VehicularCapacityActivity.this, EmergencyNotificationActivity.class);
        myIntent.putExtra("assignmentId", String.valueOf(assignmentId));
        VehicularCapacityActivity.this.startActivity(myIntent);
        finish();
    }

    // Location services methods
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

    private void locationStart() {
        Log.d(TAG, "Starting location updates");
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
            Log.d(TAG, "Going back, no permission :(");
            return;
        }

//        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 20,
//                (LocationListener) mlocListener);
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
    protected void onDestroy() {
        super.onDestroy();
        locationStop();
        stopTimerTask();
    }
}
