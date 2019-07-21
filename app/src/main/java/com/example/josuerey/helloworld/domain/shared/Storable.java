package com.example.josuerey.helloworld.domain.shared;

import android.util.Log;

/**
 * Interface that standardize the behaviour of the objects allowed by
 * {@link com.example.josuerey.helloworld.infrastructure.network.RemoteStorage}
 */
public interface Storable {

    String TAG = Storable.class.getName();
    int getBackedUpRemotely();
    int getId();
    void setBackedUpRemotely(int value);

    default void remotelyBackedUpSuccessfully() {
        setBackedUpRemotely(1);
    }

}
