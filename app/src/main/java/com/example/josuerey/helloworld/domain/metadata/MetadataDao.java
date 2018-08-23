package com.example.josuerey.helloworld.domain.metadata;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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
     * @param id of the requested metadata.
     * @return Metadata associated to given Id.
     */
    @Query("Select * from Metadata where id = :id")
    LiveData<Metadata> findMetadataById(int id);
}
