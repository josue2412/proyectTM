package com.sgcities.tdc.optimizer.domain.viaofstudy;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface ViaOfStudyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ViaOfStudy... dataEntities);

    @Query("SELECT * FROM ViaOfStudy")
    ViaOfStudy[] findAll();
}
