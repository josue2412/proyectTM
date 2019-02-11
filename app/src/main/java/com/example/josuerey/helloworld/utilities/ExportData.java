package com.example.josuerey.helloworld.utilities;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportData {
    private static final String TAG = "ExportData";

    public static void createFile(String sFileName, String payload) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Backup");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile, true);

            writer.append(payload.toString() + "\n");

            writer.flush();
            writer.close();
            Log.d(TAG, "File successfully created/updated" + sFileName);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }
}
