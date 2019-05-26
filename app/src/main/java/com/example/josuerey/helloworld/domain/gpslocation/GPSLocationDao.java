package com.example.josuerey.helloworld.domain.gpslocation;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GPSLocationDao {

    @Insert(onConflict = REPLACE)
    long insert(GPSLocation metadata);

    @Query("SELECT * from GPSLocation where assignmentId = :assignmentId")
    LiveData<List<GPSLocation>> findGPSLocationsById(int assignmentId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateGPSLocation(GPSLocation... gpsLocations);

    @Query("SELECT * from GPSLocation where backedUpRemotely = :value")
    GPSLocation[] findGPSLocationByBackedUpRemotely(int value);
}
