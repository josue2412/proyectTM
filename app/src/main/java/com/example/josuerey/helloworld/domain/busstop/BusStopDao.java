package com.example.josuerey.helloworld.domain.busstop;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BusStopDao {

    @Insert
    long insert(BusStop busStop);

    @Query("SELECT * from BusStop")
    LiveData<List<BusStop>> findAllBusStops();

    @Query("SELECT * from BusStop where idMetadata = :idMetadata")
    LiveData<List<BusStop>> findBusStopsByMetadataId(int idMetadata);

    @Query("UPDATE BusStop SET backedUpRemotely = 1 where id = :id")
    void updateBusStopBackupRemotelyById(int id);

    @Query("SELECT * FROM BusStop where backedUpRemotely = :value")
    BusStop[] findBusStopsByBackedUpRemotely(int value);
}
