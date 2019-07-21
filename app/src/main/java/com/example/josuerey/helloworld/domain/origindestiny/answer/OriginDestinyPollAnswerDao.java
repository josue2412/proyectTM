package com.example.josuerey.helloworld.domain.origindestiny.answer;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Data Access Layer for {@link OriginDestinyPollAnswer} object.
 */
@Dao
public interface OriginDestinyPollAnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(OriginDestinyPollAnswer... answers);

    @Query("SELECT * FROM OriginDestinyPollAnswer where poll_id = :pollId")
    OriginDestinyPollAnswer[] findByPollId(int pollId);
}
