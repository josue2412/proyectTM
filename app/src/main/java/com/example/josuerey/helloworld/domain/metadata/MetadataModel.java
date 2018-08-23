package com.example.josuerey.helloworld.domain.metadata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

public class MetadataModel extends AndroidViewModel {

    private MetadataRepository metadataRepository;

    public MetadataModel(Application application) {
        super(application);
        metadataRepository = new MetadataRepository(application);
    }

    public LiveData<Metadata> findMetadataById(int idMetadata) {

        return  metadataRepository.findMetadataById(idMetadata);
    }

}
