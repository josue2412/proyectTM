package com.example.josuerey.helloworld.domain.vehicularcapacityrecord;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface VehicularCapacityRecordDao {

    @Insert
    long save(VehicularCapacityRecord obj);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateInBatch(VehicularCapacityRecord... objects);

    @Query("SELECT * FROM VehicularCapacityRecord WHERE backedUpRemotely = 0")
    List<VehicularCapacityRecord> findRecordsPendingToBackup();

}
