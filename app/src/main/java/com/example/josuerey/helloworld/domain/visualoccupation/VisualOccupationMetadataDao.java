package com.example.josuerey.helloworld.domain.visualoccupation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;

@Dao
public interface VisualOccupationMetadataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(VisualOccupationMetadata... dataEntities);

    @Query("SELECT * FROM VisualOccupationMetadata")
    VisualOccupationMetadata[] findAll();

    @Insert
    long insert(VisualOccupationMetadata metadata);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateVisualOccupationMetadata(VisualOccupationMetadata... metadata);

    @Query("SELECT * FROM VisualOccupationMetadata WHERE backedUpRemotely = :value")
    VisualOccupationMetadata[] findRecordsPendingToBackup(int value);

    @Query("SELECT * FROM VisualOccupationMetadata WHERE assignmentId = :value")
    VisualOccupationMetadata findByAssignmentId(int value);
}
