package com.example.josuerey.helloworld.domain.assignment;

import android.app.Application;
import android.util.Log;

import com.example.josuerey.helloworld.domain.uRoomDatabase;
import com.example.josuerey.helloworld.sessionmangementsharedpref.utils.SaveSharedPreference;
import com.google.common.base.Optional;

public class AssignmentRepository {

    private final String TAG = this.getClass().getSimpleName();
    private AssignmentDao assignmentDao;
    private String userKey;

    public AssignmentRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        assignmentDao = db.assignmentDao();
        userKey = SaveSharedPreference.getUserNameKey(application);
    }

    public long save(Assignment assignments) {

        return assignmentDao.save(assignments);
    }

    public Assignment[] findAssignmentsByCapturistId(){

        return assignmentDao.findAssignmentsByCapturistId(Integer.valueOf(userKey));
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
