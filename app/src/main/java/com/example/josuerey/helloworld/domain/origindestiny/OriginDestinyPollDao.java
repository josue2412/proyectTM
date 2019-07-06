package com.example.josuerey.helloworld.domain.origindestiny;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
}
