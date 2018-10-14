package com.example.josuerey.helloworld.domain.metadata;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * This interface abstracts the database operations regarding the Metadata table.
 *
 * @author josuerey
 * @version 1.0
 */
@Dao
public interface MetadataDao {

    /**
     * Persist a new Metadata entry.
     *
     * @param metadata to persist.
     */
    @Insert
    long insert(Metadata metadata);

    /**
     * Retrieves metadata associated to given Id.
     * @return al Metadata records.
     */
    @Query("Select * from Metadata")
    public Metadata[] loadAllMetadata();


    @Query("Select * from Metadata where backedUpRemotely = :value")
    public Metadata[] findMetadataByBackedUpRemotely(int value);

    /**
     * This method is called once the record is successfully backed up in the
     * remote server.
     *
     * @param id that identifies the record to update.
     */
    @Query("UPDATE Metadata SET backedUpRemotely = 1 where id = :id")
    void updateMetadataBackupRemotelyById(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMetadata(Metadata ... metadata);
}
