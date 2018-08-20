package com.example.josuerey.helloworld.entidades;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {GPSLocation.class, Metadata.class, BusStop.class}, version = 2)
public abstract class uRoomDatabase extends RoomDatabase {

    public abstract GPSLocationDao gpsLocationDao();

    public abstract MetadataDao metadataDao();

    public abstract BusStopDao busStopDao();

    private static uRoomDatabase INSTANCE;

    public static uRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (uRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(), uRoomDatabase.class, "user-database")
                                    .allowMainThreadQueries()
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
