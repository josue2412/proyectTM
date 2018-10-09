package com.example.josuerey.helloworld.domain.busoccupation;

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
@Entity(tableName = "BusOccupation")
public class BusOccupation {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "studyMetadataId")
    private int studyMetadataId;

    @ColumnInfo(name = "economicNumber")
    private String economicNumber = "Desconocido";

    @ColumnInfo(name = "route")
    private String route;

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
        Log.d("BusOccupation", "Successfully backed up in remote server id: " + this.getId());
    }
}
