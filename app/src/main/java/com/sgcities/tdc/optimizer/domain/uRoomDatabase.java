package com.sgcities.tdc.optimizer.domain;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sgcities.tdc.optimizer.domain.assignment.Assignment;
import com.sgcities.tdc.optimizer.domain.assignment.AssignmentDao;
import com.sgcities.tdc.optimizer.domain.busoccupation.BusOccupation;
import com.sgcities.tdc.optimizer.domain.busoccupation.BusOccupationDao;
import com.sgcities.tdc.optimizer.domain.busroute.BusRoute;
import com.sgcities.tdc.optimizer.domain.busroute.BusRouteDao;
import com.sgcities.tdc.optimizer.domain.busstop.BusStop;
import com.sgcities.tdc.optimizer.domain.busstop.BusStopDao;
import com.sgcities.tdc.optimizer.domain.gpslocation.GPSLocation;
import com.sgcities.tdc.optimizer.domain.gpslocation.GPSLocationDao;
import com.sgcities.tdc.optimizer.domain.metadata.Metadata;
import com.sgcities.tdc.optimizer.domain.metadata.MetadataDao;
import com.sgcities.tdc.optimizer.domain.origindestiny.poll.OriginDestinyPoll;
import com.sgcities.tdc.optimizer.domain.origindestiny.answer.OriginDestinyPollAnswer;
import com.sgcities.tdc.optimizer.domain.origindestiny.answer.OriginDestinyPollAnswerDao;
import com.sgcities.tdc.optimizer.domain.origindestiny.poll.OriginDestinyPollDao;
import com.sgcities.tdc.optimizer.domain.routeviarelationship.RouteViaRelationship;
import com.sgcities.tdc.optimizer.domain.routeviarelationship.RouteViaRelationshipDao;
import com.sgcities.tdc.optimizer.domain.vehicularcapacity.VehicularCapacity;
import com.sgcities.tdc.optimizer.domain.vehicularcapacity.VehicularCapacityDao;
import com.sgcities.tdc.optimizer.domain.vehicularcapacityrecord.VehicularCapacityRecord;
import com.sgcities.tdc.optimizer.domain.vehicularcapacityrecord.VehicularCapacityRecordDao;
import com.sgcities.tdc.optimizer.domain.viaofstudy.ViaOfStudy;
import com.sgcities.tdc.optimizer.domain.viaofstudy.ViaOfStudyDao;
import com.sgcities.tdc.optimizer.domain.visualoccupation.VisualOccupationMetadata;
import com.sgcities.tdc.optimizer.domain.visualoccupation.VisualOccupationMetadataDao;
import com.sgcities.tdc.optimizer.preloaddata.BusRouteData;
import com.sgcities.tdc.optimizer.preloaddata.RouteViaRelationshipData;
import com.sgcities.tdc.optimizer.preloaddata.ViaOfStudyData;
import com.sgcities.tdc.optimizer.utilities.MovementConverter;

import java.util.concurrent.Executors;

@Database(entities = {
        GPSLocation.class,
        Metadata.class,
        BusStop.class,
        BusRoute.class,
        ViaOfStudy.class,
        VisualOccupationMetadata.class,
        RouteViaRelationship.class,
        BusOccupation.class,
        VehicularCapacity.class,
        VehicularCapacityRecord.class,
        Assignment.class,
        OriginDestinyPollAnswer.class,
        OriginDestinyPoll.class},
        version = 3, exportSchema = false)
@TypeConverters({MovementConverter.class})
public abstract class uRoomDatabase extends RoomDatabase {

    public abstract GPSLocationDao gpsLocationDao();
    public abstract MetadataDao metadataDao();
    public abstract BusStopDao busStopDao();
    public abstract BusRouteDao busRouteDao();
    public abstract ViaOfStudyDao viaOfStudyDao();
    public abstract VisualOccupationMetadataDao visualOccupationMetadataDao();
    public abstract RouteViaRelationshipDao routeViaRelationshipDao();
    public abstract BusOccupationDao busOccupationDao();
    public abstract VehicularCapacityDao vehicularCapacityDao();
    public abstract VehicularCapacityRecordDao vehicularCapacityRecordDao();
    public abstract AssignmentDao assignmentDao();
    public abstract OriginDestinyPollAnswerDao pollAnswerDao();
    public abstract OriginDestinyPollDao pollDao();

    private static uRoomDatabase INSTANCE;

    public synchronized static uRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = getDatabase(context);
        }
        return INSTANCE;
    }

    public static uRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (uRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(), uRoomDatabase.class, "user-database")
                                    .allowMainThreadQueries()
                                    .fallbackToDestructiveMigration()
                                    .addCallback(new Callback() {
                                        @Override
                                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                            super.onCreate(db);
                                            Executors.newSingleThreadExecutor().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getInstance(context).busRouteDao().insertAll(
                                                            BusRouteData.getBusRouteData()
                                                    );
                                                    getInstance(context).viaOfStudyDao().insertAll(
                                                            ViaOfStudyData.getBusRouteData()
                                                    );
                                                    getInstance(context).routeViaRelationshipDao().insertAll(
                                                            RouteViaRelationshipData.getRouteViaRelationshipData()
                                                    );
                                                }
                                            });
                                        }
                                    })
                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
