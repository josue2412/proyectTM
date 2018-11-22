package com.example.josuerey.helloworld.domain.assignment;

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

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity(tableName = "Assignment")
public class Assignment {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "serverId")
    private int serverId;

    @ColumnInfo(name = "capturistId")
    private int capturistId;

    @ColumnInfo(name = "projectId")
    private int projectId;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "timeOfStudy")
    private String timeOfStudy;

    @ColumnInfo(name = "beginAt")
    private String beginAt;

    @ColumnInfo(name = "durationInHours")
    private int durationInHours;

    @ColumnInfo(name = "streetFrom")
    private String streetFrom;

    @ColumnInfo(name = "streetTo")
    private String streetTo;

    @ColumnInfo(name = "streetFromDirection")
    private String streetFromDirection;

    @ColumnInfo(name = "streetToDirection")
    private String streetToDirection;

    @ColumnInfo(name = "streetFromCode")
    private String streetFromCode;

    @ColumnInfo(name = "streetToCode")
    private String streetToCode;

    @ColumnInfo(name = "movement")
    private String movement;

    @ColumnInfo(name = "movementCode")
    private int movementCode;

    @ColumnInfo(name = "numberOfMovements")
    private int numberOfMovements;

    @ColumnInfo(name = "enabled")
    private int enabled;

    @ColumnInfo(name = "createdAt")
    private String createdAt;

    @ColumnInfo(name = "updatedAt")
    private String updatedAt;
}
