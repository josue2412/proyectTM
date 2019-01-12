package com.example.josuerey.helloworld.domain.vehicularcapacityrecord;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

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

    @ColumnInfo(name = "movementId")
    private int movementId;

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

    @ColumnInfo(name = "beginTimeInterval")
    private String beginTimeInterval;

    @ColumnInfo(name = "endTimeInterval")
    private String endTimeInterval;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lon")
    private double lon;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;

    public void remotelyBackedUpSuccessfully() {

        this.backedUpRemotely = 1;
        Log.d("VehicularCapacityRecord", this.id + " Successfully backed up in remote server.");
    }

    @Override
    public String toString() {

        StringBuilder objectAsString = new StringBuilder();
        objectAsString.append("Id: " + this.id);
        objectAsString.append(", Inicio: " + this.beginTimeInterval);
        objectAsString.append(", Fin: " + this.endTimeInterval);
        objectAsString.append(", Carros: " + String.valueOf(numberOfCars));
        objectAsString.append(", Autobuses: " + String.valueOf(numberOfBusses));
        objectAsString.append(", Motocicletas: " + String.valueOf(numberOfMotorcycles));
        objectAsString.append(", Bicicletas: " + String.valueOf(numberOfBikes));
        objectAsString.append(", Camiones: " + String.valueOf(numberOfTrucks));
        objectAsString.append(", Latitud: " + String.valueOf(lat));
        objectAsString.append(", Longitud: " + String.valueOf(lon));

        return objectAsString.toString();
    }
}
