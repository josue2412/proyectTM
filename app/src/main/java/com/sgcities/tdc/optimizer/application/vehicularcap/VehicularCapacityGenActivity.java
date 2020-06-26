package com.sgcities.tdc.optimizer.application.vehicularcap;

import com.android.volley.RequestQueue;
import android.media.MediaPlayer;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.application.shared.AssignmentsDisplay;
import com.sgcities.tdc.optimizer.application.shared.StudyType;
import com.sgcities.tdc.optimizer.application.shared.TrackableBaseActivity;
import com.sgcities.tdc.optimizer.domain.movement.Movement;
import com.sgcities.tdc.optimizer.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.sgcities.tdc.optimizer.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.sgcities.tdc.optimizer.infrastructure.network.RemoteStorage;
import com.sgcities.tdc.optimizer.infrastructure.network.VehicularCapAssignmentResponse;
import com.google.gson.Gson;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;

@Getter
public class VehicularCapacityGenActivity extends TrackableBaseActivity implements
        RemoteStorage<VehicularCapacityRecord, VehicularCapacityRecordRepository> {

    private Application appContext;
    private String endpointUrl;
    private String postParamName;

    private static final SimpleDateFormat STD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat USER_DATE_FORMAT = new SimpleDateFormat("h:mm a");
    private final static int INTERVAL_TIME = 60000;
    private Map<Integer, Movement> movementsMap;

    private LinearLayout movementsButtonsLayout;

    private TextView beginningTimeTextView;
    private TextView lastBackupTimeTextView;
    private ImageView intersectionImageView;
    private ImageView soundImageView;
    private boolean isSoundActive;

    private Map<Integer, MovementCounter> movementsCounters;
    private boolean countersChanged;

    private Timer backupCountersTimer;
    private TimerTask timerTask;
    private Date beginTimeInterval;
    private final Handler handler = new Handler();
    private Date endTimeInterval;

    private VehicularCapacityRecordRepository repository;
    private VehicularCapAssignmentResponse assignment;
    public MediaPlayer pedestrianMediaPlayer;
    public MediaPlayer pedestrianWomanMediaPlayer;
    public MediaPlayer carMediaPlayer;
    public MediaPlayer bikeMediaPlayer;
    public MediaPlayer bikeWomanMediaPlayer;
    public MediaPlayer busMediaPlayer;
    public MediaPlayer truckMediaPlayer;
    public MediaPlayer motorcycleMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.vehicular_capacity_gen);

        pedestrianMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.step);
        pedestrianWomanMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.wip);
        carMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.car);
        bikeMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.bike);
        bikeWomanMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.bell);
        busMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.touch);
        truckMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.gout);
        motorcycleMediaPlayer = MediaPlayer.create(VehicularCapacityGenActivity.this, R.raw.arrow);

        appContext = getApplication();
        endpointUrl = "/app/api/persist/vehicCapRecord";
        postParamName = "vehicCapData";

        movementsCounters = new HashMap<>();
        movementsMap = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignment = new Gson().fromJson(
                    extras.getString("vehicCapAssignment"), VehicularCapAssignmentResponse.class);

            for (Movement mov : assignment.getMovements()) { movementsMap.put(mov.getId(), mov); }
            bindViews();
            processMovements();
            requestImage();
            requestPermissions();
        }

        beginningTimeTextView.setText(USER_DATE_FORMAT.format(Calendar.getInstance().getTime()));
        lastBackupTimeTextView.setText(beginningTimeTextView.getText().toString());
        repository = new VehicularCapacityRecordRepository(getApplication());
        retryPostItemsInBatch();
    }


    /**
     * Dynamically creates the buttons counters for each movement given.
     */
    private void processMovements() {
        for (Movement movement : assignment.getMovements()) {
            movementsCounters.put(movement.getId(), inflateMovements(movement));
        }
    }

    private void startTimer() {
        beginTimeInterval = Calendar.getInstance().getTime();
        backupCountersTimer = new Timer();

        Log.d(TAG, String.format("Schedule task started at: %s", STD_DATE_FORMAT.format(beginTimeInterval)));
        initializeTimerTask();

        backupCountersTimer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME);
    }

    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {

                        endTimeInterval = Calendar.getInstance().getTime();
                        if (countersChanged) {
                            generateCountersBackupRecord();
                        } else {
                            Log.d(TAG, "There were no changes, skipping counters backup");
                        }
                        beginTimeInterval = endTimeInterval;
                    }
                );
            }
        };
    }

    /**
     * Takes the current counter values and creates a counter backup record.
     */
    private void generateCountersBackupRecord() {

        List<VehicularCapacityRecord> records = new LinkedList<>();

        for (Integer movementId : movementsCounters.keySet()) {
            MovementCounter movementCounter = movementsCounters.get(movementId);
            VehicularCapacityRecord.VehicularCapacityRecordBuilder recordBuilder;

            if (movementsMap.get(movementId).getMovement_name().equals(StudyType.VEHICULAR.name())) {
                recordBuilder = repository.createVehicularRecord(movementCounter);
            } else {
                recordBuilder = repository.createPedestrianRecord(movementCounter);
            }

            recordBuilder.movementId(movementId).deviceId(deviceId)
                    .assignmentId(this.assignment.getId())
                    .beginTimeInterval(STD_DATE_FORMAT.format(beginTimeInterval))
                    .endTimeInterval(STD_DATE_FORMAT.format(endTimeInterval))
                    .lat(currentLocation != null ? currentLocation.getLat() : 0.0)
                    .lon(currentLocation != null ? currentLocation.getLon(): 0.0)
                    .backedUpRemotely(0);
            VehicularCapacityRecord vehicularCapacityRecord = recordBuilder.build();

            records.add(vehicularCapacityRecord);
            repository.save(vehicularCapacityRecord);
        }

        postItemsInBatch(records);
        lastBackupTimeTextView.setText(USER_DATE_FORMAT.format(endTimeInterval));
        countersChanged = false;
    }

    /**
     * Kills the current timer task who is responsible of backup the counters values
     */
    private void stopTimerTask() {
        if (backupCountersTimer != null) {
            Log.d(TAG, String.format("Stopping timer task %s", backupCountersTimer.toString()));
            if (timerTask != null) {
                timerTask.cancel();
            }
            backupCountersTimer.cancel();
            backupCountersTimer = null;
        }
    }

    /**
     * Creates button counters dynamically based on the typeOfMovement.
     * @param movement
     * @return Map with key vehicle.name - movementId and value {@linkplain CounterStats}
     */
    private MovementCounter inflateMovements(Movement movement) {

        MovementCounter movementCounter = MovementCounter.builder().build();
        LayoutInflater viewInflater = (
                LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vehicularMovementLayout =
                viewInflater.inflate(R.layout.vehicular_movement_layout, null);
        vehicularMovementLayout.setId(movement.getId());

        // Set movement id
        TextView movementIdTextView =
                vehicularMovementLayout.findViewById(R.id.movement_id_text_view);
        movementIdTextView.setText(String.valueOf(movement.getMovement_code()));

        // Put movements counters depending of the type of the study
        LinearLayout movementsCountersLayout =
                vehicularMovementLayout.findViewById(R.id.vehicular_counter_buttons_layout);
        StudyType currentStudyType = StudyType.valueOf(movement.getMovement_name());

        for (UnderStudyVehicles vehicle : UnderStudyVehicles.values()) {
            if (vehicle.getTypeOfStudy().equals(currentStudyType)) {
                movementCounter.getCounterStatusPerVehicle().put(vehicle.name(),
                        CounterStats.builder().build());
                movementsCountersLayout.addView(createCounterView(movement.getId(), vehicle),
                        movementsCountersLayout.getChildCount());
            }
        }

        movementsButtonsLayout.addView(
                vehicularMovementLayout, movementsButtonsLayout.getChildCount());
        return movementCounter;
    }

    /**
     * A counterView is composed by the information of the movement id and the type of vehicle that
     * is going to be under study.
     * @param movementId identifier of the current movement.
     * @param vehicle {@linkplain UnderStudyVehicles}
     * @return
     */
    private View createCounterView(final int movementId, UnderStudyVehicles vehicle) {
        LayoutInflater viewInflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View currentVehicleButtonCounter =
                viewInflater.inflate(R.layout.vehicle_button_counter, null);

        CounterViewTag cTag = CounterViewTag.builder()
                .movementId(movementId).vehicleType(vehicle.name()).build();
        currentVehicleButtonCounter.setId(cTag.hashCode());
        ImageButton buttonImageView =
                currentVehicleButtonCounter.findViewById(R.id.counterImageButton);
        buttonImageView.setTag(cTag);
        buttonImageView.setImageResource(findVehicleImage(vehicle));
        buttonImageView.setOnLongClickListener(onCounterLongClickListener());

        return currentVehicleButtonCounter;
    }

    /**
     * Call when a counter button is pressed.
     */
    public void onCounterClickListener(View buttonView) {
        CounterViewTag counterViewTag = (CounterViewTag) buttonView.getTag();

        updateCounterBadgeView(counterViewTag, movementsCounters
                        .get(counterViewTag.getMovementId())
                        .getCounterStatusPerVehicle()
                        .get(counterViewTag.getVehicleType()).increment(),
                movementsCounters.get(counterViewTag.getMovementId()).getTotal());
        countersChanged = true;
        if (isSoundActive) {
            playVehicleSound(UnderStudyVehicles.valueOf(counterViewTag.getVehicleType()));
        }
    }

    /**
     * Call when a counter button is long pressed
     * @return true if
     */
    private View.OnLongClickListener onCounterLongClickListener() {
        return (View buttonView) -> {
                CounterViewTag counterViewTag = (CounterViewTag) buttonView.getTag();

                updateCounterBadgeView(counterViewTag, movementsCounters
                                .get(counterViewTag.getMovementId())
                                .getCounterStatusPerVehicle()
                                .get(counterViewTag.getVehicleType()).decrement(),
                        movementsCounters.get(counterViewTag.getMovementId()).getTotal());
                return true;
            };
    }

    /**
     * Updates the counter badge that is shown in the display
     *
     * @param counterViewTag identifier of the {@linkplain View} that contains the badge
     * @param currentValue to set in the badge
     */
    private void updateCounterBadgeView(
            CounterViewTag counterViewTag, int currentValue, final int totalValue) {

        View counterToIncrement = findViewById(counterViewTag.hashCode());
        TextView counterBadge = counterToIncrement.findViewById(R.id.counterBadge);
        counterBadge.setText(String.valueOf(currentValue));
        View movementLayout = findViewById(counterViewTag.getMovementId());
        TextView counterTotalTextView =
                movementLayout.findViewById(R.id.movement_counter_text_view);
        counterTotalTextView.setText(String.valueOf(totalValue));
    }

    /**
     * Maps the incoming {@linkplain UnderStudyVehicles} to its respective image resource.
     *
     * @param vehicle {@linkplain UnderStudyVehicles}
     * @return the resource id of the given vehicle
     */
    private int findVehicleImage(UnderStudyVehicles vehicle) {
        switch (vehicle) {
            case BIKE:
                return R.drawable.bike;
            case BUS:
                return R.drawable.bus;
            case CAR:
                return R.drawable.car;
            case MOTORCYCLE:
                return R.drawable.motorcycle;
            case TRUCK:
                return R.drawable.truck;
            case PEDESTRIAN:
                return R.drawable.pedestrian;
            case PEDESTRIAN_FEMALE:
                return R.drawable.pedestrian_female;
            case BIKE_FEMALE:
                return R.drawable.bike_female;
            default:
                return R.drawable.car;
        }
    }


    private void playVehicleSound(UnderStudyVehicles vehicle) {
        switch (vehicle) {
            case BIKE:
                bikeMediaPlayer.start();break;
            case BUS:
                busMediaPlayer.start(); break;
            case CAR:
                carMediaPlayer.start(); break;
            case MOTORCYCLE:
                motorcycleMediaPlayer.start(); break;
            case TRUCK:
                truckMediaPlayer.start(); break;
            case PEDESTRIAN:
                pedestrianMediaPlayer.start(); break;
            case PEDESTRIAN_FEMALE:
                pedestrianWomanMediaPlayer.start(); break;
            case BIKE_FEMALE:
                bikeWomanMediaPlayer.start(); break;
            default:
                carMediaPlayer.start();
        }
    }

    private void bindViews() {
        soundImageView = findViewById(R.id.soundImageView);
        beginningTimeTextView = findViewById(R.id.beginningTimeValueTextView);
        lastBackupTimeTextView = findViewById(R.id.lastBackUpValueTextView);
        intersectionImageView = findViewById(R.id.intersectionImageView);
        movementsButtonsLayout = findViewById(R.id.movementsButtonsLayout);
    }

    /**
     * Handles whether the sound after counter button click must be reproduced or not, as well
     * as the background image.
     * @param target
     */
    public void changeSoundStatus(View target) {

        if (this.isSoundActive) {
            this.isSoundActive = false;
            this.soundImageView.setImageResource(R.drawable.ic_no_sound);
            Toast.makeText(getApplicationContext(), R.string.sound_inactive,Toast.LENGTH_SHORT).show();
        } else {
            this.isSoundActive = true;
            this.soundImageView.setImageResource(R.drawable.ic_sound);
            Toast.makeText(getApplicationContext(), R.string.sound_active,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Call to interrupt current study
     * @param target
     */
    public void interruptStudy(View target) {
        Log.d(TAG, String.format("Study interrupted at: %s",
                STD_DATE_FORMAT.format(Calendar.getInstance().getTime())));
        finish();
    }

    /**
     * Called to send a notification to admin server
     * @param target
     */
    public void emergencyNotification(View target) {
        Log.d(TAG, String.format("Study interrupted by an emergency at: %s",
                STD_DATE_FORMAT.format(Calendar.getInstance().getTime())));

        Intent myIntent = new Intent(VehicularCapacityGenActivity.this,
                EmergencyNotificationActivity.class);
        myIntent.putExtra("assignmentId", String.valueOf(this.assignment.getId()));
        VehicularCapacityGenActivity.this.startActivity(myIntent);
        finish();
    }

    /**
     * Downloads the image attached to the current assignment
     */
    public void requestImage() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String mImageURLString = String.format("%s%s", AssignmentsDisplay.HOST_ASSIGNMENTS_SOURCE,
                assignment.getIntersectionImageURL());

        ImageRequest imageRequest = new ImageRequest(
                mImageURLString, (Bitmap response) -> intersectionImageView.setImageBitmap(response),
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565, (VolleyError error) -> error.printStackTrace()
        );

        requestQueue.add(imageRequest);
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
        startTimer();
        if (requestPermissions())
            locationStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimerTask();
    }
}
