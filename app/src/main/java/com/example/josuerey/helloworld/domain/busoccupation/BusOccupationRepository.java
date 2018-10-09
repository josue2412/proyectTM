package com.example.josuerey.helloworld.domain.busoccupation;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

public class BusOccupationRepository {

    private BusOccupationDao busOccupationDao;

    public BusOccupationRepository(Application application) {

        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        busOccupationDao = db.busOccupationDao();
    }

    public long save(BusOccupation busOccupation) {

        return busOccupationDao.insert(busOccupation);
    }

    public void updateBusOccRecordsBackedUp(BusOccupation[] busOccupations) {

        busOccupationDao.updateBusOccupation(busOccupations);
    }
}
