package com.example.josuerey.helloworld.domain.busstop;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

import java.util.List;

public class BusStopRepository {

    private BusStopDao busStopDao;
    private LiveData<List<BusStop>> allBusStops;

    public BusStopRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        busStopDao = db.busStopDao();
        allBusStops = busStopDao.findAllBusStops();
    }

    public LiveData<List<BusStop>> getAllBusStops() { return allBusStops; }

    public LiveData<List<BusStop>> findBusStopsByMetadata(int metadata) {
        return busStopDao.findBusStopsByMetadataId(metadata);
    }

    public long insert (BusStop busStop) {
        return busStopDao.insert(busStop);
    }

    public void updateBusStopBackedUpSuccessById(int busStopId) {

        busStopDao.updateBusStopBackupRemotelyById(busStopId);
    }

    public BusStop[] findBusStopByBackedUpRemotely(int value) {

        return busStopDao.findBusStopsByBackedUpRemotely(value);
    }
}
