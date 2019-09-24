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

    public static void setUserName(Context context, String userName) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_USERNAME, userName);
        editor.apply();
    }

    public static void setUserNameKey(Context context, String userNameKey) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_USERNAME_KEY, userNameKey);
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

    public static String getUserName(Context context) {
        return getPreferences(context).getString(LOGGED_IN_USERNAME, null);
    }

    public static String getUserNameKey(Context context) {
        return getPreferences(context).getString(LOGGED_IN_USERNAME_KEY, null);
    }
}
