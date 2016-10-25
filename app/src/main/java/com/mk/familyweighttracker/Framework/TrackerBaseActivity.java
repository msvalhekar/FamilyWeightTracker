package com.mk.familyweighttracker.Framework;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TrackerBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TrackerApplication.setCurrentActivity(this);
    }

    public void navigateToAppStore(){
        String appName = TrackerApplication.getApp().getPackageName();
        try {
            String appMarketSearchUrl = String.format(Constants.PLAY_STORE_MARKET_SEARCH_URL, appName);
            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appMarketSearchUrl));
            startActivity(rateIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            String appStoreSearchUrl = String.format(Constants.PLAY_STORE_APP_SEARCH_URL, appName);
            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appStoreSearchUrl));
            startActivity(rateIntent);
        }
    }
}
