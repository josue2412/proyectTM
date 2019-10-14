package com.sgcities.tdc.optimizer.domain.gpslocation;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class GPSLocationViewModel extends AndroidViewModel {

    private GPSLocationRepository gpsLocationRepository;

    public GPSLocationViewModel(@NonNull Application application) {
        super(application);
        gpsLocationRepository = new GPSLocationRepository(application);
    }

    public LiveData<List<GPSLocation>> findGPSLocationsByMetadataId(int idMetadata) {

        return  gpsLocationRepository.findGPSLocationsByMetadataId(idMetadata);
    }
}
