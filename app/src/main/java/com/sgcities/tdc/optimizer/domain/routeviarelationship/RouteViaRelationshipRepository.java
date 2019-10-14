package com.sgcities.tdc.optimizer.domain.routeviarelationship;

import android.app.Application;

import com.sgcities.tdc.optimizer.domain.busroute.BusRoute;
import com.sgcities.tdc.optimizer.domain.uRoomDatabase;

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
