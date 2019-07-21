package com.example.josuerey.helloworld.infrastructure.persistence;

import java.util.List;

/**
 *
 */
public interface RemotelyStore<T> {

 void backedUpRemotely(List<T> recordsToUpdate);

 List<T> findRecordsPendingToBackUp();
}
