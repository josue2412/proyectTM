package com.example.josuerey.helloworld.entidades;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class BusStopRepository {

    private BusStopDao busStopDao;
    private LiveData<List<BusStop>> allBusStops;

    public BusStopRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        busStopDao = db.busStopDao();
        allBusStops = busStopDao.getAllBusStops();
    }

    LiveData<List<BusStop>> getAllBusStops() {
        return allBusStops;
    }

    public void insert (BusStop busStop) {
        new BusStopRepository.insertAsyncTask(busStopDao).execute(busStop);
    }

    private static class insertAsyncTask extends AsyncTask<BusStop, Void, Void> {

        private BusStopDao mAsyncTaskDao;

        insertAsyncTask(BusStopDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BusStop... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
