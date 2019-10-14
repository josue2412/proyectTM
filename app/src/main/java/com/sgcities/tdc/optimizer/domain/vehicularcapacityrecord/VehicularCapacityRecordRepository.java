package com.sgcities.tdc.optimizer.domain.vehicularcapacityrecord;

import android.app.Application;

import com.sgcities.tdc.optimizer.application.vehicularcap.MovementCounter;
import com.sgcities.tdc.optimizer.application.vehicularcap.UnderStudyVehicles;
import com.sgcities.tdc.optimizer.domain.uRoomDatabase;
import com.sgcities.tdc.optimizer.infrastructure.persistence.RemotelyStore;
import com.sgcities.tdc.optimizer.utilities.ExportData;

import java.util.List;

public class VehicularCapacityRecordRepository implements RemotelyStore<VehicularCapacityRecord> {
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

    @Override
    public void backedUpRemotely(List<VehicularCapacityRecord> records) {

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
            MovementCounter movementCounter) {
        return VehicularCapacityRecord.builder()
                .numberOfBusses(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.BUS.name()).flushPartialCount())
                .numberOfCars(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.CAR.name()).flushPartialCount())
                .numberOfTrucks(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.TRUCK.name()).flushPartialCount())
                .numberOfMotorcycles(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.MOTORCYCLE.name()).flushPartialCount());
    }

    public VehicularCapacityRecord.VehicularCapacityRecordBuilder createPedestrianRecord(
            MovementCounter movementCounter) {
        return VehicularCapacityRecord.builder()
                .numberOfBikes(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.BIKE.name()).flushPartialCount())
                .numberOfBikesFemale(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.BIKE_FEMALE.name()).flushPartialCount())
                .numberOfPedestrians(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.PEDESTRIAN.name()).flushPartialCount())
                .numberOfPedestriansFemale(movementCounter.getCounterStatusPerVehicle()
                        .get(UnderStudyVehicles.PEDESTRIAN_FEMALE.name()).flushPartialCount());
    }
}