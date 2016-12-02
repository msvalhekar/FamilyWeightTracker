package com.mk.familyweighttracker.Fragments;

import android.support.v4.app.Fragment;

import com.mk.familyweighttracker.Activities.PregnantUserDetailActivity;
import com.mk.familyweighttracker.Models.MonthGrowthRange;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserBaseFragment extends Fragment {

    public PregnantUserBaseFragment() {
        // Required empty public constructor
    }

    protected long getUserId() {
        return ((PregnantUserDetailActivity) getActivity()).getUserId();
    }

    protected User getUser() {
        return ((PregnantUserDetailActivity) getActivity()).getUser();
    }

    protected void onUserDataChange() {
        ((PregnantUserDetailActivity) getActivity()).onUserDataChange();
    }

    protected List<WeekWeightGainRange> getWeightGainRangeTable() {
        return ((PregnantUserDetailActivity) getActivity()).getWeightGainRangeTable();
    }

    public WeekWeightGainRange getWeightGainRangeFor(long weekNumber) {
        return ((PregnantUserDetailActivity) getActivity()).getPregnancyWeightGainRangeFor(weekNumber);
    }

    protected List<MonthGrowthRange> getMonthGrowthRangeTable() {
        return ((PregnantUserDetailActivity) getActivity()).getMonthGrowthRangeTable();
    }

    public MonthGrowthRange getMonthGrowthRangeFor(long monthNumber) {
        return ((PregnantUserDetailActivity) getActivity()).getInfantMonthGrowthRangeFor(monthNumber);
    }
}
