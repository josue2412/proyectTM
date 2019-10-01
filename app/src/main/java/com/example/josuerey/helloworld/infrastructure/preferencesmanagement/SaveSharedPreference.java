package com.example.josuerey.helloworld.infrastructure.preferencesmanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import static com.example.josuerey.helloworld.infrastructure.preferencesmanagement.PreferencesUtility.*;

public class SaveSharedPreference {
    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static void setUserEmail(Context context, String userMail) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_USER_EMAIL, userMail);
        editor.apply();
    }

    public static void setUserPassword(Context context, String userPass) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_USER_PASSWORD, userPass);
        editor.apply();
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_USER_NAME, userName);
        editor.apply();
    }

    public static void setUserId(Context context, int userId) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(LOGGED_IN_USER_ID, userId);
        editor.apply();
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static String getUserMail(Context context) {
        return getPreferences(context).getString(LOGGED_IN_USER_EMAIL, null);
    }

    public static String getUserPassword(Context context) {
        return getPreferences(context).getString(LOGGED_IN_USER_PASSWORD, null);
    }

    public static String getUserName(Context context) {
        return getPreferences(context).getString(LOGGED_IN_USER_NAME, null);
    }

    public static int getUserId(Context context) {
        return getPreferences(context).getInt(LOGGED_IN_USER_ID, 0);
    }
}
