package com.mk.familyweighttracker.Framework;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mk.familyweighttracker.Activities.AppFeedbackActivity;
import com.mk.familyweighttracker.R;

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

    public void navigateToFeedbackActivity(Context context){
        Intent feedbackIntent = new Intent(context, AppFeedbackActivity.class);
        startActivity(feedbackIntent);
    }

    public void navigateToShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
        startActivity(shareIntent);
    }

    private String getShareText() {
        return String.format("\'%s\'", getString(R.string.app_name)) +
                getString(R.string.app_share_line_2) +
                getString(R.string.app_share_line_3) +
                getString(R.string.app_share_line_4) +
                "\n\n" +
                String.format(Constants.PLAY_STORE_APP_SEARCH_URL, TrackerApplication.getApp().getPackageName());
    }

}
