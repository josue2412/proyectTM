package com.example.josuerey.helloworld.application.vehicularcap;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.application.shared.AssignmentsDisplay;
import com.example.josuerey.helloworld.application.shared.StudyType;
import com.example.josuerey.helloworld.application.shared.TrackableBaseActivity;
import com.example.josuerey.helloworld.domain.movement.Movement;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.infrastructure.network.RemoteStorage;
import com.example.josuerey.helloworld.infrastructure.network.VehicularCapAssignmentResponse;
import com.google.gson.Gson;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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
    private String android_device_id;

    private LinearLayout movementsButtonsLayout;

    private TextView beginningTimeTextView;
    private TextView lastBackupTimeTextView;
    private ImageView intersectionImageView;

    private Map<Integer, Map<String, CounterStats>> counterStatusMap;
    private boolean countersChanged;

    private Timer backupCountersTimer;
    private TimerTask timerTask;
    private Date beginTimeInterval;
    private final Handler handler = new Handler();
    private Date endTimeInterval;

    private VehicularCapacityRecordRepository repository;
    private VehicularCapAssignmentResponse assignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.vechicular_capacity_gen);
        android_device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        appContext = getApplication();
        endpointUrl = "/app/api/persist/vehicCapRecord";
        postParamName = "vehicCapData";

        counterStatusMap = new HashMap<>();
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
    }


    /**
     * Dynamically creates the buttons counters for each movement given.
     */
    private void processMovements() {
        for (Movement movement : assignment.getMovements()) {
            counterStatusMap.put(movement.getId(),
                    inflateMovementLayoutView(movement.getMovement_name(), movement.getId(), movement.getMovement_code()));
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

        for (Integer movementId : counterStatusMap.keySet()) {
            Map<String, CounterStats> vehicles = counterStatusMap.get(movementId);
            VehicularCapacityRecord.VehicularCapacityRecordBuilder recordBuilder;

            if (movementsMap.get(movementId).getMovement_name().equals(StudyType.VEHICULAR.name())) {
                recordBuilder = repository.createVehicularRecord(vehicles);
            } else {
                recordBuilder = repository.createPedestrianRecord(vehicles);
            }

            recordBuilder.movementId(movementId).deviceId(android_device_id)
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
     * TODO : optimize
     * @param movementCode
     */
    private LinearLayout inflateMovementLabelView(String movementCode) {

        LinearLayout movementLinearLayout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        movementLinearLayout.setLayoutParams(params);
        movementLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        WindowManager.LayoutParams viewParams = new WindowManager.LayoutParams();

        TextView movementLabel = new TextView(getApplicationContext());
        movementLabel.setLayoutParams(viewParams);
        movementLabel.setText(movementCode);
        movementLabel.setGravity(Gravity.CENTER);
        movementLabel.setTextSize(24);
        movementLabel.setTypeface(null, Typeface.BOLD_ITALIC);
        movementLinearLayout.addView(movementLabel, movementLinearLayout.getChildCount());
        return movementLinearLayout;
    }


    /**
     * Creates button counters dynamically based on the typeOfMovement.
     *
     * @param typeOfMovement of the type {@linkplain StudyType}
     * @param movementId movement unique id
     * @param movementCode
     *
     * @return Map with key vehicle.name - movementId and value {@linkplain CounterStats}
     */
    private Map<String, CounterStats> inflateMovementLayoutView(
            String typeOfMovement, int movementId, int movementCode) {

        Map<String, CounterStats> counterStatusPerMov = new HashMap<>();
        LayoutInflater counterVehicleInflater = (
                LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Linear layout creation
        LinearLayout movementLinearLayout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        movementLinearLayout.setLayoutParams(params);
        movementLinearLayout.setOrientation(LinearLayout.VERTICAL);

        movementLinearLayout.addView(inflateMovementLabelView(String.valueOf(movementCode)),
                movementLinearLayout.getChildCount());

        StudyType currentStudyType = StudyType.valueOf(typeOfMovement);

        for (UnderStudyVehicles vehicle : UnderStudyVehicles.values()) {

            if (vehicle.getTypeOfStudy().equals(currentStudyType)) {
                View currentVehicleButtonCounter =
                        counterVehicleInflater.inflate(R.layout.vehicular_capacity_button_counter, null);

                CounterViewTag cTag = CounterViewTag.builder()
                        .movementId(movementId).vehicleType(vehicle.name()).build();
                currentVehicleButtonCounter.setId(cTag.toString().hashCode());
                ImageButton buttonImageView =
                        currentVehicleButtonCounter.findViewById(R.id.counterImageButton);
                buttonImageView.setTag(cTag);
                buttonImageView.setImageResource(findVehicleImage(vehicle));
                buttonImageView.setOnLongClickListener(onCounterLongClickListener());
                counterStatusPerMov.put(vehicle.name(), CounterStats.builder()
                        .partialCount(new AtomicInteger()).totalCount(new AtomicInteger()).build());

                movementLinearLayout.addView(
                        currentVehicleButtonCounter, movementLinearLayout.getChildCount());
            }
        }

        movementsButtonsLayout.addView(movementLinearLayout, movementsButtonsLayout.getChildCount());
        return counterStatusPerMov;
    }

    /**
     * Call when a counter button is pressed.
     */
    public void onCounterClickListener(View buttonView) {
        CounterViewTag counterTag = (CounterViewTag) buttonView.getTag();

        updateCounterBadgeView(counterTag.toString(), counterStatusMap
                .get(counterTag.getMovementId())
                .get(counterTag.getVehicleType()).increment());
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
                CounterViewTag counterTag = (CounterViewTag) buttonView.getTag();

                updateCounterBadgeView(counterTag.toString(), counterStatusMap
                        .get(counterTag.getMovementId())
                        .get(counterTag.getVehicleType()).decrement());
                return true;
            }
        };
    }

    /**
     * Updates the counter badge that is shown in the display
     *
     * @param counterTag identifier of the {@linkplain View} that contains the badge
     * @param value to set in the badge
     */
    private void updateCounterBadgeView(String counterTag, int value) {
        View counterToIncrement = findViewById(counterTag.hashCode());
        TextView counterBadge = counterToIncrement.findViewById(R.id.counterBadge);
        counterBadge.setText(String.valueOf(value));
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
        myIntent.putExtra("assignmentId", String.valueOf(this.assignment.getId()));
        VehicularCapacityGenActivity.this.startActivity(myIntent);
        finish();
    }

    /**
     * Downloads the image attached to the current assignment
     */
    public void requestImage() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String mImageURLString = String.format("%s%s", AssignmentsDisplay.serverIp,
                assignment.getPointOfStudy().getIntersectionImageURL());

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
