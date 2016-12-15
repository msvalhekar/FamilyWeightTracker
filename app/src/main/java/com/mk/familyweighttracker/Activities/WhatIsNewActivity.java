package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.PreferenceHelper;
import com.mk.familyweighttracker.Framework.SystemInformation;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.HomeActivity;
import com.mk.familyweighttracker.R;

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

        TextView tv = ((TextView) findViewById(R.id.what_is_new_view));
        tv.setText(Html.fromHtml(getWhatsNewMessage()));
        tv.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.what_is_new_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceHelper.putInt(Constants.SharedPreference.WhatsNewShownFor, SystemInformation.getAppVersionCode());
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getWhatsNewMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("<big>");
        builder.append("<br />" + "<b>" + "Infant growth tracking" + "</b>");
        builder.append("<br />" + "Infant physical growth can now be tracked for Weight, Height and Head Circumference.");
        builder.append("<br />");
        builder.append("<br />" + "<b>" + "Share growth chart" + "</b>");
        builder.append("<br />" + "Pregnancy weight gain / Infant physical growth chart(s) can now be shared with others/social media.");
        builder.append("</big>");
        return builder.toString();
    }
}
