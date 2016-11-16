package com.mk.familyweighttracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;

public class HomeActivity extends TrackerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Analytic.sendScreenView(Constants.Activities.HomeActivity);

        StorageUtility.createDirectories();

        initToolbarControl();
        initDisclaimerControl();
    }

    @Override
    protected void onResume() {
        super.onResume();

        promptForUpgradeIfRequired();
    }

    private void initDisclaimerControl() {
        findViewById(R.id.home_app_disclaimer_ok)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.UsersListActivity.class));
                    }
                });
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_home);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void promptForUpgradeIfRequired() {
        boolean promptForUpgrade = getIntent().getBooleanExtra(Constants.ExtraArg.PROMPT_FOR_UPGRADE, false);

        if (!promptForUpgrade) return;

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_update_title))
                .setMessage(getString(R.string.app_update_message))
                .setPositiveButton(getString(R.string.app_update_positive_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateToAppStore();
                    }
                })
                .setNegativeButton(getString(R.string.app_update_negative_label), null)
                .create()
                .show();
    }
}
