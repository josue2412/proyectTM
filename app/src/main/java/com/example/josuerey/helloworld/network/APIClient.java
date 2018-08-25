package com.example.josuerey.helloworld.network;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.domain.metadata.Metadata;

import java.util.HashMap;
import java.util.Map;

public class APIClient {

    private Application app;
    private final static String TAG = "APIClient";

    public APIClient(Application app) {
        this.app = app;
    }

    public void postMetadata(final Metadata m) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/metadata";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.e("Volley Result", ""+response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> postMap = new HashMap<>();
                        postMap.put("id", String.valueOf(m.getId()));
                        postMap.put("route", m.getRoute());
                        postMap.put("via", m.getVia());
                        postMap.put("economicNumber", m.getEconomicNumber());
                        postMap.put("capturist", m.getCapturist());

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
    }
}
