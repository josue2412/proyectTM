package com.example.josuerey.helloworld;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.josuerey.helloworld.utilidades.Utilidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText campoId,campoNom_Ruta, campoVia, campoNum_Econ, campoEncuestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        campoId=(EditText) findViewById(R.id.editTextID);
        campoNom_Ruta=(EditText) findViewById(R.id.editTextRuta);
        campoVia=(EditText) findViewById(R.id.editTextVia);
        campoNum_Econ=(EditText) findViewById(R.id.editTextNumEcon);
        campoEncuestador=(EditText) findViewById(R.id.editTextEnc);
    }

    public void onClick(View view){
        //registrarRecorridos();

        Intent miIntent=null;
        switch (view.getId()){

            case R.id.btnConsultaRecorrido:
                miIntent=new Intent(MainActivity.this,ConsultarRecorridos.class);
                break;

        }
        if (miIntent!=null){
            startActivity(miIntent);
        }

    }

    private void registrarRecorridosSQL(){
        ConexionSQLiteH conn= new ConexionSQLiteH(this,"bd_recorridos", null, 1);

        SQLiteDatabase db=conn.getWritableDatabase();

        String insert="INSERT INTO "+ Utilidades.TABLA_RECORRIDO
                +" ( "+ Utilidades.CAMPO_ID +","+ Utilidades.CAMPO_NOM_RUTA
                +","+ Utilidades.CAMPO_VIA +","+ Utilidades.CAMPO_NUM_ECON +","+ Utilidades.CAMPO_ENCUESTADOR +")" +
                "VALUES ("+ campoId.getText().toString() +", '"+campoNom_Ruta.getText().toString()+"','"
                 + campoVia.getText().toString() +"', '" + campoNum_Econ.getText().toString() + "','"
                + campoEncuestador.getText().toString() + "')";

        db.execSQL(insert);

        db.close();
    }

    public void registrarRecorridos(){
        ConexionSQLiteH conn= new ConexionSQLiteH(this,"bd_recorridos", null, 1);

        SQLiteDatabase db=conn.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(Utilidades.CAMPO_ID,campoId.getText().toString());
        values.put(Utilidades.CAMPO_NOM_RUTA,campoNom_Ruta.getText().toString());
        values.put(Utilidades.CAMPO_VIA,campoVia.getText().toString());
        values.put(Utilidades.CAMPO_NUM_ECON,campoNum_Econ.getText().toString());
        values.put(Utilidades.CAMPO_ENCUESTADOR,campoEncuestador.getText().toString());

        Long idResultante=db.insert(Utilidades.TABLA_RECORRIDO,Utilidades.CAMPO_ID, values);

        Toast.makeText(getApplicationContext(), "Id Registro: "+idResultante,Toast.LENGTH_SHORT).show();
        db.close();
    }

    public void setStartButton(View target) {
        new AsyncCaller().execute();
    }

    private class AsyncCaller extends AsyncTask<Void, Void, JSONObject> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                JSONObject response = getAuthorization();
                return response;
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                evalResponse(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //this method will be running on UI thread
            pdLoading.dismiss();
        }
    }

    private void evalResponse(JSONObject response) throws JSONException {

        String auth = response.getString("success");
        String msg;

        if (auth.equals("1")){
            JSONObject data = response.getJSONObject("data");
            String isActive = data.getString("active");

            if (isActive.equals("1")){

                //registrarRecorridos();
                registrarRecorridosSQL();

                msg = "Welcome";
                Intent myIntent = new Intent(MainActivity.this, TrackerActivity.class);
                MainActivity.this.startActivity(myIntent);
            }

            else
                msg = "You are not allowed to use this app anymore, until you pay.";
        } else {
            msg = "Unable to connect to remote server.";
        }

        Log.i("EvalResponse", "Auth:" + msg);
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_SHORT).show();

    }

    /**
     * This method makes a GET request to a remote server in order to obtain the authorization
     * data from the user.
     *
     * @return auth data for the current user.
     * @throws JSONException
     */
    private JSONObject getAuthorization() throws JSONException {
        URL url;
        HttpURLConnection urlConnection = null;
        String user = "User1";
        String pass = "abc123";
        StringBuffer response = new StringBuffer();
        try {
            url = new URL("http://u856955919.hostingerapp.com/userAuthentication.php" +
                    "?user=" + user + "&pass=" + pass);
            Log.i("AuthUser", "HttpRequest to verify user: " + url.toString());

            urlConnection = (HttpURLConnection) url
                    .openConnection();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Log.i("AuthUSer", "Response:" + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new JSONObject(response.toString());
    }
}
