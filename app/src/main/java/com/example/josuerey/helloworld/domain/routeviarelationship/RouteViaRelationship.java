package com.example.josuerey.helloworld.domain.routeviarelationship;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;

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
@Entity(
        tableName = "RouteViaRelationship",
        primaryKeys = {"routeId","viaOfStudyId"},
        foreignKeys = {
                @ForeignKey(entity = BusRoute.class,
                parentColumns = "id",
                childColumns = "routeId"),
                @ForeignKey(entity = ViaOfStudy.class,
                parentColumns = "id",
                childColumns = "viaOfStudyId")
        }
)
public class RouteViaRelationship {

    private int routeId;
    private int viaOfStudyId;

}
