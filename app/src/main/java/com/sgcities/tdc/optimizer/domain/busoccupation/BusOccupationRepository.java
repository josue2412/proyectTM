package com.sgcities.tdc.optimizer.domain.busoccupation;

import android.app.Application;

import com.sgcities.tdc.optimizer.domain.uRoomDatabase;
import com.sgcities.tdc.optimizer.infrastructure.persistence.RemotelyStore;

import java.util.List;

public class BusOccupationRepository implements RemotelyStore<BusOccupation> {

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

    @Override
    public void backedUpRemotely(List<BusOccupation> records) {

        this.updateBusOccRecordsBackedUp(records.toArray(new BusOccupation[records.size()]));
    }

    @Override
    public List<BusOccupation> findRecordsPendingToBackUp(){

        return busOccupationDao.findPendingToBackup();
    }
}
