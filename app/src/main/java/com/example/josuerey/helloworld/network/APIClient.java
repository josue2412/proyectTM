package com.example.josuerey.helloworld.network;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.domain.busstop.BusStop;
import com.example.josuerey.helloworld.domain.metadata.Metadata;

import java.util.HashMap;
import java.util.Map;

public class APIClient {

    private Application app;
    private final static String TAG = "APIClient";

    public APIClient(Application app) {
        this.app = app;
    }

    public void postBusStop(final BusStop bs) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/busstop";
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
                        postMap.put("id", String.valueOf(bs.getId()));
                        postMap.put("idMetadata", String.valueOf(bs.getIdMetadata()));
                        postMap.put("timeStamp", bs.getTimeStamp());
                        postMap.put("stopType", bs.getStopType());
                        postMap.put("passengersUp", String.valueOf(bs.getPassengersUp()));
                        postMap.put("passengersDown", String.valueOf(bs.getPassengersDown()));
                        postMap.put("totalPassengers", String.valueOf(bs.getTotalPassengers()));
                        postMap.put("lat", String.valueOf(bs.getLat()));
                        postMap.put("lon", String.valueOf(bs.getLon()));
                        postMap.put("isOfficial", String.valueOf(bs.isOfficial() ? 1 : 0));
                        postMap.put("stop_begin", bs.getStopBegin());
                        postMap.put("stop_end", bs.getStopEnd());
                        postMap.put("deviceId", bs.getDeviceId());

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
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
                        postMap.put("deviceId", m.getDeviceId());

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
    }
}
