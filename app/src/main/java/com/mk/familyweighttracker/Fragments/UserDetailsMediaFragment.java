package com.mk.familyweighttracker.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsMediaFragment extends Fragment implements OnNewReadingAdded {

    private long mSelectedUserId;
    private User mUser;
    private View mFragmentView;

    public UserDetailsMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_media, container, false);
        //getActivity().setTitle("My Wonderful Pregnancy");

        mSelectedUserId = getActivity().getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mUser = new UserService().get(mSelectedUserId);

        initActionControls();

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                Constants.AnalyticsEvents.UserDetailsMedia,
                String.format(Constants.AnalyticsActions.UserDetailsMedia, mUser.name),
                null);

        return mFragmentView;
    }

    private void initActionControls() {
        mFragmentView.findViewById(R.id.media_slideshow_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mUser.getReadingsCount() == 0) {
                        Toast.makeText(v.getContext(), R.string.user_readings_not_found_message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.UserSlideshowActivity.class);
                    intent.putExtra(Constants.ExtraArg.USER_ID, mSelectedUserId);
                    startActivity(intent);
                }
            });

        mFragmentView.findViewById(R.id.media_select_bkgd_music_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Choose Background Music", Toast.LENGTH_SHORT).show();
                }
            });

        mFragmentView.findViewById(R.id.media_export_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Export", Toast.LENGTH_SHORT).show();
                }
            });

        mFragmentView.findViewById(R.id.media_share_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Share", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private boolean mIsOriginator = false;

    @Override
    public boolean isOriginator() {
        return mIsOriginator;
    }

    @Override
    public void onNewReadingAdded() {
        mUser = new UserService().get(mSelectedUserId);
        if(mUser.getReadingsCount() == 0) return;
    }
}
