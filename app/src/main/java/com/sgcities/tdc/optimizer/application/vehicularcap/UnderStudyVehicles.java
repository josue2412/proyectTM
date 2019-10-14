package com.sgcities.tdc.optimizer.application.vehicularcap;

import com.sgcities.tdc.optimizer.application.shared.StudyType;

/**
 * Vehicles available to a vehicular capacity study
 * {@linkplain com.sgcities.tdc.optimizer.domain.vehicularcapacityrecord.VehicularCapacityRecord}
 */
public enum UnderStudyVehicles {

    CAR(StudyType.VEHICULAR), BUS(StudyType.VEHICULAR), TRUCK(StudyType.VEHICULAR),
    MOTORCYCLE(StudyType.VEHICULAR), BIKE(StudyType.PEATONAL), BIKE_FEMALE(StudyType.PEATONAL),
    PEDESTRIAN(StudyType.PEATONAL), PEDESTRIAN_FEMALE(StudyType.PEATONAL);

    private final StudyType typeOfStudy;

    UnderStudyVehicles(StudyType typeOfStudy) {
        this.typeOfStudy = typeOfStudy;
    }

    public StudyType getTypeOfStudy() {
        return typeOfStudy;
    }
}
