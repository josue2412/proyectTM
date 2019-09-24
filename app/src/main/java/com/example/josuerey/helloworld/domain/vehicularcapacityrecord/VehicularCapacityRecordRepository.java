package com.example.josuerey.helloworld.domain.vehicularcapacityrecord;

import android.app.Application;

import com.example.josuerey.helloworld.application.vehicularcap.UnderStudyVehicles;
import com.example.josuerey.helloworld.application.vehicularcap.MovementCounter;
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
