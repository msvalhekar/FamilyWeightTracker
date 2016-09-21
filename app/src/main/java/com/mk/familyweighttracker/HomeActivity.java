package com.mk.familyweighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Services.UserService;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Analytic.sendScreenView(Constants.Activities.HomeActivity);

        StorageUtility.createDirectories();

        initToolbarControl();
        initDisclaimerControl();
    }

    private void initDisclaimerControl() {
        ((TextView) findViewById(R.id.home_app_disclaimer_ok))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<User> users = new UserService().getAll();
                        if(users.size() > 0) {
                            Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.UserDetailActivity.class);
                            intent.putExtra(Constants.ExtraArg.USER_ID, users.get(0).getId());
                            startActivity(intent);
                            return;
                        } else {
                            Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.AddPregnantUserActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_home);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
