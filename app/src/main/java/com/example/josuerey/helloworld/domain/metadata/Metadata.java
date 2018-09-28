package com.example.josuerey.helloworld.domain.metadata;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class wraps the header information of the sensing form.
 *
 * @author josuerey
 * @version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity(tableName = "Metadata")
public class Metadata {

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

    @ColumnInfo(name = "deviceId")
    private String deviceId;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;

}
