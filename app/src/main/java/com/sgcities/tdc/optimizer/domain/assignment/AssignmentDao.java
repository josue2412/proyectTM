package com.sgcities.tdc.optimizer.domain.assignment;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.google.common.base.Optional;

@Dao
public interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Assignment assignment);

    @Query("SELECT * FROM Assignment where capturistId = :value")
    Assignment[] findAssignmentsByCapturistId(int value);

    @Query("SELECT * FROM Assignment where serverId = :serverId")
    Optional<Assignment> findByServerId(int serverId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAssignment(Assignment assignment);

    @Query("UPDATE Assignment SET timeOfStudy = :value WHERE serverId = :serverId ")
    void updateRemainingTime(String value, int serverId);
}
