package com.mk.familyweighttracker.Framework;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mvalhekar on 18-05-2016.
 */
public class TrackerApplication extends com.activeandroid.app.Application {

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
}
