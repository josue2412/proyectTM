package com.sgcities.tdc.optimizer.domain.origindestiny.poll;

import android.app.Application;
import android.util.Log;

import com.sgcities.tdc.optimizer.application.shared.TrackableBaseActivity;
import com.sgcities.tdc.optimizer.domain.gpslocation.GPSLocation;
import com.sgcities.tdc.optimizer.domain.origindestiny.answer.OriginDestinyPollAnswerRepository;
import com.sgcities.tdc.optimizer.domain.uRoomDatabase;
import com.sgcities.tdc.optimizer.infrastructure.persistence.RemotelyStore;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class OriginDestinyPollRepository implements RemotelyStore<OriginDestinyPollWrapper> {

    private static final String TAG = OriginDestinyPollAnswerRepository.class.getName();
    private OriginDestinyPollDao pollDao;

    public OriginDestinyPollRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        this.pollDao = db.pollDao();
    }

    @Override
    public void backedUpRemotely(List<OriginDestinyPollWrapper> recordsToUpdate) {
        Log.i(TAG, String.format(" %d records backed up remotely", recordsToUpdate.size()));
        List<OriginDestinyPoll> pollsToUpdate = new LinkedList<>();
        for (OriginDestinyPollWrapper wrapper : recordsToUpdate) {
            pollsToUpdate.add(wrapper.getPoll());
        }

        this.pollDao.updateInBatch(pollsToUpdate.toArray(new OriginDestinyPoll[recordsToUpdate.size()]));
    }

    @Override
    public List<OriginDestinyPollWrapper> findRecordsPendingToBackUp() {
        return this.pollDao.findPendingToBackUp();
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

    public List<OriginDestinyPollWrapper> findAll() {
        return this.pollDao.findAll();
    }

    /**
     * Retrieves the available polls related to the assignment.
     * @param id of the assignment.
     * @return {@link List} of {@link OriginDestinyPollWrapper}
     */
    public List<OriginDestinyPollWrapper> findByAssignmentId(long id) {
        return this.pollDao.findByAssignmentId(id);
    }

    public OriginDestinyPoll save(final OriginDestinyPoll poll) {
        Log.d(TAG, String.format("Saving poll from assignment: %d ...", poll.getAssignmentId()));
        long id = pollDao.save(poll);
        poll.setId(id);
        return poll;
    }
}
