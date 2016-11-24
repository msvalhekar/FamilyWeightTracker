package com.mk.familyweighttracker.Framework;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class PreferenceHelper {
    static SharedPreferences mPreferences = TrackerApplication.getApp().getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);

    public static Date getDate(String key, Date defaultValue) {
        long value = mPreferences.getLong(key, defaultValue.getTime());
        return new Date(value);
    }

    public static String getString(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public static void putDate(String key, Date value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value.getTime());
        editor.commit();
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
