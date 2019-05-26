package com.example.josuerey.helloworld.domain.busstop;

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
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity(tableName = "BusStop")
public class BusStop {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "assignmentId")
    private int assignmentId;

    @ColumnInfo(name = "deviceId")
    private String deviceId;

    @ColumnInfo(name = "stopType")
    private String stopType;

    @ColumnInfo(name = "passengersUp")
    private int passengersUp;

    @ColumnInfo(name = "passengersDown")
    private int passengersDown;

    @ColumnInfo(name = "totalPassengers")
    private int totalPassengers;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lon")
    private double lon;

    @ColumnInfo(name = "isOfficial")
    private boolean isOfficial;

    @ColumnInfo(name = "stopBegin")
    private String stopBegin;

    @ColumnInfo(name = "stopEnd")
    private String stopEnd;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;

    public void remotelyBackedUpSuccessfully() {

        this.backedUpRemotely = 1;
        Log.d("BusStop", this.id + " Successfully backed up in remote server.");
    }
}
