package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.mk.familyweighttracker.Adapter.WhatIsNewListAdapter;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.PreferenceHelper;
import com.mk.familyweighttracker.Framework.SystemInformation;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.HomeActivity;
import com.mk.familyweighttracker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mvalhekar on 15-12-2016.
 */

public class WhatIsNewActivity extends TrackerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_is_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_whats_new);
        setSupportActionBar(toolbar);

        findViewById(R.id.what_is_new_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceHelper.putInt(Constants.SharedPreference.WhatsNewShownFor, SystemInformation.getAppVersionCode());
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ExpandableListView listView = ((ExpandableListView) findViewById(R.id.what_is_new_list));
        listView.setAdapter(new WhatIsNewListAdapter(this, getWhatsNewList()));
        setGroupIndicatorToRight(listView);
        listView.performItemClick(null, 0, 0);
    }

    private void setGroupIndicatorToRight(ExpandableListView expListView) {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private List<IWhatIsNew> getWhatsNewList() {
        List<IWhatIsNew> list = new ArrayList<>();

        // 2.0.170103
        list.add(new IWhatIsNew() {
            @Override
            public String getVersion() { return "2.0"; }

            @Override
            public Date getReleaseDate() {
                Calendar calendar = Calendar.getInstance();
                calendar.set(2017, 0, 3);
                return calendar.getTime();
            }

            @Override
            public List<WhatIsNewMessage> getFeatureMap() {
                return Arrays.asList(
                        new WhatIsNewMessage(1, "Infant growth tracking", "Infant's physical growth can now be tracked for Weight, Height and Head Circumference up to 36 months of age."),
                        new WhatIsNewMessage(2, "Multiple pregnancies tracking", "Next pregnancy of user or relative's or friend's pregnancy can now be tracked for Weight gain."),
                        new WhatIsNewMessage(3, "Share growth chart", "Pregnancy weight gain / Infant physical growth chart(s) can now be shared with others/social media."),
                        new WhatIsNewMessage(4, "Android M support", "Supporting Android M permissions model."));
            }
        });

        // 1.3.161008
        list.add(new IWhatIsNew() {
            @Override
            public String getVersion() { return "1.3"; }

            @Override
            public Date getReleaseDate() {
                Calendar calendar = Calendar.getInstance();
                calendar.set(2016, 9, 8);
                return calendar.getTime();
            }

            @Override
            public List<WhatIsNewMessage> getFeatureMap() {
                return Arrays.asList(
                        new WhatIsNewMessage(1, "Twins", "Pregnancy with \"twins\" can also be tracked."),
                        new WhatIsNewMessage(2, "Less calculations", "Delivery Due date is mandatory so that current week suggestion is near accurate."),
                        new WhatIsNewMessage(3, "Current week number suggestion", "Week number is auto-calculated based on Delivery Due Date."),
                        new WhatIsNewMessage(4, "Post-delivery reading", "Post-delivery reading can be added (completing pregnancy tracking)."),
                        new WhatIsNewMessage(5, "User name length restriction", "Restricting length of user name to 15 chars."),
                        new WhatIsNewMessage(6, "Better UI", "Improved look and feel"));
            }
        });

        return list;
    }

    public interface IWhatIsNew {
        String getVersion();
        Date getReleaseDate();
        List<WhatIsNewMessage> getFeatureMap();
    }

    public class WhatIsNewMessage {
        public int order;
        public String title;
        public String description;

        public WhatIsNewMessage (int order, String title, String description) {
            this.order = order;
            this.title = title;
            this.description = description;
        }
    }
}
