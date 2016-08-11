package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.mk.familyweighttracker.Fragments.UserDetailsProfileFragment;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Framework.SlidingTabLayout;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Framework.UserDetailsTabsFactory;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserDetailActivity extends TrackerBaseActivity
        implements OnNewReadingAdded, UserDetailsProfileFragment.OnUserDeleted {

    private long mUserId;
    private User mUser;
    private boolean mIsDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        mUserId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mUser = new UserService().get(mUserId);
        this.setTitle(mUser.name);

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.UserDetailsACtivity,
                String.format(Constants.AnalyticsActions.UserDetailsLoaded, mUser.name),
                null);

        initToolbarControl();

        initDetailTabControl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                informDataChangedAndFinish();
                return true;

            case R.id.user_detail_help:

                View layout = getLayoutInflater().inflate(R.layout.help_popup_user_detail_readings, null);

                PopupWindow window = new PopupWindow(this);
                window.setContentView(layout);
                window.setWidth(900);
                window.setHeight(900);
                window.setFocusable(true);
                window.showAtLocation(layout, Gravity.NO_GRAVITY, 40, 20);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean mIsOriginator = false;
    @Override
    public boolean isOriginator() {
        return mIsOriginator;
    }

    @Override
    public void onNewReadingAdded() {
        mIsDataChanged = true;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments) {
            if(fragment instanceof OnNewReadingAdded &&
                ((OnNewReadingAdded) fragment).isOriginator() == false) {
                    ((OnNewReadingAdded) fragment).onNewReadingAdded();
            }
        }
    }

    @Override
    public void onUserDeleted() {
        mIsDataChanged = true;

        mUser.removeReminder(getApplicationContext());
        new UserService().remove(mUserId);

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.UserDelete,
                String.format(Constants.AnalyticsActions.UserDeleted, mUser.name),
                null);

        informDataChangedAndFinish();

        String message = String.format("'%s' removed permanently.", mUser.name);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            informDataChangedAndFinish();
        }
        return super.onKeyDown(keyCode, event);
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
        viewPager.setAdapter(new UserDetailsTabPagerAdapter(getSupportFragmentManager()));

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

    private void informDataChangedAndFinish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.ExtraArg.IS_DATA_CHANGED, mIsDataChanged);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public class UserDetailsTabPagerAdapter extends FragmentStatePagerAdapter
    {
        private int mTabsCount;
        private Object[] mTabs;
        private Object[] mTitles;

        public UserDetailsTabPagerAdapter(FragmentManager fm) {
            super(fm);

            mTabs = UserDetailsTabsFactory.getInstance().getTabs().toArray();
            mTitles = UserDetailsTabsFactory.getInstance().getTabTitles().toArray();
            mTabsCount = mTabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) { return (String)mTitles[position]; }

        @Override
        public Fragment getItem(int position) { return (Fragment)mTabs[position]; }

        @Override
        public int getCount() { return mTabsCount; }
    }
}
