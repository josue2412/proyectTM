package com.example.josuerey.helloworld;

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

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "driver")
    private String driver;

    @ColumnInfo(name = "route")
    private String route;

    @ColumnInfo(name = "via")
    private String via;

    @ColumnInfo(name = "startTime")
    private LocalDateTime startTime;

    @ColumnInfo(name = "finishTime")
    private LocalDateTime finishTime;

    /**
     * Public metadata constructor.
     *
     * @param driver name of the person who fills the form.
     * @param route the bus follow.
     * @param via of the route.
     */
    public Metadata(@NonNull String driver,
                    @NonNull String route,
                    @NonNull String via){
        this.driver = driver;
        this.route = route;
        this.via = via;
    }
}
