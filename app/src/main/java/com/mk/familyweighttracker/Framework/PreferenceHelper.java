package com.mk.familyweighttracker.Framework;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class PreferenceHelper {
    static SharedPreferences mPreferences = TrackerApplication.getApp().getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);

    public static String getString(String key, String defaultValue) {
        return mPreferences.getString(Constants.SharedPreference.SelectedBackgroundAudio, defaultValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
