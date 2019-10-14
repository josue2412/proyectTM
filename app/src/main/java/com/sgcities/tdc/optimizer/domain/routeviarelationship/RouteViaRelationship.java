package com.sgcities.tdc.optimizer.domain.routeviarelationship;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import com.sgcities.tdc.optimizer.domain.busroute.BusRoute;
import com.sgcities.tdc.optimizer.domain.viaofstudy.ViaOfStudy;

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

    @ColumnInfo(name = "routeId")
    private int routeId;

    @ColumnInfo(name = "viaOfStudyId")
    private int viaOfStudyId;

}
