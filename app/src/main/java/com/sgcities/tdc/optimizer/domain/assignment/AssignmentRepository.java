package com.sgcities.tdc.optimizer.domain.assignment;

import android.app.Application;
import android.util.Log;

import com.sgcities.tdc.optimizer.domain.uRoomDatabase;
import com.sgcities.tdc.optimizer.infrastructure.preferencesmanagement.SaveSharedPreference;
import com.google.common.base.Optional;

public class AssignmentRepository {

    private final String TAG = this.getClass().getSimpleName();
    private AssignmentDao assignmentDao;
    private int userKey;

    public AssignmentRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        assignmentDao = db.assignmentDao();
        userKey = SaveSharedPreference.getUserId(application);
    }

    public long save(Assignment assignments) {

        return assignmentDao.save(assignments);
    }

    public Assignment[] findAssignmentsByCapturistId(){

        return assignmentDao.findAssignmentsByCapturistId(userKey);
    }

    public Optional<Assignment> findByServerId(int serverId) {

        return assignmentDao.findByServerId(serverId);
    }

    public void updateAssignment(Assignment assignment) {
        assignmentDao.updateAssignment(assignment);
    }

    public void updateAssignmentRemainingTime(String remainingTime, int serverId) {
        Log.d(TAG, "Updating remaining time to " + remainingTime + " to assignment " + String.valueOf(serverId));
        assignmentDao.updateRemainingTime(remainingTime, serverId);
    }
}
