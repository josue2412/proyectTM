package com.example.josuerey.helloworld.entidades;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GPSLocationDao {

    @Insert(onConflict = REPLACE)
    void insert(GPSLocation metadata);

    @Query("SELECT * from GPSLocation where idMetadata = :id")
    LiveData<List<GPSLocation>> findGPSLocationsById(int id);
}
