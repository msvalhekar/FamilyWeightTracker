package com.mk.familyweighttracker.Framework;

import android.support.v4.app.Fragment;

import com.mk.familyweighttracker.Fragments.*;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by mvalhekar on 16-03-2016.
 */
public class PregnantUserTabsFactory {
    private static PregnantUserTabsFactory tabsFactory;

    private PregnantUserTabsFactory() {}

    public static PregnantUserTabsFactory getInstance() {
        if(tabsFactory == null)
            tabsFactory = new PregnantUserTabsFactory();
        return tabsFactory;
    }

    private LinkedHashMap<String, Fragment> _homeTabs;

    private LinkedHashMap<String, Fragment> getTabsCollection() {
        if(_homeTabs == null) {
            _homeTabs = new LinkedHashMap<>();

            _homeTabs.put("Profile", new PregnantUserProfileFragment());
            _homeTabs.put("Readings", new PregnantUserReadingsFragment());
            _homeTabs.put("Trend", new PregnantUserChartFragment());
            _homeTabs.put("MWP", new PregnantUserMediaFragment());
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
