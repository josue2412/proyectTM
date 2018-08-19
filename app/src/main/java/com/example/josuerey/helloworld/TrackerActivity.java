package com.example.josuerey.helloworld;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.josuerey.helloworld.utilidades.Utilidades;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TrackerActivity extends AppCompatActivity {

    //PARADAS
    TextView campoId_dRec,campoId_Rec, campoHora_Ref, campoT_Parada, campoT_Parada2, campoSuben, campoBajan, campoP_Abordo, campoCoord;
    //DRECORRIDO
    TextView campodHora_Ref, campodCoord;

    private TextView latLongTextView;
    private TextView directionsTextView;
    private Button botonGuardar;
    private LocationManager mlocManager;
    private Localizacion mlocListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_activity);

        //Bind layout components
        latLongTextView = findViewById(R.id.latLong);
        directionsTextView = findViewById(R.id.directions);
        botonGuardar = this.findViewById(R.id.btnSave);

       /* botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TrackerActivity.this, "Not available yet.",
                        Toast.LENGTH_SHORT).show();
            }
        });*/

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

        ConexionSQLiteH conn= new ConexionSQLiteH(this,"bd_recorridos", null, 1);

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


    private void locationStart() {

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new Localizacion();
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

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mlocListener);
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
    public class Localizacion implements LocationListener {
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
            loc.getLatitude();
            loc.getLongitude();
            String Text = "Lat = "+ loc.getLatitude() + "\n Long = " + loc.getLongitude();
            /*double TextLa = loc.getLatitude();
            double TextLo = loc.getLongitude();
            latLongTextView.setText(Text);
            latitude.setText((int) TextLa);
            longitude.setText((int) TextLo);*/
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

    private void registrarcoordenadas(){

        ConexionSQLiteH conn= new ConexionSQLiteH(this,"bd_recorridos", null, 1);

        SQLiteDatabase db=conn.getWritableDatabase();

        String insert="INSERT INTO "+ Utilidades.TABLA_DRECORRIDOS
                +" ( "+ Utilidades.CAMPO_ID_REC
                +","+ Utilidades.CAMPO_DHORA_REF +","+ Utilidades.CAMPO_DCOORD +")" +
                "VALUES (1,2,'"+ latLongTextView.getText().toString() +"')";

        db.execSQL(insert);

        db.close();
    }

    private void registrarparadas(){
        ConexionSQLiteH conn= new ConexionSQLiteH(this,"bd_recorridos", null, 1);
        SQLiteDatabase db=conn.getWritableDatabase();

        String insert="INSERT INTO "+ Utilidades.TABLA_PARADAS
                +" ( "+ Utilidades.CAMPO_ID_PARADAS +","+ Utilidades.CAMPO_ID_REC
                +","+ Utilidades.CAMPO_HORA_REF +","+ Utilidades.CAMPO_T_PARADA +","+ Utilidades.CAMPO_T_PARADA2
                +","+ Utilidades.CAMPO_SUBEN +","+ Utilidades.CAMPO_BAJAN +","+ Utilidades.CAMPO_P_ABORDO
                +","+ Utilidades.CAMPO_COORD +")" +
                "VALUES (1,1,2,1,1,2,2,3,'"+ latLongTextView.getText().toString() +"')";

        db.execSQL(insert);

        db.close();
    }

}
