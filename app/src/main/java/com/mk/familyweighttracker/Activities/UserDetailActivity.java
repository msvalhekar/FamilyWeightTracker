package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mk.familyweighttracker.Fragments.UserDetailsRecordsFragment;
import com.mk.familyweighttracker.Framework.SlidingTabLayout;
import com.mk.familyweighttracker.Framework.UserDetailsTabsFactory;
import com.mk.familyweighttracker.IUserDetailsFragment;
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
public class UserDetailActivity extends AppCompatActivity implements UserDetailsRecordsFragment.OnNewReadingAdded {

    public static final String ARG_USER_ID = "user_id";
    private boolean mIsDataChanged = false;
    public static final String ARG_IS_DATA_CHANGED = "IsDataChanged";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        long userId = getIntent().getLongExtra(ARG_USER_ID, 0);
        User user = new UserService().get(userId);
        this.setTitle(user.getName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_user_detail);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ViewPager viewPager = ((ViewPager) findViewById(R.id.user_detail_pager));
        viewPager.setAdapter(new UserDetailsTabPagerAdapter(user, getSupportFragmentManager()));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(ARG_IS_DATA_CHANGED, mIsDataChanged);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewReadingAdded() {
        mIsDataChanged = true;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments) {
            if(fragment instanceof UserDetailsRecordsFragment.OnNewReadingAdded)
                ((UserDetailsRecordsFragment.OnNewReadingAdded) fragment).onNewReadingAdded();
        }
    }

    public class UserDetailsTabPagerAdapter extends FragmentStatePagerAdapter
    {
        private int mTabsCount;
        private Object[] mTabs;
        private Object[] mTitles;

        public UserDetailsTabPagerAdapter(User user, FragmentManager fm) {
            super(fm);

            mTabs = UserDetailsTabsFactory.getInstance().getTabs().toArray();
            mTitles = UserDetailsTabsFactory.getInstance().getTabTitles().toArray();
            mTabsCount = mTabs.length;

            for(Object fragment: mTabs)
                ((IUserDetailsFragment) fragment).setUser(user);
        }

        @Override
        public CharSequence getPageTitle(int position) { return (String)mTitles[position]; }

        @Override
        public Fragment getItem(int position) { return (Fragment)mTabs[position]; }

        @Override
        public int getCount() { return mTabsCount; }
    }
}
