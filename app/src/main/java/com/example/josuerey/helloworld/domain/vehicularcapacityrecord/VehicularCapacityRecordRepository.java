package com.example.josuerey.helloworld.domain.vehicularcapacityrecord;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;
import com.example.josuerey.helloworld.utilities.ExportData;

public class VehicularCapacityRecordRepository {
    private VehicularCapacityRecordDao vehicularCapacityRecordDao;

    public VehicularCapacityRecordRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        this.vehicularCapacityRecordDao = db.vehicularCapacityRecordDao();
    }

    public long save(VehicularCapacityRecord vehicularCapacityRecord) {

        long generatedId = vehicularCapacityRecordDao.save(vehicularCapacityRecord);

        vehicularCapacityRecord.setId((int)generatedId);
        ExportData.createFile(String.format("%s-%d.txt", vehicularCapacityRecord.getDeviceId(),
                vehicularCapacityRecord.getMovementId()),
                vehicularCapacityRecord.toString());
        return generatedId;
    }

    public void updateInBatch(VehicularCapacityRecord[] vehicularCapacityRecords) {

        vehicularCapacityRecordDao.updateInBatch(vehicularCapacityRecords);
    }

    public VehicularCapacityRecord[] findRecordsPendingToBackup() {

        return vehicularCapacityRecordDao.findRecordsPendingToBackup(0);
    }
}
