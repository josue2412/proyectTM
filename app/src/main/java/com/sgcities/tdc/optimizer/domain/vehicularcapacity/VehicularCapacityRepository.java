package com.sgcities.tdc.optimizer.domain.vehicularcapacity;

import android.app.Application;

import com.sgcities.tdc.optimizer.domain.uRoomDatabase;
import com.sgcities.tdc.optimizer.infrastructure.network.VehicularCapAssignmentResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VehicularCapacityRepository {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private VehicularCapacityDao vehicularCapacityDao;

    public VehicularCapacityRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        vehicularCapacityDao = db.vehicularCapacityDao();
    }

    private VehicularCapacity createVehicularCapacity(VehicularCapAssignmentResponse assignment) {
        return VehicularCapacity.builder()
                .assignmentId(assignment.getId())
                .backedUpRemotely(0)
                .observations("No observations")
                .timeStamp(DATE_FORMAT.format(Calendar.getInstance().getTime()))
                .build();
    }

    public long save(VehicularCapacity vehicularCapacity) {

        return vehicularCapacityDao.save(vehicularCapacity);
    }

    public VehicularCapacity save(VehicularCapAssignmentResponse assignment) {

        VehicularCapacity persisted = createVehicularCapacity(assignment);
        vehicularCapacityDao.save(persisted);
        return persisted;
    }

    public void updateInBatch(VehicularCapacity[] vehicularCapacities) {

        vehicularCapacityDao.updateInBatch(vehicularCapacities);
    }

    public VehicularCapacity[] findRecordsPendingToBackup() {

        return vehicularCapacityDao.findRecordsPendingToBackup(0);
    }
}
