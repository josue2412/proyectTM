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

    public Metadata[] findAllMetadata() {
        return metadataDao.loadAllMetadata();
    }

    public void updateMetadataBackedUpSuccessById(int metadataId) {

        metadataDao.updateMetadataBackupRemotelyById(metadataId);
    }

    public Metadata[] findMetadataByBackedUpRemotely(int value) {

        return metadataDao.findMetadataByBackedUpRemotely(value);
    }
}
