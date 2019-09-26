package com.example.josuerey.helloworld.domain.busoccupation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Data Access Layer for {@link BusOccupation} object.
 */
@Dao
public interface BusOccupationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BusOccupation busOccupation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBusOccupation(BusOccupation ... busOccupations);

    @Query("SELECT * FROM BusOccupation where backedUpRemotely = 0")
    List<BusOccupation> findPendingToBackup();
}
