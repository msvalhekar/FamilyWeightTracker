package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mk.familyweighttracker.Adapter.PregnantUserTabPagerAdapter;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.SlidingTabLayout;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.MonthGrowthRange;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;
import com.mk.familyweighttracker.Services.UserService;

import java.util.List;

public class PregnantUserDetailActivity extends TrackerBaseActivity {

    private long mUserId;
    private User mUser;
    List<WeekWeightGainRange> mWeekWeightGainRangeList;
    List<MonthGrowthRange> mMonthGrowthRangeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        mUserId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        if(getUser() == null) {
            Toast.makeText(PregnantUserDetailActivity.this, "User does not exist, try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_user_detail);
        setSupportActionBar(toolbar);

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.UserDetailsActivity,
                String.format(Constants.AnalyticsActions.UserDetailsLoaded, getUser().name),
                null);

        initToolbarControl();
        initInteractionControl();
        initDetailTabControl();
    }

    @Override
    public void onResume() {
        super.onResume();

        getSupportActionBar().setTitle(getUser().name);
        if(getUser().isPregnant())
            getSupportActionBar().setSubtitle(R.string.user_detail_activity_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                break;

//            case R.id.user_detail_help:
//                View layout = getLayoutInflater().inflate(R.layout.help_popup_user_detail_readings, null);
//
//                PopupWindow window = new PopupWindow(this);
//                window.setContentView(layout);
//                window.setWidth(900);
//                window.setHeight(900);
//                window.setFocusable(true);
//                window.showAtLocation(layout, Gravity.NO_GRAVITY, 40, 20);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public long getUserId() {
        return getUser().getId();
    }

    public User getUser() {
        if(mUser == null)
            mUser = new UserService().get(mUserId);
        return mUser;
    }

    public void onUserDataChange() {
        mUser = null;
        mWeekWeightGainRangeList = null;
        mMonthGrowthRangeList = null;
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_user_detail);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDetailTabControl() {
        ViewPager viewPager = ((ViewPager) findViewById(R.id.user_detail_pager));
        viewPager.setAdapter(new PregnantUserTabPagerAdapter(getSupportFragmentManager(), getUser().type));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        slidingTabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the slidingTabLayout Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabLayout.setViewPager(viewPager);
    }

    private void initInteractionControl() {
        findViewById(R.id.app_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
                startActivity(shareIntent);
            }
        });

        findViewById(R.id.app_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedbackIntent = new Intent(v.getContext(), AppFeedbackActivity.class);
                feedbackIntent.putExtra(Constants.ExtraArg.USER_ID, mUserId);
                startActivity(feedbackIntent);
            }
        });

        findViewById(R.id.app_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAppStore();
            }
        });
    }

    private String getShareText() {
        return String.format("\'%s\'", getString(R.string.app_name)) +
                getString(R.string.app_share_line_2) +
                getString(R.string.app_share_line_3) +
                getString(R.string.app_share_line_4) +
                "\n\n" +
                String.format(Constants.PLAY_STORE_APP_SEARCH_URL, TrackerApplication.getApp().getPackageName());
    }

    public List<WeekWeightGainRange> getWeightGainRangeTable() {
        if(mWeekWeightGainRangeList == null) {
            double baseWeight = getUser().getStartingWeight();
            if (baseWeight == 0) return null;

            if(getUser().isPregnant()) {
                mWeekWeightGainRangeList = new PregnancyService().getWeightGainTableFor(
                        baseWeight,
                        getUser().getWeightCategory(),
                        getUser().weightUnit,
                        getUser().haveTwins);
            }
        }
        return mWeekWeightGainRangeList;
    }

    public WeekWeightGainRange getPregnancyWeightGainRangeFor(long weekNumber) {
        getWeightGainRangeTable();

        if(mWeekWeightGainRangeList == null)
            return null;

        for (WeekWeightGainRange range: mWeekWeightGainRangeList) {
            if(range.WeekNumber == weekNumber) {
                return range;
            }
        }
        return null;
    }

    public List<MonthGrowthRange> getMonthGrowthRangeTable() {
        if(mMonthGrowthRangeList == null) {
                mMonthGrowthRangeList = new PregnancyService().getMonthGrowthRangeTableFor(
                        getUser().isMale,
                        getUser().weightUnit,
                        getUser().heightUnit,
                        getUser().headCircumUnit);
            }
        return mMonthGrowthRangeList;
    }

    public MonthGrowthRange getInfantMonthGrowthRangeFor(long monthNumber) {
        getMonthGrowthRangeTable();

        if(mMonthGrowthRangeList == null)
            return null;

        for (MonthGrowthRange range: mMonthGrowthRangeList) {
            if(range.MonthNumber == monthNumber) {
                return range;
            }
        }
        return null;
    }
}
