package com.example.josuerey.helloworld.domain.vehicularcapacityrecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity(tableName = "VehicularCapacityRecord")
public class VehicularCapacityRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "deviceId")
    private String deviceId;

    @ColumnInfo(name = "assignmentId")
    private int assignmentId;

    @ColumnInfo(name = "movement")
    private String movement;

    @ColumnInfo(name = "numberOfCars")
    private int numberOfCars;

    @ColumnInfo(name = "numberOfBusses")
    private int numberOfBusses;

    @ColumnInfo(name = "numberOfMotorcycles")
    private int numberOfMotorcycles;

    @ColumnInfo(name = "numberOfBikes")
    private int numberOfBikes;

    @ColumnInfo(name = "numberOfTrucks")
    private int numberOfTrucks;

    @ColumnInfo(name = "numberOfPedestrians")
    private int numberOfPedestrians;

    @ColumnInfo(name = "beginTimeInterval")
    private String beginTimeInterval;

    @ColumnInfo(name = "endTimeInterval")
    private String endTimeInterval;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;

    public void remotelyBackedUpSuccessfully() {

        this.backedUpRemotely = 1;
        Log.d("VehicularCapacityRecord", this.id + " Successfully backed up in remote server.");
    }

    @Override
    public String toString() {
        StringBuilder objectAsString = new StringBuilder();
        objectAsString.append("Inicio: " + this.beginTimeInterval);
        objectAsString.append("\nCarros: " + String.valueOf(numberOfCars));
        objectAsString.append("\nAutobuses: " + String.valueOf(numberOfBusses));
        objectAsString.append("\nMotocicletas: " + String.valueOf(numberOfMotorcycles));
        objectAsString.append("\nBicicletas: " + String.valueOf(numberOfBikes));
        objectAsString.append("\nCamiones: " + String.valueOf(numberOfTrucks));
        objectAsString.append("\nPeatones: " + String.valueOf(numberOfPedestrians));
        objectAsString.append("\nFin: " + this.endTimeInterval);
        return objectAsString.toString();
    }
}
