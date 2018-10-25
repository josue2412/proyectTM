package com.example.josuerey.helloworld.domain.vehicularcapacity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface VehicularCapacityDao {

    @Insert
    long save(VehicularCapacity obj);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateInBatch(VehicularCapacity... objects);

    @Query("SELECT * FROM VehicularCapacity WHERE backedUpRemotely = :value")
    VehicularCapacity[] findRecordsPendingToBackup(int value);
}
