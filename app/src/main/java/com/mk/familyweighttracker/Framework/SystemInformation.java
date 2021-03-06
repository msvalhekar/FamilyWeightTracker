package com.mk.familyweighttracker.Framework;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.mk.familyweighttracker.BuildConfig;
import com.mk.familyweighttracker.R;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Created by mvalhekar on 03-08-2016.
 */
public class SystemInformation {

    public static LinkedHashMap<String, String> getSystemInformation() {
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        String appVersionLabel = String.format("%s (%d)", getAppVersionName(), getAppVersionCode());
        data.put(Utility.getResourceString(R.string.application_version_label), appVersionLabel);
        data.put(Utility.getResourceString(R.string.architecture_label), getOSArchitecture());
        data.put(Utility.getResourceString(R.string.available_memory_label), getAvailableMemoryString());
        data.put(Utility.getResourceString(R.string.battery_level_label), getBatteryLevel());
        data.put(Utility.getResourceString(R.string.locale_label), getLocaleName());
        data.put(Utility.getResourceString(R.string.model_label), getDeviceName());
        data.put(Utility.getResourceString(R.string.android_version_label), getAndroidVersionName());
        data.put(Utility.getResourceString(R.string.processor_count_label), String.valueOf(getProcessorCount()));
        data.put(Utility.getResourceString(R.string.display_density), getDisplayDensity());
        return data;
    }

    public static long getAvailableMemory() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) stat.getBlockSize() * (long) stat.getAvailableBlocks()) / (1024 * 1024);
    }

    public static String getAvailableMemoryString() {
        return String.format("%dMB", SystemInformation.getAvailableMemory());
    }

    public static String getAndroidVersionName() {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static int getProcessorCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getLocaleName() {
        return String.format("%s (%s)", Locale.getDefault().getDisplayLanguage(), Locale.getDefault().getDisplayCountry());
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER, model = Build.MODEL;

        if (!model.startsWith(manufacturer)) {
            return String.format("%s %s", manufacturer, model).toUpperCase();
        }

        return model.toUpperCase();
    }

    public static String getBatteryLevel() {
        Intent batteryIntent = TrackerApplication.getApp().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return String.valueOf(((float) level / (float) scale) * 100.0f) + "%";
    }

    public static String getAppVersionName() {
        Application app = TrackerApplication.getApp();

        try {
            return app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return BuildConfig.VERSION_NAME;
    }

    public static String getOSArchitecture() {
        return System.getProperty("os.arch").toUpperCase();
    }

    public static String getDisplayDensity() {
        return String.format("%d dpi", TrackerApplication.getApp().getResources().getDisplayMetrics().densityDpi);
    }

    public static int getAppVersionCode() {
        Application app = TrackerApplication.getApp();

        try {
            return app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return BuildConfig.VERSION_CODE;
    }}
