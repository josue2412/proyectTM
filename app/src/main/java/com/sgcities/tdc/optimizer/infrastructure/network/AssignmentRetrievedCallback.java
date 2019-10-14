package com.sgcities.tdc.optimizer.infrastructure.network;

import java.util.List;

public interface AssignmentRetrievedCallback<T> {
    void onSuccess(List<T> assignments);
}
