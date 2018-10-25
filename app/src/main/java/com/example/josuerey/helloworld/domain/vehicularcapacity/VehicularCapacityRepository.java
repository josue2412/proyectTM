package com.example.josuerey.helloworld.domain.vehicularcapacity;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

public class VehicularCapacityRepository {

    private VehicularCapacityDao vehicularCapacityDao;

    public VehicularCapacityRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        vehicularCapacityDao = db.vehicularCapacityDao();
    }

    public long save(VehicularCapacity vehicularCapacity) {

        return vehicularCapacityDao.save(vehicularCapacity);
    }

    public void updateInBatch(VehicularCapacity[] vehicularCapacities) {

        vehicularCapacityDao.updateInBatch(vehicularCapacities);
    }

    public VehicularCapacity[] findRecordsPendingToBackup() {

        return vehicularCapacityDao.findRecordsPendingToBackup(0);
    }
}
