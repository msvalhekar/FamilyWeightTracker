package com.mk.familyweighttracker.Framework;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mvalhekar on 18-05-2016.
 */
public class TrackerApplication extends com.activeandroid.app.Application {

    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
    }
}
