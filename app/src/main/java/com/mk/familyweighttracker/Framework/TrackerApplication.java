package com.mk.familyweighttracker.Framework;

import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mk.familyweighttracker.DbModels.UserModel;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mvalhekar on 18-05-2016.
 */
public class TrackerApplication extends com.activeandroid.app.Application {

    private static AppCompatActivity currentActivity = null;
    private static TrackerApplication application;
    private Tracker mTracker;

    public static TrackerApplication getApp() { return application; }

    public void onCreate() {
        super.onCreate();
        application = this;

        //LocaleHelper.onCreate(this, "fr");

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        JodaTimeAndroid.init(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {

                try {
                    JSONArray array = new JSONArray();
                    for(UserModel userModel: UserModel.getAll()) {
                        array.put(userModel.toJSON());
                    }
                    Crashlytics.log(array.toString());
                    Crashlytics.logException(e);
                } catch (JSONException je) {
                    Crashlytics.logException(e);
                }

//                Intent intent = new Intent ();
//                intent.setAction("com.mk.familyweighttracker.Framework.SEND_LOG");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
//                startActivity(intent);

                System.exit(1);
            }
        });
    }

    public void onTerminate() {
        super.onTerminate();
        application = null;
        currentActivity = null;
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker("UA-78914112-2");
        }
        return mTracker;
    }

    public static void setCurrentActivity(AppCompatActivity activity) {
        currentActivity = activity;
    }

    public static AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }
}
