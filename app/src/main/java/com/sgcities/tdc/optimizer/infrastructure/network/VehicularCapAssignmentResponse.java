package com.sgcities.tdc.optimizer.infrastructure.network;

import com.sgcities.tdc.optimizer.domain.capturist.Capturist;
import com.sgcities.tdc.optimizer.domain.movement.Movement;
import com.sgcities.tdc.optimizer.domain.pointOfStudy.PointOfStudy;
import com.google.gson.annotations.SerializedName;

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
public class VehicularCapAssignmentResponse implements ServerAssignmentResponse{
    @SerializedName("id")
    private int id;

    @SerializedName("capturist_id")
    private int capturistId;

    @SerializedName("point_of_study_id")
    private int pointOfStudyId;

    @SerializedName("begin_at_date")
    private String beginAtDate;

    @SerializedName("begin_at_place")
    private String beginAtPlace;

    @SerializedName("duration_in_hours")
    private String durationInHours;

    @SerializedName("enabled")
    private int enabled;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("capturist")
    private Capturist capturist;

    @SerializedName("point_of_study")
    private PointOfStudy pointOfStudy;

    @SerializedName("movements")
    private List<Movement> movements;

}