package com.example.josuerey.helloworld.domain.vehicularcapacityrecord;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

public class VehicularCapacityRecordRepository {
    private VehicularCapacityRecordDao vehicularCapacityRecordDao;

    public VehicularCapacityRecordRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        this.vehicularCapacityRecordDao = db.vehicularCapacityRecordDao();
    }

    public long save(VehicularCapacityRecord vehicularCapacityRecord) {

        return vehicularCapacityRecordDao.save(vehicularCapacityRecord);
    }

    public void updateInBatch(VehicularCapacityRecord[] vehicularCapacityRecords) {

        vehicularCapacityRecordDao.updateInBatch(vehicularCapacityRecords);
    }

    public VehicularCapacityRecord[] findRecordsPendingToBackup() {

        return vehicularCapacityRecordDao.findRecordsPendingToBackup(0);
    }
}
