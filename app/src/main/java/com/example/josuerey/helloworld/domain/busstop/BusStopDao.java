package com.example.josuerey.helloworld.domain.busstop;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.josuerey.helloworld.domain.busoccupation.BusOccupation;

import java.util.List;

/**
 * Data Access Layer for {@link BusStop} object.
 */
@Dao
public interface BusStopDao {

    @Insert
    long insert(BusStop busStop);

    @Query("SELECT * from BusStop")
    LiveData<List<BusStop>> findAllBusStops();

    @Query("SELECT * from BusStop where assignmentId = :assignmentId")
    LiveData<List<BusStop>> findBusStopsByMetadataId(int assignmentId);

    @Query("SELECT * FROM BusStop where backedUpRemotely = :value")
    BusStop[] findBusStopsByBackedUpRemotely(int value);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBusStop(BusStop ... busStops);

    @Query("SELECT * FROM BusStop where backedUpRemotely = 0")
    List<BusStop> findRecordsPendingToBackup();
}
