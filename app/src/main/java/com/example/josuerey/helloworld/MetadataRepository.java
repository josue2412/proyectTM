package com.example.josuerey.helloworld;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.example.josuerey.helloworld.entidades.MetadataDao;

import java.util.List;

public class MetadataRepository {

    private MetadataDao metadataDao;
    private LiveData<List<MetadataDao>> allMetadata;

    MetadataRepository(Application application) {

    }

}
