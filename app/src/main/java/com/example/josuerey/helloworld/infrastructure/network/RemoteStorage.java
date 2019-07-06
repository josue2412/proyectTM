package com.example.josuerey.helloworld.infrastructure.network;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.josuerey.helloworld.application.shared.AssignmentsDisplay;
import com.example.josuerey.helloworld.domain.shared.Storable;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <T> Type of the object that is going to be store remotely. It must implement the
 * {@link Storable} interface.
 */
public interface RemoteStorage<T extends Storable> {

    String REMOTE_STORAGE_TAG = RemoteStorage.class.getName();
    String getEndpointUrl();
    Application getAppContext();
    String getPostParamName();

    default void postItemsInBatch(final List<T> itemsToStorage) {

        final String requestUrl = String.format("%s%s", AssignmentsDisplay.serverIp, getEndpointUrl());
        StringRequest stringRequest =
                new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //the response contains the result from the server, a
                        // json string or any other object returned by your server

                        Log.d(REMOTE_STORAGE_TAG, String.format("Server response: %s ", response));
                        for (T item : itemsToStorage) {
                            item.remotelyBackedUpSuccessfully();
                        }
                        //busStopRepository.updateBusStopInBatch(busStop.toArray(new BusStop[busStop.size()]));
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
                        String list2JsonArray = new Gson().toJson(itemsToStorage);
                        postMap.put(getPostParamName(), list2JsonArray);
                        Log.i(REMOTE_STORAGE_TAG, String.format("%s: %s", getPostParamName(),list2JsonArray));

                        return postMap;
                    }
                };
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(getAppContext()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
