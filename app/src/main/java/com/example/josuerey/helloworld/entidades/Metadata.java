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

    @ColumnInfo(name = "route")
    private String route;

    @ColumnInfo(name = "via")
    private String via;

    @ColumnInfo(name = "economicNumber")
    private String economicNumber;

    @ColumnInfo(name = "capturist")
    private String capturist;


    public int getId() {
        return id;
    }

    public String getRoute() {
        return route;
    }

    public String getVia() {
        return via;
    }

    public String getEconomicNumber() {
        return economicNumber;
    }

    public String getCapturist() {
        return capturist;
    }


    /**
     *
     * @param route
     * @param via
     * @param economicNumber
     * @param capturist
     */
    public Metadata(@NonNull String route,
                    @NonNull String via,
                    @NonNull String economicNumber,
                    @NonNull String capturist){
        // this.driver = driver;
        this.route = route;
        this.via = via;
        this.economicNumber = economicNumber;
        this.capturist = capturist;

    }
}
