package com.example.josuerey.helloworld.application.vehicularcap;

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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.application.shared.TrackableBaseActivity;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.infrastructure.network.APIClient;
import com.example.josuerey.helloworld.infrastructure.network.AssignmentResponse;
import com.example.josuerey.helloworld.utilities.MovementConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VehicularCapacityGenActivity extends TrackableBaseActivity {

    private static final SimpleDateFormat STD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat USER_DATE_FORMAT = new SimpleDateFormat("h:mm a");
    private final static int INTERVAL_TIME = 60000;
    private int assignmentId;
    private String intersectionImageURL;
    private List<AssignmentResponse.Movement> movements;
    private Map<Integer, AssignmentResponse.Movement> movementsMap;

    private LinearLayout movementsButtonsLayout;

    private TextView beginningTimeTextView;
    private TextView lastBackupTimeTextView;
    private ImageView intersectionImageView;

    private Map<Integer, MovementCounter> movementsCounters;
    private boolean countersChanged;

    private Timer backupCountersTimer;
    private TimerTask timerTask;
    private Date beginTimeInterval;
    private final Handler handler = new Handler();
    private Date endTimeInterval;

    private APIClient apiClient;
    private VehicularCapacityRecordRepository vehicularCapacityRecordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.vehicular_capacity_gen);
        movementsMap = new HashMap<>();
        movementsCounters = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignmentId = Integer.valueOf(extras.getString("assignmentId"));
            movements = new MovementConverter().toMovementList(extras.getString("movements"));
            for (AssignmentResponse.Movement mov : movements) { movementsMap.put(mov.getId(), mov); }
            intersectionImageURL = extras.getString("intersectionImageURL");
            bindViews();
            processMovements();
            requestImage();
            requestPermissions();
        }

        beginningTimeTextView.setText(USER_DATE_FORMAT.format(Calendar.getInstance().getTime()));
        lastBackupTimeTextView.setText(beginningTimeTextView.getText().toString());
        apiClient = APIClient.builder().app(getApplication()).build();
        vehicularCapacityRecordRepository = new VehicularCapacityRecordRepository(getApplication());
    }


    /**
     * Dynamically creates the buttons counters for each movement given.
     */
    private void processMovements() {
        for (AssignmentResponse.Movement movement : movements) {
            movementsCounters.put(movement.getId(), inflateMovements(movement));
        }
    }

    private void startTimer() {
        beginTimeInterval = Calendar.getInstance().getTime();
        backupCountersTimer = new Timer();

        Log.d(TAG, String.format("Schedule task started at: %s",
                STD_DATE_FORMAT.format(beginTimeInterval)));
        initializeTimerTask();

        backupCountersTimer.schedule(timerTask, INTERVAL_TIME, INTERVAL_TIME);
    }

    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (countersChanged) {
                            generateCountersBackupRecord();
                        } else {
                            Log.d(TAG, "There were no changes, skipping counters backup");
                        }
                    }
                });
            }
        };
    }

    /**
     * Takes the current counter values and creates a counter backup record.
     */
    private void generateCountersBackupRecord() {

        List<VehicularCapacityRecord> records = new LinkedList<>();
        endTimeInterval = Calendar.getInstance().getTime();

        for (Integer movementId : movementsCounters.keySet()) {
            MovementCounter movementCounter = movementsCounters.get(movementId);
            VehicularCapacityRecord.VehicularCapacityRecordBuilder recordBuilder;

            if (movementsMap.get(movementId).getMovement_name()
                    .equals(StudyType.VEHICULAR.name())) {
                recordBuilder =
                        vehicularCapacityRecordRepository.createVehicularRecord(movementCounter);
            } else {
                recordBuilder =
                        vehicularCapacityRecordRepository.createPedestrianRecord(movementCounter);
            }

            recordBuilder.movementId(movementId).deviceId(deviceId)
                    .beginTimeInterval(STD_DATE_FORMAT.format(beginTimeInterval))
                    .endTimeInterval(STD_DATE_FORMAT.format(endTimeInterval))
                    .lat(currentLocation != null ? currentLocation.getLat() : 0.0)
                    .lon(currentLocation != null ? currentLocation.getLon(): 0.0)
                    .backedUpRemotely(0);
            VehicularCapacityRecord vehicularCapacityRecord = recordBuilder.build();

            records.add(vehicularCapacityRecord);
            vehicularCapacityRecordRepository.save(vehicularCapacityRecord);
        }
        apiClient.postVehicularCapRecord(records, vehicularCapacityRecordRepository);
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
    private MovementCounter inflateMovements(AssignmentResponse.Movement movement) {
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

        CounterTag cTag = CounterTag.builder()
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
        CounterTag counterTag = (CounterTag) buttonView.getTag();

        updateCounterBadgeView(counterTag, movementsCounters
                .get(counterTag.getMovementId())
                .getCounterStatusPerVehicle()
                .get(counterTag.getVehicleType()).increment(),
                movementsCounters.get(counterTag.getMovementId()).getTotal());
        countersChanged = true;
    }

    /**
     * Call when a counter button is long pressed
     * @return true if
     */
    private View.OnLongClickListener onCounterLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View buttonView) {
                CounterTag counterTag = (CounterTag) buttonView.getTag();

                updateCounterBadgeView(counterTag, movementsCounters
                        .get(counterTag.getMovementId())
                        .getCounterStatusPerVehicle()
                        .get(counterTag.getVehicleType()).decrement(),
                        movementsCounters.get(counterTag.getMovementId()).getTotal());
                return true;
            }
        };
    }

    /**
     * Updates the counter badge that is shown in the display
     *
     * @param counterTag identifier of the {@linkplain View} that contains the badge
     * @param currentValue to set in the badge
     */
    private void updateCounterBadgeView(
            CounterTag counterTag, int currentValue, final int totalValue) {

        View counterToIncrement = findViewById(counterTag.hashCode());
        TextView counterBadge = counterToIncrement.findViewById(R.id.counterBadge);
        counterBadge.setText(String.valueOf(currentValue));
        View movementLayout = findViewById(counterTag.getMovementId());
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

    private void bindViews() {
        beginningTimeTextView = findViewById(R.id.beginningTimeValueTextView);
        lastBackupTimeTextView = findViewById(R.id.lastBackUpValueTextView);
        intersectionImageView = findViewById(R.id.intersectionImageView);
        movementsButtonsLayout = findViewById(R.id.movementsButtonsLayout);
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
        myIntent.putExtra("assignmentId", String.valueOf(assignmentId));
        VehicularCapacityGenActivity.this.startActivity(myIntent);
        finish();
    }

    /**
     * Downloads the image attached to the current assignment
     */
    public void requestImage() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String mImageURLString = String.format("%s%s", APIClient.SERVER_HOST, intersectionImageURL);

        ImageRequest imageRequest = new ImageRequest(
                mImageURLString,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        intersectionImageView.setImageBitmap(response);
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
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
