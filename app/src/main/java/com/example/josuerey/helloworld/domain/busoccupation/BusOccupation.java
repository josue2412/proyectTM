package com.example.josuerey.helloworld.domain.busoccupation;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.example.josuerey.helloworld.domain.shared.Storable;

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
@Entity(tableName = "BusOccupation")
public class BusOccupation implements Storable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "visOccAssignmentId")
    private int visOccAssignmentId;

    @ColumnInfo(name = "routeId")
    private int routeId;

    @ColumnInfo(name = "economicNumber")
    private String economicNumber;

    @ColumnInfo(name = "occupationLevel")
    private String occupationLevel;

    @ColumnInfo(name = "busType")
    private String busType;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lon")
    private double lon;

    @ColumnInfo(name = "timeStamp")
    private String timeStamp;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;

    public void remotelyBackedUpSuccessfully() {

        this.backedUpRemotely = 1;
        Log.d("BusOccupation", String.format(
                "Record with id: %d was successfully backed up in remote server.", this.id));
    }
}
