package com.example.josuerey.helloworld;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
