package com.example.josuerey.helloworld.domain.routeviarelationship;

import android.app.Application;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;
import com.example.josuerey.helloworld.domain.uRoomDatabase;

public class RouteViaRelationshipRepository {
    private RouteViaRelationshipDao routeViaRelationshipDao;

    public RouteViaRelationshipRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        routeViaRelationshipDao = db.routeViaRelationshipDao();
    }

    public BusRoute[] findRoutesByViaOfStudyId(int viaOfStudyId){

        return routeViaRelationshipDao.findRoutesForViaOfStudy(viaOfStudyId);
    }

    public RouteViaRelationship[] findAll() {

        return routeViaRelationshipDao.findAll();
    }
}
