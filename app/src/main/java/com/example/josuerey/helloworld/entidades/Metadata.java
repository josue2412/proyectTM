package com.example.josuerey.helloworld.entidades;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.time.LocalDateTime;

/**
 * This class wraps the header information of the sensing form.
 *
 * @author josuerey
 * @version 1.0
 */
@Entity(tableName = "Metadata")
public class Metadata {

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "driver")
    private String driver;

    @ColumnInfo(name = "route")
    private String route;

    @ColumnInfo(name = "via")
    private String via;

    @ColumnInfo(name = "startTime")
    private String startTime;

    @ColumnInfo(name = "finishTime")
    private String finishTime;

    public int getId() {
        return id;
    }

    public String getDriver() {
        return driver;
    }

    public String getRoute() {
        return route;
    }

    public String getVia() {
        return via;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    /**
     * Public metadata constructor.
     *
     * @param driver name of the person who fills the form.
     * @param route the bus follow.
     * @param via of the route.
     */
    public Metadata(@NonNull String driver,
                    @NonNull String route,
                    @NonNull String via,
                    @NonNull String startTime,
                    @NonNull String finishTime){
        this.driver = driver;
        this.route = route;
        this.via = via;
        this.startTime = startTime;
        this.finishTime = finishTime;

    }
}
