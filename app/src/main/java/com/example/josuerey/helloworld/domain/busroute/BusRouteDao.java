package com.example.josuerey.helloworld.domain.busroute;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BusRouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(BusRoute... dataEntities);

    @Query("SELECT * FROM BusRoute")
    BusRoute[] findAll();
}
