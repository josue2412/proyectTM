package com.sgcities.tdc.optimizer.domain.origindestiny.answer;

import android.app.Application;
import android.util.Log;

import com.sgcities.tdc.optimizer.domain.uRoomDatabase;

import java.util.List;

/**
 * Data access layer for {@link OriginDestinyPollAnswer} entity.
 */
public class OriginDestinyPollAnswerRepository {

    private static final String TAG = OriginDestinyPollAnswerRepository.class.getName();

    private OriginDestinyPollAnswerDao pollAnswerDao;

    public OriginDestinyPollAnswerRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        pollAnswerDao = db.pollAnswerDao();
    }

    public void saveAll(final List<OriginDestinyPollAnswer> answers) {
        Log.d(TAG, String.format("Saving %d OriginDestinyPollAnswer answers from poll: %d",
                answers.size(), answers.get(0).getPollId()));
        pollAnswerDao.saveAll(answers.toArray(new OriginDestinyPollAnswer[answers.size()]));
    }


    public OriginDestinyPollAnswer[] findByPollId(final int pollId) {
        return pollAnswerDao.findByPollId(pollId);
    }
}
