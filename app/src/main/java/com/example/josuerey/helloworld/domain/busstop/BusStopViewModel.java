package com.example.josuerey.helloworld.domain.busstop;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class BusStopViewModel extends AndroidViewModel {

    private BusStopRepository busStopRepository;
    private LiveData<List<BusStop>> allBusStops;

    public BusStopViewModel(Application application) {
        super(application);
        busStopRepository = new BusStopRepository(application);
        allBusStops = busStopRepository.getAllBusStops();
    }

    public LiveData<List<BusStop>> getAllBusStops() { return allBusStops; }

    public LiveData<List<BusStop>> findBusStopsByMetadata(int idMetadata) {

        return  busStopRepository.findBusStopsByMetadata(idMetadata);
    }
}
