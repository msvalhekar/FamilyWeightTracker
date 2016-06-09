package com.mk.familyweighttracker.Framework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class TrackerBaseActivity extends AppCompatActivity {

    public Tracker getTracker() {
        return ((TrackerApplication) getApplication()).getDefaultTracker();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setAnalyticsForCurrentActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();

        resetAnalyticsForCurrentActivity();
    }

    private void setAnalyticsForCurrentActivity() {
        getTracker().setScreenName(this.getLocalClassName());
    }

    private void resetAnalyticsForCurrentActivity() {
        getTracker().setScreenName(null);
    }

    public void sendAnalyticsData(String category, String action, String label, long value) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }

}
