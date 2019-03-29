package com.example.josuerey.helloworld.domain.busroute;

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
public class RouteBusPayload {
    @SerializedName("id")
    private int id;

    @SerializedName("route")
    private String route;

    @SerializedName("via")
    private String via;

    @SerializedName("enabled")
    private int enabled;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("pivot")
    private Pivot pivot;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static final class Pivot {
        @SerializedName("vis_occ_assignment_id")
        private String visOccAssignmentId;

        @SerializedName("route_bus_id")
        private String routeBusId;
    }
}