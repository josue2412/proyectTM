package com.example.josuerey.helloworld.domain.shared;

import android.util.Log;

public interface Storable {

    String TAG = Storable.class.getName();
    int getBackedUpRemotely();
    int getId();
    void setBackedUpRemotely(int value);

    default void remotelyBackedUpSuccessfully() {

        setBackedUpRemotely(1);
        Log.d(TAG, String.format("Object with id: {} successfully backed up in remote server.", getId()));
    }

}
