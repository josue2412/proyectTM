package com.sgcities.tdc.optimizer.utilities;

import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    /**
     * Returns the difference between date1 and date2
     * @param date1 begin date
     * @param date2 end date
     * @param timeUnit unit of difference
     * @return
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
