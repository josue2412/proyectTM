package com.sgcities.tdc.optimizer.infrastructure.network;

/**
 *
 * @param <T> type of the object to be handled
 */
public interface UserLoginRetrievedCallback<T> {

    void onSuccess(T user);
}
