package com.example.josuerey.helloworld.domain.viaofstudy;

import android.app.Application;

import com.example.josuerey.helloworld.domain.uRoomDatabase;

public class ViaOfStudyRepository {
    private ViaOfStudyDao viaOfStudyDao;

    public ViaOfStudyRepository(Application application){
        uRoomDatabase db = uRoomDatabase.getDatabase(application);
        viaOfStudyDao = db.viaOfStudyDao();
    }

    public ViaOfStudy[] findAll() {
        return viaOfStudyDao.findAll();
    }
}
