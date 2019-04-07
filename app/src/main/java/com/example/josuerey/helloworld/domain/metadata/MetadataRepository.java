package com.example.josuerey.helloworld.domain.metadata;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;
import com.example.josuerey.helloworld.infrastructure.network.AscDescAssignmentResponse;

public class MetadataRepository {
    private MetadataDao metadataDao;

    public MetadataRepository(Application application) {
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        metadataDao = db.metadataDao();
    }

    public long insert (Metadata metadata) {
        return metadataDao.insert(metadata);
    }

    public Metadata[] findMetadataByBackedUpRemotely(int value) {

        return metadataDao.findMetadataByBackedUpRemotely(value);
    }

    public Metadata findMetadataByAssignmentId(final int assignmentId) {
        return metadataDao.findMetadataByAssignmentId(assignmentId);
    }

    public void updateMetadataInBatch(Metadata[] metadata) {

        metadataDao.updateMetadata(metadata);
    }

    public Metadata updateMetadataFromAssignment(final AscDescAssignmentResponse assignmentResponse,
                                                 final int metadataId) {
        Metadata updatedMetadata =  Metadata.builder()
                .id(metadataId)
                .durationInHours(assignmentResponse.getDurationInHours())
                .assignmentId(assignmentResponse.getId())
                .beginAtDate(assignmentResponse.getBeginAtDate())
                .beginAtPlace(assignmentResponse.getBeginAtPlace())
                .economicNumber(assignmentResponse.getEconomicNumber())
                .via(assignmentResponse.getVia())
                .route(assignmentResponse.getRoute()).build();

        metadataDao.updateMetadata(updatedMetadata);
        return updatedMetadata;
    }
}
