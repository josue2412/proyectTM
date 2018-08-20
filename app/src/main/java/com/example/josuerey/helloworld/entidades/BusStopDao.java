package com.example.josuerey.helloworld.entidades;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BusStopDao {

    @Insert
    void insert(BusStop busStop);

    @Query("SELECT * from BusStop ORDER BY id ASC")
    LiveData<List<BusStop>> getAllBusStops();
}
