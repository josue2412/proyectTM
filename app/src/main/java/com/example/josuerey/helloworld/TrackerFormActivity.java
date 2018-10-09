package com.example.josuerey.helloworld;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.domain.busstop.BusStop;
import com.example.josuerey.helloworld.domain.busstop.BusStopRepository;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocationRepository;
import com.example.josuerey.helloworld.domain.metadata.Metadata;
import com.example.josuerey.helloworld.domain.metadata.MetadataRepository;
import com.example.josuerey.helloworld.network.APIClient;
import com.example.josuerey.helloworld.utilidades.ExportData;

import org.json.JSONException;
import org.json.JSONObject;

public class TrackerFormActivity extends AppCompatActivity {

    public final static String METADATA_ID_PROPERTY = "metadataId";
    public final static String METADATA_PROPERTY = "metadata";
    private EditText campoNoRuta;
    private EditText campoVia;
    private EditText campoNumEcon;
    private EditText campoEncuestador;
    private MetadataRepository metadataRepository;
    private GPSLocationRepository gpsLocationRepository;
    private BusStopRepository busStopRepository;
    private int metadataId;
    private final String TAG = this.getClass().getSimpleName();
    private APIClient apiClient;
    private String androidDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_form_activity);

        androidDeviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.d(TAG, "Unique id: " + androidDeviceId);

        campoNoRuta = (EditText) findViewById(R.id.editTextRuta);
        campoVia = (EditText) findViewById(R.id.editTextVia);
        campoNumEcon = (EditText) findViewById(R.id.editTextNumEcon);
        campoEncuestador = (EditText) findViewById(R.id.editTextEnc);

        metadataRepository = new MetadataRepository(getApplication());
        gpsLocationRepository = new GPSLocationRepository(getApplication());
        busStopRepository = new BusStopRepository(getApplication());

        apiClient = APIClient.builder().app(getApplication()).build();

        checkForDataPendingToBackUp();
    }

    private void checkForDataPendingToBackUp() {

        Metadata[] metadataRecords = metadataRepository.findMetadataByBackedUpRemotely(0);
        Log.d(TAG, "Metadata records pending to backup: " + metadataRecords.length);
        for (Metadata record : metadataRecords) {
            Log.d(TAG, record.toString());
        }

        BusStop[] busStopsRecords = busStopRepository.findBusStopByBackedUpRemotely(0);
        Log.d(TAG, "BusStop records pending to backup: " + busStopsRecords.length);
        for (BusStop record : busStopsRecords) {
            Log.d(TAG, record.toString());
        }

        GPSLocation[] gpsLocationsRecords = gpsLocationRepository.findGPSLocationByBackedUpRemotely(0);
        Log.d(TAG, "GPSLocation records pending to backup: " + gpsLocationsRecords.length);
        for (GPSLocation record : gpsLocationsRecords) {
            Log.d(TAG, record.toString());
        }
    }

    public void onClick(View view){

        Intent miIntent = null;
        switch (view.getId()){

            case R.id.btnStart:
                    if (fieldsValidateSuccess()) {
                        Metadata metadata = saveMetadata();
                        Log.d(TAG, metadata.toString());
                        apiClient.postMetadata(metadata, metadataRepository);

                        Intent myIntent = new Intent(TrackerFormActivity.this, TrackerActivity.class);
                        myIntent.putExtra(METADATA_ID_PROPERTY, String.valueOf(metadataId));
                        myIntent.putExtra(METADATA_PROPERTY, metadata.toString());
                        myIntent.putExtra("Route", metadata.getRoute());
                        myIntent.putExtra("econNumber", metadata.getEconomicNumber());
                        TrackerFormActivity.this.startActivity(myIntent);
                        finish();
                    }
                break;
        }
        if (miIntent!=null){
            startActivity(miIntent);
        }
    }

    private boolean fieldsValidateSuccess() {

        if (TextUtils.isEmpty(campoNoRuta.getText().toString())) {
            campoNoRuta.setError("Favor de ingresar una ruta");
            campoNoRuta.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(campoEncuestador.getText().toString())) {
            campoEncuestador.setError("Favor de ingresar un nombre");
            campoEncuestador.requestFocus();
            return false;
        }
        return true;
    }

    /**
     *
     * @return id autogenerated for the persisted row.
     */
    private Metadata saveMetadata(){

        Metadata metadata = Metadata.builder()
                .capturist(campoEncuestador.getText().toString())
                .economicNumber(campoNumEcon.getText().toString())
                .via(campoVia.getText().toString())
                .deviceId(androidDeviceId)
                .backedUpRemotely(0)
                .route(campoNoRuta.getText().toString()).build();

        metadataId = (int) metadataRepository.insert(metadata);
        metadata.setId(metadataId);

        StringBuilder fileName = new StringBuilder();
        fileName.append(campoNoRuta.getText().toString());
        fileName.append("-");
        fileName.append(campoNumEcon.getText().toString());
        fileName.append("-Recorrido-");
        fileName.append(String.valueOf(metadataId));
        fileName.append(".txt");

        // Create Metadata file
        ExportData.createFile(fileName.toString(), metadata.toString());

        return metadata;
    }

}
