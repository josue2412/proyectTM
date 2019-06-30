package com.example.josuerey.helloworld.domain.origindestiny;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.josuerey.helloworld.domain.shared.Storable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity(tableName = "OriginDestinyPollAnswer")
public class OriginDestinyPollAnswer {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "question_id")
    private int questionId;

    @ColumnInfo(name = "answer_given")
    private String answerGiven;

}
