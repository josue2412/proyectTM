package com.example.josuerey.helloworld.domain.gpslocation;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

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

    public long insert (GPSLocation gpsLocation) {
        return gpsLocationDao.insert(gpsLocation);
    }

    public void updateGPSLocationBackupRemotelyById(GPSLocation[] gpsLocations) {

        gpsLocationDao.updateGPSLocation(gpsLocations);
    }

    public LiveData<List<GPSLocation>> findGPSLocationsByMetadataId(int metadataId) {

        return gpsLocationDao.findGPSLocationsById(metadataId);
    }

    public GPSLocation[] findGPSLocationByBackedUpRemotely(int value) {

        return gpsLocationDao.findGPSLocationByBackedUpRemotely(value);
    }

}

