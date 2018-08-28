package com.example.josuerey.helloworld.domain.metadata;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

public class MetadataRepository {
    private MetadataDao metadataDao;

    public MetadataRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        metadataDao = db.metadataDao();
    }

    public long insert (Metadata metadata) {
        return metadataDao.insert(metadata);
    }

    public LiveData<Metadata> findMetadataById(int metadataId) {
        return metadataDao.findMetadataById(metadataId);
    }
}