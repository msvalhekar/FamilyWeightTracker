package com.mk.familyweighttracker.Fragments;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsProfileFragment extends Fragment implements UserDetailsRecordsFragment.OnNewReadingAdded {

    private User mUser;
    private long mSelectedUserId;

    private View mFragmentView;

    public UserDetailsProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_profile, container, false);

        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);

        onNewReadingAdded();

        initDeleteUserControl();

        return mFragmentView;
    }

    @Override
    public void onNewReadingAdded() {
        mUser = new UserService().get(mSelectedUserId);

        initControls();
    }

    private void initControls() {
        if(mUser.imageBytes != null) {
            ((ImageButton) mFragmentView.findViewById(R.id.show_user_image))
                .setImageBitmap(BitmapFactory.decodeByteArray(mUser.imageBytes, 0, mUser.imageBytes.length));
        }

        ((TextView) mFragmentView.findViewById(R.id.show_user_name))
            .setText(mUser.name);

        ((TextView) mFragmentView.findViewById(R.id.show_user_gender))
            .setText(mUser.isMale ? "Male" : "Female");

        ((TextView) mFragmentView.findViewById(R.id.show_user_dob))
            .setText(mUser.getDateOfBirthStr());

        ((TextView) mFragmentView.findViewById(R.id.show_user_age))
            .setText(Utility.calculateAge(mUser.dateOfBirth));

        ((TextView) mFragmentView.findViewById(R.id.show_user_weight_unit))
            .setText(mUser.weightUnit.toString());

        ((TextView) mFragmentView.findViewById(R.id.show_user_height_unit))
            .setText(mUser.heightUnit.toString());

        // todo: set below section only for pregnant member
        {
            ((GridLayout) mFragmentView.findViewById(R.id.show_user_pregnancy_section))
                    .setVisibility(View.VISIBLE);

            ((TextView) mFragmentView.findViewById(R.id.show_user_pregnancy_label))
                    .setText("Pre-pregnancy details");

            ((TextView) mFragmentView.findViewById(R.id.show_user_pre_pregnancy_weight))
                    .setText(String.format("%.2f", mUser.getStartingWeight()));

            ((TextView) mFragmentView.findViewById(R.id.show_user_pre_pregnancy_height))
                    .setText(String.valueOf(mUser.getStartingHeight()));

            ((TextView) mFragmentView.findViewById(R.id.show_user_pre_pregnancy_bmi))
                    .setText(String.format("%.2f", mUser.getBmi()));

            ((TextView) mFragmentView.findViewById(R.id.show_user_pre_pregnancy_weight_category))
                    .setText(mUser.getWeightCategory().toString());
        }
    }

    private void initDeleteUserControl() {
        mFragmentView.findViewById(R.id.remove_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Warning: Delete User")
                        .setMessage(Html.fromHtml("Deleting user will remove user data permanently.<br/>Do you want to continue?"))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new UserService().remove(mSelectedUserId);
                                ((OnUserDeleted) getActivity()).onUserDeleted();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
        });
    }

    public interface OnUserDeleted {
        void onUserDeleted();
    }
}
