package com.example.josuerey.helloworld.entidades;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

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
