package com.example.josuerey.helloworld.application.origindestiny;

import com.example.josuerey.helloworld.domain.capturist.Capturist;
import com.example.josuerey.helloworld.domain.pointOfStudy.PointOfStudy;
import com.example.josuerey.helloworld.infrastructure.network.ServerAssignmentResponse;
import com.google.gson.annotations.SerializedName;

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
public class OriginDestinyAssignmentResponse implements ServerAssignmentResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("point_of_study_id")
    private int pointOfStudyId;

    @SerializedName("capturist_id")
    private int capturistId;

    @SerializedName("questionary_id")
    private int pollId;

    @SerializedName("number_of_polls")
    private int numberOfPolls;

    @SerializedName("begin_at_date")
    private String beginAtDate;

    @SerializedName("begin_at_place")
    private String beginAtPlace;

    @SerializedName("observations")
    private String observations;

    @SerializedName("is_editable")
    private int isEditable;

    @SerializedName("enabled")
    private int enabled;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("point_of_study")
    private PointOfStudy pointOfStudy;

    @SerializedName("capturist")
    private Capturist capturist;

    @SerializedName("questionary")
    private OriginDestinyPoll originDestinyPoll;


}
