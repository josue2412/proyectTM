package com.example.josuerey.helloworld.domain.visualoccupation;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

public class VisualOccupationMetadataRepository {

    private VisualOccupationMetadataDao visualOccupationMetadataDao;

    public VisualOccupationMetadataRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        visualOccupationMetadataDao = db.visualOccupationMetadataDao();
    }

    public long save(VisualOccupationMetadata visualOccupationMetadata) {
        return visualOccupationMetadataDao.insert(visualOccupationMetadata);
    }

    public void updateVisualOccMetadata(VisualOccupationMetadata[] metadata) {

        visualOccupationMetadataDao.updateVisualOccupationMetadata(metadata);
    }
}
