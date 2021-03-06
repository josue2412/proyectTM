package com.example.josuerey.helloworld.network;

import android.app.Application;
import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacity;
import com.example.josuerey.helloworld.domain.vehicularcapacity.VehicularCapacityRepository;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.example.josuerey.helloworld.domain.vehicularcapacityrecord.VehicularCapacityRecordRepository;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadata;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
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
    private ProgressDialog progressDialog;
    ObjectMapper mapper;

    public void postBusStopInBatch(final List<BusStop> busStop,
                                  final BusStopRepository busStopRepository) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/routeBusStopV2";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("BusStop", ""+response);

                        for (BusStop record : busStop) {
                            record.remotelyBackedUpSuccessfully();
                        }
                        busStopRepository.updateBusStopInBatch(
                                busStop.toArray(new BusStop[busStop.size()]));
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
                        String list2JsonArray = new Gson().toJson(busStop);
                        postMap.put("busStopData", list2JsonArray);
                        Log.i(TAG, "busStopData: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void postMetadataInBatch (final List<Metadata> metadata,
                                    final MetadataRepository repository) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/routeMetadataV2";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("Metadata", ""+response);

                        for (Metadata record : metadata) {
                            record.remotelyBackedUpSuccessfully();
                        }
                        repository.updateMetadataInBatch(
                                metadata.toArray(new Metadata[metadata.size()]));
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
                        String list2JsonArray = new Gson().toJson(metadata);
                        postMap.put("metadataData", list2JsonArray);
                        Log.i(TAG, "metadataData: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void postGpsLocationInBatch(final List<GPSLocation> route,
                                       final GPSLocationRepository gpsLocationRepository) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/routeV2";
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
                        postMap.put("RouteData", list2JsonArray);
                        Log.i(TAG, "RouteData: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void postVehicularCapMeta(final List<VehicularCapacity> VehicularCap,
                                     final VehicularCapacityRepository vehicularCapRepo) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/vehicCapMetadata";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("vehicularCapResult", ""+response);

                        for (VehicularCapacity record : VehicularCap) {
                            record.remotelyBackedUpSuccessfully();
                        }
                        vehicularCapRepo.updateInBatch(
                                VehicularCap.toArray(new VehicularCapacity[VehicularCap.size()]));
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
                        String list2JsonArray = new Gson().toJson(VehicularCap);
                        postMap.put("vehicCapData", list2JsonArray);
                        Log.i(TAG, "vehicCapData: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void postVehicularCapRecord(final List<VehicularCapacityRecord> VehicularCap,
                                       final VehicularCapacityRecordRepository vehicularCapRepo) {
        String requestUrl = "http://u856955919.hostingerapp.com/api/persist/vehicCapRecord";
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        Log.d("CapRecordResult", ""+response);

                        for (VehicularCapacityRecord record : VehicularCap) {
                            record.remotelyBackedUpSuccessfully();
                        }
                        vehicularCapRepo.updateInBatch(
                                VehicularCap.toArray(new VehicularCapacityRecord[VehicularCap.size()]));
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
                        String list2JsonArray = new Gson().toJson(VehicularCap);
                        postMap.put("vehicCapData", list2JsonArray);
                        Log.i(TAG, "vehicCapData: " + list2JsonArray);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void retrieveAssignments(final String capturistId){
        mapper = new ObjectMapper();
        String requestUrl = "http://u856955919.hostingerapp.com/api/capturistAssignments?capturist_id=1";
        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server
                        try {
                            AssignmentResponse[] assignmentResponse = mapper.readValue(response, AssignmentResponse[].class);

                            Log.d(TAG, "Assignments: " + assignmentResponse[0].getMovement());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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
                        postMap.put("capturist_id", capturistId);
                        Log.i(TAG, "Retrieving assignments for capturis: : " + capturistId);

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(app).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}
