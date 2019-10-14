package com.sgcities.tdc.optimizer.domain.pointOfStudy;

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
public class PointOfStudy {

    @SerializedName("id")
    private int id;

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("description")
    private String description;

    @SerializedName("intersection_image_url")
    private String intersectionImageURL;

    @SerializedName("capturist_responsible_id")
    private int capturistResponsibleId;

    @SerializedName("enabled")
    private int enabled;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

}
