package com.example.josuerey.helloworld.domain.visualoccupation;

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
@Entity(tableName = "VisualOccupationMetadata")
public class VisualOccupationMetadata {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "viaOfStudy")
    private String viaOfStudy;

    @ColumnInfo(name = "directionLane")
    private String directionLane;

    @ColumnInfo(name = "crossroads")
    private String crossroads;

    @ColumnInfo(name = "observations")
    private String observations;

    @ColumnInfo(name = "waterConditions")
    private String waterConditions;

    @ColumnInfo(name = "capturist")
    private String capturist;

    @ColumnInfo(name = "deviceId")
    private String deviceId;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;

    @ColumnInfo(name = "timeStamp")
    private String timeStamp;

    public void remotelyBackedUpSuccessfully() {

        this.backedUpRemotely = 1;
        Log.d("VisualOccMetadata", "Successfully backed up in remote server.");
    }
}
