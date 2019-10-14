package com.sgcities.tdc.optimizer.domain.busstop;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.sgcities.tdc.optimizer.domain.uRoomDatabase;
import com.sgcities.tdc.optimizer.infrastructure.persistence.RemotelyStore;

import java.util.List;

public class BusStopRepository implements RemotelyStore<BusStop> {

    private static final String TAG = BusStopRepository.class.getName();
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


    public BusStop[] findBusStopByBackedUpRemotely(int value) {

        return busStopDao.findBusStopsByBackedUpRemotely(value);
    }

    @Override
    public void backedUpRemotely(List<BusStop> records) {

        this.updateBusStopInBatch(records.toArray(new BusStop[records.size()]));
    }

    @Override
    public List<BusStop> findRecordsPendingToBackUp() {

        return busStopDao.findRecordsPendingToBackup();
    }

    public void updateBusStopInBatch(BusStop[] busStops) {

        busStopDao.updateBusStop(busStops);
    }
}
