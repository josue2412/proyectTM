package com.example.josuerey.helloworld.domain.origindestiny;

import android.app.Application;
import android.util.Log;

import com.example.josuerey.helloworld.application.shared.TrackableBaseActivity;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.uRoomDatabase;

import java.util.Calendar;
import java.util.List;

public class OriginDestinyPollRepository {

    private static final String TAG = OriginDestinyPollAnswerRepository.class.getName();
    private OriginDestinyPollDao pollDao;

    public OriginDestinyPollRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        this.pollDao = db.pollDao();
    }

    /**
     * Creates a new instance of {@link OriginDestinyPoll}
     *
     * @param currentLocation current device location
     * @param assignmentId unique identifier of the current assignment
     * @return
     */
    public OriginDestinyPoll createOriginDestinyPoll(GPSLocation currentLocation, int assignmentId) {
        return OriginDestinyPoll.builder()
                .assignmentId(assignmentId)
                .lat(currentLocation != null ? currentLocation.getLat() : 0.0)
                .lon((currentLocation != null ? currentLocation.getLon() : 0.0))
                .timeStamp(TrackableBaseActivity.DATE_FORMAT.format(
                        Calendar.getInstance().getTime())).build();
    }

    public void saveAll(final List<OriginDestinyPoll> polls) {
        Log.d(TAG, String.format("Saving %d polls...", polls.size()));
        pollDao.saveAll(polls.toArray(new OriginDestinyPoll[polls.size()]));
    }

    public OriginDestinyPoll save(final OriginDestinyPoll poll) {
        Log.d(TAG, String.format("Saving poll from assignment: %d ...", poll.getAssignmentId()));
        long id = pollDao.save(poll);
        poll.setId(id);
        return poll;
    }
}
