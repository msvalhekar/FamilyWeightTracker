package com.mk.familyweighttracker.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mk.familyweighttracker.Framework.UserDetailsTabsFactory;

/**
 * Created by mvalhekar on 17-11-2016.
 */
public class PregnantUserDetailsTabPagerAdapter extends FragmentStatePagerAdapter {
    private int mTabsCount;
    private Object[] mTabs;
    private Object[] mTitles;

    public PregnantUserDetailsTabPagerAdapter(FragmentManager fm) {
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