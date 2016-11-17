package com.mk.familyweighttracker.Fragments;

import android.support.v4.app.Fragment;

import com.mk.familyweighttracker.Activities.PregnantUserDetailActivity;
import com.mk.familyweighttracker.Models.User;

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
}
