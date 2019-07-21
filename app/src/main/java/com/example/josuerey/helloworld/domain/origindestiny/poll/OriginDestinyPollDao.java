package com.example.josuerey.helloworld.domain.origindestiny.poll;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Data access layer for {@link OriginDestinyPoll} object.
 */
@Dao
public interface OriginDestinyPollDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(OriginDestinyPoll... polls);

    @Query("SELECT * FROM OriginDestinyPoll where id = :pollId")
    List<OriginDestinyPoll> findByPollId(int pollId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(OriginDestinyPoll poll);

    @Query("SELECT * FROM OriginDestinyPoll")
    List<OriginDestinyPollWrapper> findAll();

    @Query("SELECT * FROM OriginDestinyPoll where assignment_id = :id")
    List<OriginDestinyPollWrapper> findByAssignmentId(long id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateInBatch(OriginDestinyPoll... polls);

    @Query("SELECT * FROM OriginDestinyPoll where backedUpRemotely = 0")
    List<OriginDestinyPollWrapper> findPendingToBackUp();
}
