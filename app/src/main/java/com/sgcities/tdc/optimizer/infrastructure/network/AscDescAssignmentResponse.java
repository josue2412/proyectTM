package com.sgcities.tdc.optimizer.infrastructure.network;

import com.sgcities.tdc.optimizer.domain.capturist.Capturist;
import com.sgcities.tdc.optimizer.domain.pointOfStudy.PointOfStudy;
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
public class AscDescAssignmentResponse implements ServerAssignmentResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("capturist_id")
    private int capturistId;

    @SerializedName("point_of_study_id")
    private int pointOfStudyId;

    @SerializedName("route")
    private String route;

    @SerializedName("via")
    private String via;

    @SerializedName("economic_number")
    private String economicNumber;

    @SerializedName("is_editable")
    private int isEditable;

    @SerializedName("initial_passengers")
    private int initialPassengers;

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

    @SerializedName("point_of_study")
    private PointOfStudy pointOfStudy;
}
