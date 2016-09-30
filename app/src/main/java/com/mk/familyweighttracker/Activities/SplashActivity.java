package com.mk.familyweighttracker.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.StringHelper;
import com.mk.familyweighttracker.HomeActivity;
import com.mk.familyweighttracker.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        setAppVersion();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        }, Constants.SPLASH_SCREEN_TIMEOUT_SECONDS);
    }

    private void setAppVersion() {
        TextView versionTv = (TextView) findViewById(R.id.splash_vesion_text);
        String appVersion = "";
        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(StringHelper.isNullOrEmpty(appVersion)) {
            versionTv.setVisibility(View.GONE);
        } else {
            versionTv.setText(String.format(getString(R.string.splash_activity_version_format), appVersion));
            versionTv.setVisibility(View.VISIBLE);
        }
    }
}
