package com.example.josuerey.helloworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.josuerey.helloworld.utilidades.Utilidades;

public class ConexionSQLiteH extends SQLiteOpenHelper{


    public ConexionSQLiteH(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.CREAR_TABLA_RECORRIDO);
        db.execSQL(Utilidades.CREAR_TABLA_DRECORRIDO);
        db.execSQL(Utilidades.CREAR_TABLA_PARADAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        db.execSQL("DROP TABLE IF EXISTS "+Utilidades.TABLA_RECORRIDO);
        db.execSQL("DROP TABLE IF EXISTS "+Utilidades.TABLA_DRECORRIDOS);
        db.execSQL("DROP TABLE IF EXISTS "+Utilidades.TABLA_PARADAS);
        onCreate(db);
    }
}
