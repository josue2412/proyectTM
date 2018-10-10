package com.example.josuerey.helloworld.network;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.domain.busoccupation.BusOccupation;
import com.example.josuerey.helloworld.domain.busoccupation.BusOccupationRepository;
import com.example.josuerey.helloworld.domain.busstop.BusStop;
import com.example.josuerey.helloworld.domain.busstop.BusStopRepository;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocationRepository;
import com.example.josuerey.helloworld.domain.metadata.Metadata;
import com.example.josuerey.helloworld.domain.metadata.MetadataRepository;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadata;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadataRepository;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
public class APIClient {

    private final Application app;
    private final String TAG = this.getClass().getSimpleName();

    public void postBusStop(final BusStop bs, final BusStopRepository busStopRepository) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/routeBusStop";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("BusStopResult", ""+response);
                        busStopRepository.updateBusStopBackedUpSuccessById(bs.getId());
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

    public void postMetadata(final Metadata m, final MetadataRepository repository) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/routeMetadata";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("MetadataResult", ""+response);
                        repository.updateMetadataBackedUpSuccessById(m.getId());
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
                        postMap.put("initialPassengers", String.valueOf(m.getInitialPassengers()));
                        postMap.put("deviceId", m.getDeviceId());

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
    }

    public void PostArray(final List<GPSLocation> route, final GPSLocationRepository gpsLocationRepository) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/route";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d(TAG, "RouteArrayResult" + response);

                        for (GPSLocation point : route) {
                            point.remotelyBackedUpSuccessfully();
                        }

                        gpsLocationRepository.updateGPSLocationBackupRemotelyById(
                                route.toArray(new GPSLocation[route.size()]));
                        Log.d(TAG, "Location points updated successfully");
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
                        String list2JsonArray = new Gson().toJson(route);
                        postMap.put("RouteArray", list2JsonArray);
                        Log.i(TAG, "JsonArray: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
    }

    public void postBusOccupationMeta(final List<VisualOccupationMetadata> visOccMeta,
                                      final VisualOccupationMetadataRepository visualOccMetaRepo) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/busOccMetadataV2";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("BusOccMetaResult", ""+response);

                        for (VisualOccupationMetadata record : visOccMeta) {
                            record.remotelyBackedUpSuccessfully();
                        }
                        visualOccMetaRepo.updateVisualOccMetadata(
                                visOccMeta.toArray(new VisualOccupationMetadata[visOccMeta.size()]));
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
                        String list2JsonArray = new Gson().toJson(visOccMeta);
                        postMap.put("busOccMetaArray", list2JsonArray);
                        Log.i(TAG, "busOccMetadata: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
    }

    public void postBusOccupation(final List<BusOccupation> busOcc,
                                  final BusOccupationRepository busOccupationRepository) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/busOccRecordV2";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("BusOccResult", ""+response);

                        for (BusOccupation record : busOcc) {
                            record.remotelyBackedUpSuccessfully();
                        }
                        busOccupationRepository.updateBusOccRecordsBackedUp(
                                busOcc.toArray(new BusOccupation[busOcc.size()]));
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
                        String list2JsonArray = new Gson().toJson(busOcc);
                        postMap.put("busOccData", list2JsonArray);
                        Log.i(TAG, "busOccData: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
    }

}
