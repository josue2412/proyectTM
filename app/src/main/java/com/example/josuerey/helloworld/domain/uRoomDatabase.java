package com.example.josuerey.helloworld.domain;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;
import com.example.josuerey.helloworld.domain.busroute.BusRouteDao;
import com.example.josuerey.helloworld.domain.busstop.BusStop;
import com.example.josuerey.helloworld.domain.busstop.BusStopDao;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocation;
import com.example.josuerey.helloworld.domain.gpslocation.GPSLocationDao;
import com.example.josuerey.helloworld.domain.metadata.Metadata;
import com.example.josuerey.helloworld.domain.metadata.MetadataDao;
import com.example.josuerey.helloworld.domain.routeviarelationship.RouteViaRelationship;
import com.example.josuerey.helloworld.domain.routeviarelationship.RouteViaRelationshipDao;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudy;
import com.example.josuerey.helloworld.domain.viaofstudy.ViaOfStudyDao;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadata;
import com.example.josuerey.helloworld.domain.visualoccupation.VisualOccupationMetadataDao;
import com.example.josuerey.helloworld.preloaddata.BusRouteData;
import com.example.josuerey.helloworld.preloaddata.RouteViaRelationshipData;
import com.example.josuerey.helloworld.preloaddata.ViaOfStudyData;

import java.util.concurrent.Executors;

@Database(entities = {
        GPSLocation.class,
        Metadata.class,
        BusStop.class,
        BusRoute.class,
        ViaOfStudy.class,
        VisualOccupationMetadata.class,
        RouteViaRelationship.class},
        version = 2, exportSchema = false)
public abstract class uRoomDatabase extends RoomDatabase {

    public abstract GPSLocationDao gpsLocationDao();

    public abstract MetadataDao metadataDao();

    public abstract BusStopDao busStopDao();

    public abstract BusRouteDao busRouteDao();

    public abstract ViaOfStudyDao viaOfStudyDao();

    public abstract VisualOccupationMetadataDao visualOccupationMetadataDao();

    public abstract RouteViaRelationshipDao routeViaRelationshipDao();

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
