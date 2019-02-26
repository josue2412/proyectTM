package com.example.josuerey.helloworld.domain.visualoccupation;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;
import com.example.josuerey.helloworld.network.VisualOccupationAssignmentResponse;

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

    public VisualOccupationMetadata findByAssignmentId(final int assignmentId) {

        return visualOccupationMetadataDao.findByAssignmentId(assignmentId);
    }

    public VisualOccupationMetadata[] findPendingToBackup() {

        return visualOccupationMetadataDao.findRecordsPendingToBackup(0);
    }

    public VisualOccupationMetadata updateMetadataFromAssignment(
            final VisualOccupationAssignmentResponse existingVisualOccResponse,
            final int visualOccMetadataAssignmentId) {

        VisualOccupationMetadata visOccMetadata = VisualOccupationMetadata.builder()
                .id(visualOccMetadataAssignmentId)
                .assignmentId(existingVisualOccResponse.getId())
                .directionLane(existingVisualOccResponse.getDirectionLane())
                .viaOfStudy(existingVisualOccResponse.getViaOfStudy())
                .crossroads(existingVisualOccResponse.getCrossroads())
                .beginAtDate(existingVisualOccResponse.getBeginAtDate())
                .beginAtPlace(existingVisualOccResponse.getBeginAtPlace())
                .durationInHours(existingVisualOccResponse.getDurationInHours())
                .build();

        visualOccupationMetadataDao.updateVisualOccupationMetadata(visOccMetadata);
        return visOccMetadata;
    }
}
