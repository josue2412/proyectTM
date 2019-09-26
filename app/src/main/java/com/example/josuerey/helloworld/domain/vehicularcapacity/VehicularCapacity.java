package com.example.josuerey.helloworld.domain.vehicularcapacity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.example.josuerey.helloworld.domain.shared.BaseEntity;

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
@Entity(tableName = "VehicularCapacity")
public class VehicularCapacity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "assignmentId")
    private int assignmentId;

    @ColumnInfo(name = "viaOfStudy")
    private String viaOfStudy;

    @ColumnInfo(name = "directionLane")
    private String directionLane;

    @ColumnInfo(name = "vehicleMove")
    private String vehicleMove;

    @ColumnInfo(name = "deviceId")
    private String deviceId;

    @ColumnInfo(name = "observations")
    private String observations;

    @ColumnInfo(name = "timeStamp")
    private String timeStamp;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;


    public void remotelyBackedUpSuccessfully() {

        this.backedUpRemotely = 1;
        Log.d("VehicularCapacity", String.format(
                "Record with id: %d was successfully backed up in remote server.", this.id));
    }
}
