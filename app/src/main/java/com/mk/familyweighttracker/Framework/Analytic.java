package com.mk.familyweighttracker.Framework;

import com.google.android.gms.analytics.HitBuilders;

/**
 * Created by mvalhekar on 03-08-2016.
 */
public class Analytic {
    public static void sendScreenView(String screenName){
        TrackerApplication.getApp().getDefaultTracker().setScreenName(screenName);
        TrackerApplication.getApp().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void setData(String category, String action, String label, Long value){

        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label);

        if (value != null) {
            eventBuilder.setValue(value);
        }

        TrackerApplication.getApp().getDefaultTracker().send(eventBuilder.build());
    }
}
