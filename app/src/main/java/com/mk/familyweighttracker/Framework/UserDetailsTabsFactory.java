package com.mk.familyweighttracker.Framework;

import android.support.v4.app.Fragment;

import com.mk.familyweighttracker.Fragments.*;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by mvalhekar on 16-03-2016.
 */
public class UserDetailsTabsFactory {
    private static UserDetailsTabsFactory userDetailsTabsFactory;

    private UserDetailsTabsFactory() {}

    public static UserDetailsTabsFactory getInstance() {
        if(userDetailsTabsFactory == null)
            userDetailsTabsFactory = new UserDetailsTabsFactory();
        return userDetailsTabsFactory;
    }

    private LinkedHashMap<String, Fragment> _homeTabs;

    private LinkedHashMap<String, Fragment> getTabsCollection() {
        if(_homeTabs == null) {
            _homeTabs = new LinkedHashMap<>();

            _homeTabs.put("Profile", new UserDetailsProfileFragment());
            _homeTabs.put("Readings", new UserDetailsRecordsFragment());
            _homeTabs.put("Trend", new UserDetailsChartFragment());
            _homeTabs.put("MWP", new UserDetailsMediaFragment());
        }
        return _homeTabs;
    }

    public Collection<Fragment> getTabs() {
        return getTabsCollection().values();
    }

    public Collection<String> getTabTitles() {
        return getTabsCollection().keySet();
    }
}
