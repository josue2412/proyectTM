package com.example.josuerey.helloworld.utilidades;

import android.util.Log;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StudyDuration {

    /**
     *
     * @param totalMins time passed in minutes
     * @param studyDurationHrs total amount of time of the study.
     * @return study remaining time
     */
    public static String remainingTime(int totalMins, int studyDurationHrs){
        int studyDurationInSecs = studyDurationHrs * 3600;
        int totalSecs = totalMins *60;

        int hours = (studyDurationInSecs - totalSecs) / 3600;
        int minutes = ((studyDurationInSecs - totalSecs) % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     *
     * @param totalMins time passed in minutes
     * @param remainingTime total amount of time of the study.
     * @return study remaining time
     */
    public static String remainingTime(int totalMins, String remainingTime){

        Log.d("TIME", "TotalMins: " + String.valueOf(totalMins) + remainingTime);
        String[] time = remainingTime.split(":");

        int studyDurationInSecs = Integer.valueOf(time[0]) * 3600 + Integer.valueOf(time[1]) * 60;
        int totalSecs = totalMins * 60;

        int hours = (studyDurationInSecs - totalSecs) / 3600;
        int minutes = ((studyDurationInSecs - totalSecs) % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
