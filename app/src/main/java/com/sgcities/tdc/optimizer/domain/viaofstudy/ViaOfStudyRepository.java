package com.sgcities.tdc.optimizer.domain.viaofstudy;

import android.app.Application;

import com.sgcities.tdc.optimizer.domain.uRoomDatabase;

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
