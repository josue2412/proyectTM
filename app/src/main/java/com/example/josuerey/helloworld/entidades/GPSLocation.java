package com.example.josuerey.helloworld.entidades;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.time.LocalDateTime;

@Entity(tableName = "GPSLocation")
public class GPSLocation {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "idMetadata")
    private int idMetadata;

    @ColumnInfo(name = "timeStamp")
    private String timeStamp;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lon")
    private double lon;

    public GPSLocation(int idMetadata, String timeStamp, double lat, double lon) {
        this.idMetadata = idMetadata;
        this.timeStamp = timeStamp;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public int getIdMetadata() {
        return idMetadata;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GPSLocation{" +
                "idMetadata=" + idMetadata +
                ", timeStamp='" + timeStamp + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
