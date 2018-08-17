package com.example.josuerey.helloworld;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MetadataRepository {

    private MetadataDao metadataDao;
    private LiveData<List<MetadataDao>> allMetadata;

    MetadataRepository(Application application) {

    }

}
