package com.example.josuerey.helloworld.entidades;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class GPSLocationRepository {

    private GPSLocationDao gpsLocationDao;
    private LiveData<List<GPSLocation>> gPSLocationsById;

    /**
     * Constructor
     * @param application
     */
    public GPSLocationRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        gpsLocationDao = db.gpsLocationDao();
    }

    public void insert (GPSLocation gpsLocation) {
        new insertAsyncTask(gpsLocationDao).execute(gpsLocation);
    }

    public LiveData<List<GPSLocation>> findGPSLocationsByMetadataId(int metadataId) {

        return gpsLocationDao.findGPSLocationsById(metadataId);
    }

    private static class insertAsyncTask extends AsyncTask<GPSLocation, Void, Void> {

        private GPSLocationDao mAsyncTaskDao;

        insertAsyncTask(GPSLocationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final GPSLocation... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}

