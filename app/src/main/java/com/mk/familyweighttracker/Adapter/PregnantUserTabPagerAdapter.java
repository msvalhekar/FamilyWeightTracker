package com.mk.familyweighttracker.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mk.familyweighttracker.Enums.UserType;
import com.mk.familyweighttracker.Framework.PregnantUserTabsFactory;

/**
 * Created by mvalhekar on 17-11-2016.
 */
public class PregnantUserTabPagerAdapter extends FragmentStatePagerAdapter {
    private int mTabsCount;
    private Object[] mTabs;
    private Object[] mTitles;

    public PregnantUserTabPagerAdapter(FragmentManager fm, UserType userType) {
        super(fm);
        PregnantUserTabsFactory factory = new PregnantUserTabsFactory(userType);
        mTabs = factory.getTabs().toArray();
        mTitles = factory.getTabTitles().toArray();
        mTabsCount = mTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) { return (String)mTitles[position]; }

    @Override
    public Fragment getItem(int position) { return (Fragment)mTabs[position]; }

    @Override
    public int getCount() { return mTabsCount; }
}