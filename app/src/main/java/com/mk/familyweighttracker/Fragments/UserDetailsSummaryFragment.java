package com.mk.familyweighttracker.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mk.familyweighttracker.IUserDetailsFragment;
import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsSummaryFragment extends Fragment implements IUserDetailsFragment {

    private User mUser;
    public UserDetailsSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUser(User user) {
        mUser = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details_summary, container, false);
        ((TextView) view.findViewById(R.id.userId)).setText("UserId: " + mUser.getName());
        return view;
    }
}
