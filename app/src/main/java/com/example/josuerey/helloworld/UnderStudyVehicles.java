package com.example.josuerey.helloworld;

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
