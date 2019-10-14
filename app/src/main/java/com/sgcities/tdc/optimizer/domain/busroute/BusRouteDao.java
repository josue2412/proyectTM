package com.sgcities.tdc.optimizer.domain.busroute;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface BusRouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(BusRoute... dataEntities);

    @Query("SELECT * FROM BusRoute")
    BusRoute[] findAll();
}
