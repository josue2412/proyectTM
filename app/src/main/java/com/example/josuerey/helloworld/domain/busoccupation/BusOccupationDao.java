package com.example.josuerey.helloworld.domain.busoccupation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

@Dao
public interface BusOccupationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(BusOccupation busOccupation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateBusOccupation(BusOccupation ... busOccupations);
}
