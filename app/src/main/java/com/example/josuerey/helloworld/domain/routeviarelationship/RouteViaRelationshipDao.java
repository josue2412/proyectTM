package com.example.josuerey.helloworld.domain.routeviarelationship;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;

@Dao
public interface RouteViaRelationshipDao {

    @Query("SELECT * FROM BusRoute INNER JOIN RouteViaRelationship ON " +
            "BusRoute.id = RouteViaRelationship.viaOfStudyId " +
            "where RouteViaRelationship.viaOfStudyId = :viaOfStudy")
    BusRoute[] findRoutesForViaOfStudy(final int viaOfStudy);

    @Query("Select * from RouteViaRelationship")
    RouteViaRelationship[] findAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RouteViaRelationship... dataEntities);
}
