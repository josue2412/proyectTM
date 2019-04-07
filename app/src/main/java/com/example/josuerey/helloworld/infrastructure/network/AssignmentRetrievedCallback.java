package com.example.josuerey.helloworld.infrastructure.network;

import java.util.List;

public interface AssignmentRetrievedCallback<T> {
    void onSuccess(List<T> assignments);
}
