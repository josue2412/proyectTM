package com.example.josuerey.helloworld.entidades;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "BusStop")
public class BusStop {

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "idMetadata")
    private int idMetadata;

    @ColumnInfo(name = "timeStamp")
    private String timeStamp;

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

    @Override
    public String toString() {
        return "BusStop{" +
                "id=" + id +
                ", idMetadata=" + idMetadata +
                ", timeStamp='" + timeStamp + '\'' +
                ", stopType='" + stopType + '\'' +
                ", passengersUp=" + passengersUp +
                ", passengersDown=" + passengersDown +
                ", totalPassengers=" + totalPassengers +
                ", lat=" + lat +
                ", lon=" + lon +
                ", isOfficial=" + isOfficial +
                '}';
    }

    @ColumnInfo(name = "isOfficial")
    private boolean isOfficial;

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getStopType() {
        return stopType;
    }

    public int getPassengersUp() {
        return passengersUp;
    }

    public int getPassengersDown() {
        return passengersDown;
    }

    public int getTotalPassengers() {
        return totalPassengers;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getId() {
        return id;
    }

    public int getIdMetadata() {
        return idMetadata;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    /**
     * Constructor
     *
     * @param idMetadata
     * @param timeStamp
     * @param stopType
     * @param passengersUp
     * @param passengersDown
     * @param totalPassengers
     * @param lat
     * @param lon
     * @param isOfficial
     */
    public BusStop(int idMetadata, String timeStamp, String stopType, int passengersUp,
                   int passengersDown, int totalPassengers, double lat, double lon,
                   boolean isOfficial) {
        this.idMetadata = idMetadata;
        this.timeStamp = timeStamp;
        this.stopType = stopType;
        this.passengersUp = passengersUp;
        this.passengersDown = passengersDown;
        this.totalPassengers = totalPassengers;
        this.lat = lat;
        this.lon = lon;
        this.isOfficial = isOfficial;
    }


}
