package com.example.josuerey.helloworld.domain.origindestiny;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.josuerey.helloworld.domain.shared.Storable;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OriginDestinyPoll implements Storable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "assignment_id")
    private int assignmentId;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lon")
    private double lon;

    @ColumnInfo(name = "timeStamp")
    private String timeStamp;

    @ColumnInfo(name = "backedUpRemotely")
    private int backedUpRemotely;

    @ColumnInfo(name = "answers_given")
    private List<OriginDestinyPollAnswer> answers;
}
