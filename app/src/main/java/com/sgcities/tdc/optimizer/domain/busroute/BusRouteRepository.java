package com.sgcities.tdc.optimizer.domain.busroute;

import android.app.Application;

import com.sgcities.tdc.optimizer.domain.uRoomDatabase;

public class BusRouteRepository {

    private BusRouteDao busRouteDao;

    public BusRouteRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        busRouteDao = db.busRouteDao();
    }

    public BusRoute[] findAll() {
        return busRouteDao.findAll();
    }
}
