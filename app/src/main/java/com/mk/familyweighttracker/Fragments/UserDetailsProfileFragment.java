package com.mk.familyweighttracker.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mk.familyweighttracker.Activities.AddPregnantUserActivity;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsProfileFragment extends Fragment implements OnNewReadingAdded {

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

        mSelectedUserId = getActivity().getIntent().getLongExtra(Constants.ARG_USER_ID, 0);
        mUser = new UserService().get(mSelectedUserId);

        initUserDetailsControls();

        initPrePregnancyControls();

        initReminderControls();

        initActionControls();

        return mFragmentView;
    }

    boolean mIsOriginator = false;
    @Override
    public boolean isOriginator() {
        return mIsOriginator;
    }

    @Override
    public void onNewReadingAdded() {
        mUser = new UserService().get(mSelectedUserId);
        initPrePregnancyControls();
    }

    private void initUserDetailsControls() {
        if(mUser.imageBytes != null) {
            ((ImageButton) mFragmentView.findViewById(R.id.view_user_image_button))
                .setImageBitmap(BitmapFactory.decodeByteArray(mUser.imageBytes, 0, mUser.imageBytes.length));
        }

        ((TextView) mFragmentView.findViewById(R.id.view_user_name))
            .setText(mUser.name);

        ((TextView) mFragmentView.findViewById(R.id.view_user_dob))
            .setText(String.format("%s (%s)", mUser.getDateOfBirthStr(), Utility.calculateAge(mUser.dateOfBirth)));
    }

    private void initPrePregnancyControls() {

        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_divider).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_weight_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_height_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_bmi_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_wt_category_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_lmp_section).setVisibility(View.GONE);

        if(mUser.getReadings(true).size() > 0) {

            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_divider).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_weight_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_height_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_bmi_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_wt_category_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_lmp_section).setVisibility(View.VISIBLE);

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_weight))
                    .setText(mUser.getStartingWeightStr());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_height))
                    .setText(mUser.getStartingHeightStr());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_bmi))
                    .setText(mUser.getBmiStr());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_wt_category))
                    .setText(mUser.getWeightCategory().toString());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_lmp))
                    .setText(mUser.getLastMenstrualPeriodStr());
        }
    }

    private void initReminderControls() {
        mFragmentView.findViewById(R.id.view_user_reminder_check_section).setVisibility(View.VISIBLE);
        mFragmentView.findViewById(R.id.view_user_reminder_day_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_reminder_time_section).setVisibility(View.GONE);

        if(mUser.enableReminder) {

            mFragmentView.findViewById(R.id.view_user_reminder_check_section).setVisibility(View.GONE);
            mFragmentView.findViewById(R.id.view_user_reminder_day_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_reminder_time_section).setVisibility(View.VISIBLE);

            final List<String> days = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

            ((TextView) mFragmentView.findViewById(R.id.view_user_reminder_day))
                    .setText(days.get(mUser.reminderDay));

            ((TextView) mFragmentView.findViewById(R.id.view_user_reminder_time))
                    .setText(String.format("%02d:%02d", mUser.reminderHour, mUser.reminderMinute));
        }
    }

    private void initActionControls() {
        mFragmentView.findViewById(R.id.view_user_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Remove User?")
                        .setMessage(Html.fromHtml("User data will be lost permanently. Do you want to continue?"))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((OnUserDeleted) getActivity()).onUserDeleted();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
        });

        mFragmentView.findViewById(R.id.view_user_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddPregnantUserActivity.class);
                intent.putExtra(Constants.ARG_USER_ID, mSelectedUserId);
                startActivityForResult(intent, Constants.REQUEST_CODE_FOR_EDIT_USER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == Constants.REQUEST_CODE_FOR_ADD_READING) {
            mUser = new UserService().get(mSelectedUserId);
            initPrePregnancyControls();

            mIsOriginator = true;
            ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            mIsOriginator = false;
        }

        if(requestCode == Constants.REQUEST_CODE_FOR_EDIT_USER) {
            // todo: refresh usersList
            mUser = new UserService().get(mSelectedUserId);
            initUserDetailsControls();
            initPrePregnancyControls();
            initReminderControls();
            getActivity().setTitle(mUser.name);
        }
    }

    public interface OnUserDeleted {
        void onUserDeleted();
    }
}
