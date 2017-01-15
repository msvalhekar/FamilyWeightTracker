package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.mk.familyweighttracker.Adapter.PregnantUserTabPagerAdapter;
import com.mk.familyweighttracker.Fragments.PregnantUserBaseFragment;
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
                getUser().getUserDetailsEvent(),
                String.format(Constants.AnalyticsActions.UserDetailsLoaded, getUser().name, getUser().getTypeShortName()),
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

        if(currentFragment != null) {
            menu.findItem(R.id.user_detail_share_charts).setVisible(currentFragment.showShareChartMenu());
            menu.findItem(R.id.user_detail_help).setVisible(currentFragment.showHelpMenu());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Respond to the action bar's Up/Home button
                finish();
                break;

            case R.id.user_detail_share_charts:
                currentFragment.onShareChartMenu();
                break;

            case R.id.user_detail_help:
                currentFragment.onHelpMenu();
                break;
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

    PregnantUserBaseFragment currentFragment;

    private void initDetailTabControl() {
        final ViewPager viewPager = ((ViewPager) findViewById(R.id.user_detail_pager));
        viewPager.setAdapter(new PregnantUserTabPagerAdapter(getSupportFragmentManager(), getUser().type));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                currentFragment = ((PregnantUserBaseFragment) ((PregnantUserTabPagerAdapter) viewPager.getAdapter()).getItem(position));
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        currentFragment = ((PregnantUserBaseFragment) ((PregnantUserTabPagerAdapter) viewPager.getAdapter()).getItem(0));
        invalidateOptionsMenu();

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
                navigateToShareIntent();
            }
        });

        findViewById(R.id.app_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFeedbackActivity(v.getContext());
            }
        });

        findViewById(R.id.app_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAppStore();
            }
        });
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
