package com.example.josuerey.helloworld.utilidades;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportData {

    public static void createFile(String sFileName, String payload) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Backup");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);

            writer.append(payload.toString());

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
