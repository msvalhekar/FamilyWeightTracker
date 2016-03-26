package com.mk.familyweighttracker.Framework;

import android.support.v4.app.Fragment;

import com.mk.familyweighttracker.Fragments.*;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by mvalhekar on 16-03-2016.
 */
public class UserDetailsTabsFactory {
    private static UserDetailsTabsFactory userDetailsTabsFactory;

    private UserDetailsTabsFactory() {}

    public static UserDetailsTabsFactory getInstance()
    {
        if(userDetailsTabsFactory == null)
            userDetailsTabsFactory = new UserDetailsTabsFactory();
        return userDetailsTabsFactory;
    }

    private HashMap<String, Fragment> _homeTabs;

    private HashMap<String, Fragment> getTabsCollection()
    {
        if(_homeTabs == null) {
            _homeTabs = new HashMap<>();

            _homeTabs.put("Summary", new UserDetailsSummaryFragment());
            _homeTabs.put("Records", new UserDetailsRecordsFragment());
            _homeTabs.put("Chart", new UserDetailsChartFragment());
        }
        return _homeTabs;
    }

    public Collection<Fragment> getTabs()
    {
        return getTabsCollection().values();
    }

    public Collection<String> getTabTitles()
    {
        return getTabsCollection().keySet();
    }
}
