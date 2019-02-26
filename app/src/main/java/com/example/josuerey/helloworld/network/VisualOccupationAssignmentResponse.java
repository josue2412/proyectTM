package com.example.josuerey.helloworld.network;

import com.example.josuerey.helloworld.domain.capturist.Capturist;
import com.example.josuerey.helloworld.domain.project.Project;
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
public class VisualOccupationAssignmentResponse implements ServerAssignmentResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("capturist_id")
    private int capturistId;

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("via_of_study")
    private String viaOfStudy;

    @SerializedName("direction_lane")
    private String directionLane;

    @SerializedName("crossroads")
    private String crossroads;

    @SerializedName("observations")
    private String observations;

    @SerializedName("waterConditions")
    private String waterConditions;

    @SerializedName("is_editable")
    private int isEditable;

    @SerializedName("begin_at_date")
    private String beginAtDate;

    @SerializedName("begin_at_place")
    private String beginAtPlace;

    @SerializedName("duration_in_hours")
    private int durationInHours;

    @SerializedName("enabled")
    private int enabled;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("capturist")
    private Capturist capturist;

    @SerializedName("project")
    private Project project;
}

