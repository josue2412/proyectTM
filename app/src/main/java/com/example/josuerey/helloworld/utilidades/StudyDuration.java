package com.example.josuerey.helloworld.utilidades;

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
}
