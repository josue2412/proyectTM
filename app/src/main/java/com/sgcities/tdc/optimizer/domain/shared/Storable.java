package com.sgcities.tdc.optimizer.domain.shared;

/**
 * Interface that standardize the behaviour of the objects allowed by
 * {@link com.sgcities.tdc.optimizer.infrastructure.network.RemoteStorage}
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
