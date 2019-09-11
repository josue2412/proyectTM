package com.example.josuerey.helloworld.domain.vehicularcapacityrecord;

import android.app.Application;
import android.util.Log;

import com.example.josuerey.helloworld.application.vehicularcap.CounterStats;
import com.example.josuerey.helloworld.application.vehicularcap.UnderStudyVehicles;
import com.example.josuerey.helloworld.domain.uRoomDatabase;
import com.example.josuerey.helloworld.infrastructure.persistence.RemotelyStore;
import com.example.josuerey.helloworld.utilities.ExportData;

import java.util.List;
import java.util.Map;

public class VehicularCapacityRecordRepository implements RemotelyStore<VehicularCapacityRecord> {
    private VehicularCapacityRecordDao vehicularCapacityRecordDao;
    private static final String TAG = VehicularCapacityRecordRepository.class.getName();

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

    @Override
    public void backedUpRemotely(List<VehicularCapacityRecord> records) {
        Log.i(TAG, String.format(" %d records backed up remotely", records.size()));

        this.updateInBatch(records.toArray(new VehicularCapacityRecord[records.size()]));
    }

    @Override
    public List<VehicularCapacityRecord> findRecordsPendingToBackUp() {

        return vehicularCapacityRecordDao.findRecordsPendingToBackup();
    }

    public void updateInBatch(VehicularCapacityRecord[] vehicularCapacityRecords) {

        vehicularCapacityRecordDao.updateInBatch(vehicularCapacityRecords);
    }

    public VehicularCapacityRecord.VehicularCapacityRecordBuilder createVehicularRecord (
            Map<String, CounterStats> vehicles) {
        return VehicularCapacityRecord.builder()
                .numberOfBusses(vehicles.get(UnderStudyVehicles.BUS.name()).flushPartialCount())
                .numberOfCars(vehicles.get(UnderStudyVehicles.CAR.name()).flushPartialCount())
                .numberOfTrucks(vehicles.get(UnderStudyVehicles.TRUCK.name()).flushPartialCount())
                .numberOfMotorcycles(vehicles.get(UnderStudyVehicles.MOTORCYCLE.name()).flushPartialCount());
    }

    public VehicularCapacityRecord.VehicularCapacityRecordBuilder createPedestrianRecord(
            Map<String, CounterStats> vehicles) {
        return VehicularCapacityRecord.builder()
                .numberOfBikes(vehicles.get(UnderStudyVehicles.BIKE.name()).flushPartialCount())
                .numberOfBikesFemale(vehicles.get(UnderStudyVehicles.BIKE_FEMALE.name()).flushPartialCount())
                .numberOfPedestrians(vehicles.get(UnderStudyVehicles.PEDESTRIAN.name()).flushPartialCount())
                .numberOfPedestriansFemale(vehicles.get(UnderStudyVehicles.PEDESTRIAN_FEMALE.name()).flushPartialCount());
    }
}
