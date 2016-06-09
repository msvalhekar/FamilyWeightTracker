package com.mk.familyweighttracker.Framework;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mvalhekar on 18-05-2016.
 */
public class TrackerApplication extends com.activeandroid.app.Application {

    private Tracker mTracker;

    private Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                e.printStackTrace(); // not all Android versions will print the stack trace automatically

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
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker("UA-78914112-1");
        }
        return mTracker;
    }

    private void setAnalyticsScreen(String screen) {
        getDefaultTracker().setScreenName(screen);
    }

    private void resetAnalyticsScreen() {
        getDefaultTracker().setScreenName(null);
    }

    public void sendAnalyticsData(String screen, String category, String action, String label, long value) {
        setAnalyticsScreen(screen);

        getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());

        resetAnalyticsScreen();
    }

}
