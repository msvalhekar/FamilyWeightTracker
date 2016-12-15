package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.AppVersion;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.DataMigratorTask;
import com.mk.familyweighttracker.Framework.PreferenceHelper;
import com.mk.familyweighttracker.Framework.StringHelper;
import com.mk.familyweighttracker.Framework.SystemInformation;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.HomeActivity;
import com.mk.familyweighttracker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends TrackerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        ProgressBar progressBar = ((ProgressBar) findViewById(R.id.splash_progress_bar));

        initAppVersionControl();

        new DataMigratorTask().execute();

        setLastUpdateCheckedDateForFirstTime();

        boolean checkMarketAppVersion = isEligibleToCheckVersionOnPlayStore();
        if(checkMarketAppVersion) {
            progressBar.setVisibility(View.VISIBLE);

            new MarketAppInfoReader().execute();
            return;
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                gotoHomeActivity(false);
            }
        }, Constants.Settings.SPLASH_SCREEN_TIMEOUT_SECONDS);
    }

    private void initAppVersionControl() {
        TextView versionTv = (TextView) findViewById(R.id.splash_vesion_text);
        String appVersion = SystemInformation.getAppVersionName();

        if(StringHelper.isNullOrEmpty(appVersion)) {
            versionTv.setVisibility(View.GONE);
        } else {
            versionTv.setText(String.format(getString(R.string.splash_activity_version_format), appVersion));
            versionTv.setVisibility(View.VISIBLE);
        }
    }

    private void gotoHomeActivity(boolean promptUpgrade) {

        int whatsNewShownFor = PreferenceHelper.getInt(Constants.SharedPreference.WhatsNewShownFor, 0);
        int appVersionCode = SystemInformation.getAppVersionCode();
        if(whatsNewShownFor < appVersionCode) {
            Intent intent = new Intent(getApplicationContext(), WhatIsNewActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(Constants.ExtraArg.PROMPT_FOR_UPGRADE, promptUpgrade);
            startActivity(intent);
        }

        finish();
    }

    private void setLastUpdateCheckedDateForFirstTime() {
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(1983, 4, 31, 0, 0, 0);
        Date myDate = myCalendar.getTime();

        Date lastCheckedOn = PreferenceHelper.getDate(Constants.SharedPreference.AppMarketLastUpdateCheckedOn, myDate);

        long lastCheckedBeforeDays = Utility.calculateDateDiff(myDate, lastCheckedOn);
        if(lastCheckedBeforeDays == 0) {
            Calendar today = Calendar.getInstance();
            today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

            PreferenceHelper.putDate(Constants.SharedPreference.AppMarketLastUpdateCheckedOn, today.getTime());
        }
    }

    public boolean isEligibleToCheckVersionOnPlayStore() {
        Date lastCheckedOn = PreferenceHelper.getDate(Constants.SharedPreference.AppMarketLastUpdateCheckedOn, new Date());
        long lastCheckedBeforeDays = Utility.calculateDateDiff(lastCheckedOn);

        return (Constants.Settings.CHECK_MARKET_APP_UPDATE_AFTER_DAYS < lastCheckedBeforeDays);
    }

    private void upgradeAppIfRequired(String marketAppVersion) {
        if(StringHelper.isNullOrEmpty(marketAppVersion)) {
            gotoHomeActivity(false);
        }

        String currentAppVersion = SystemInformation.getAppVersionName();

        AppVersion market = AppVersion.parseFrom(marketAppVersion);
        AppVersion current = AppVersion.parseFrom(currentAppVersion);

        gotoHomeActivity(current.compareTo(market) < 0);
    }

    private class MarketAppInfoReader extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = String.format(Constants.PLAY_STORE_APP_SEARCH_URL, TrackerApplication.getApp().getPackageName())
                        .concat("&hl=en");

                Element versionElement = Jsoup.connect(url)
                        .timeout(20000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first();
                return versionElement.ownText();
            } catch(Exception e) {
                return SystemInformation.getAppVersionName();
            }
        }

        @Override
        protected void onPostExecute(final String result){
            Calendar today = Calendar.getInstance();
            today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            PreferenceHelper.putDate(Constants.SharedPreference.AppMarketLastUpdateCheckedOn, today.getTime());

            upgradeAppIfRequired(result);
        }
    }
}
