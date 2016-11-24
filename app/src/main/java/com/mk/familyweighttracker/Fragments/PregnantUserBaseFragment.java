package com.mk.familyweighttracker.Fragments;

import android.support.v4.app.Fragment;

import com.mk.familyweighttracker.Activities.PregnantUserDetailActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.Services.PregnancyService;

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

    protected List<WeekWeightGainRange> getWeightGainRange() {
        return ((PregnantUserDetailActivity) getActivity()).getWeightGainRange();
    }

    public WeekWeightGainRange getWeightGainRangeFor(long weekNumber) {
        return ((PregnantUserDetailActivity) getActivity()).getPregnancyWeightGainRangeFor(weekNumber);
    }
}
