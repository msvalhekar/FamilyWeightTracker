package com.mk.familyweighttracker.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mk.familyweighttracker.Activities.AddPregnantUserActivity;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserProfileFragment extends PregnantUserBaseFragment implements OnNewReadingAdded {

    private User mUser;
    private long mSelectedUserId;

    private View mFragmentView;

    public PregnantUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_profile, container, false);

        mSelectedUserId = getUserId();
        mUser = getUser();

        initUserDetailsControls();
        initTwinsControls();
        initDeliveryDueDateControls();
        initPrePregnancyControls();
        initReminderControls();
        initActionControls();

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                Constants.AnalyticsEvents.UserDetailsProfile,
                String.format(Constants.AnalyticsActions.UserDetailsProfile, mUser.name),
                null);

        return mFragmentView;
    }

    private void initDeliveryDueDateControls() {
        ((TextView) mFragmentView.findViewById(R.id.view_user_delivery_due_date))
                .setText(mUser.getDeliveryDueDateStr());

        mFragmentView.findViewById(R.id.view_user_delivery_remaining_section).setVisibility(View.GONE);
        if(mUser.deliveryDueDate != null) {
            mFragmentView.findViewById(R.id.view_user_delivery_remaining_section).setVisibility(View.VISIBLE);
            ((TextView) mFragmentView.findViewById(R.id.view_user_delivery_remaining))
                    .setText(mUser.getDeliveryRemainingStr());
        }
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
        ((ImageButton) mFragmentView.findViewById(R.id.view_user_image_button)).setImageBitmap(mUser.getImageAsBitmap(true));

        ((TextView) mFragmentView.findViewById(R.id.view_user_name))
            .setText(mUser.name);

        if(mUser.dateOfBirth != null) {
            ((TextView) mFragmentView.findViewById(R.id.view_user_dob))
                    .setText(String.format("%s (%s)", mUser.getDateOfBirthStr(), Utility.calculateAge(mUser.dateOfBirth)));
        }
    }

    private void initPrePregnancyControls() {

        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_divider).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_weight_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_height_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_bmi_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_lmp_section).setVisibility(View.GONE);

        if(mUser.getReadingsCount() > 0) {

            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_divider).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_weight_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_height_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_bmi_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_lmp_section).setVisibility(View.VISIBLE);

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_weight))
                    .setText(mUser.getStartingWeightStr());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_height))
                    .setText(mUser.getStartingHeightStr());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_bmi))
                    .setText(String.format("%s - %s", mUser.getBmiStr(), mUser.getWeightCategory().toString()));

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_lmp))
                    .setText(mUser.getLastMenstrualPeriodStr());
        }
    }

    private void initTwinsControls() {
        String message = getResources().getString(
                mUser.haveTwins ? R.string.yes_label : R.string.no_label);

        ((TextView) mFragmentView.findViewById(R.id.view_user_delivery_twins))
                .setText(message);
    }

    private void initReminderControls() {
        ((TextView) mFragmentView.findViewById(R.id.view_user_reminder_day)).setText(R.string.value_not_set_message);
        if(mUser.enableReminder) {
            final List<String> days = Arrays.asList(
                    getString(R.string.WEEK_DAY_SUNDAY),
                    getString(R.string.WEEK_DAY_MONDAY),
                    getString(R.string.WEEK_DAY_TUESDAY),
                    getString(R.string.WEEK_DAY_WEDNESDAY),
                    getString(R.string.WEEK_DAY_THURSDAY),
                    getString(R.string.WEEK_DAY_FRIDAY),
                    getString(R.string.WEEK_DAY_SATURDAY));
            String reminderText = String.format("%02d:%02d %s %s", mUser.reminderHour, mUser.reminderMinute, getString(R.string.profile_reminder_message), days.get(mUser.reminderDay));
            ((TextView) mFragmentView.findViewById(R.id.view_user_reminder_day)).setText(reminderText);
        }
    }

    private void initActionControls() {
        mFragmentView.findViewById(R.id.view_user_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(getString(R.string.delete_user_warning_title))
                        .setMessage(Html.fromHtml(getString(R.string.delete_user_warning_message)))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes_label), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((OnUserDeleted) getActivity()).onUserDeleted();
                            }
                        })
                        .setNegativeButton(getString(R.string.no_label), null)
                        .create()
                        .show();
            }
        });

        mFragmentView.findViewById(R.id.view_user_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddPregnantUserActivity.class);
                intent.putExtra(Constants.ExtraArg.USER_ID, mSelectedUserId);
                startActivityForResult(intent, Constants.RequestCode.EDIT_USER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == Constants.RequestCode.ADD_READING) {
            mUser = new UserService().get(mSelectedUserId);
            initPrePregnancyControls();

            mIsOriginator = true;
            ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            mIsOriginator = false;
        }

        if(requestCode == Constants.RequestCode.EDIT_USER) {
            // todo: refresh usersList
            mUser = new UserService().get(mSelectedUserId);
            initUserDetailsControls();
            initTwinsControls();
            initPrePregnancyControls();
            initReminderControls();
        }
    }
//
//    public void onUserDeleted() {
//        mUser.removeReminder();
//        new UserService().remove(mSelectedUserId);
//
//        Analytic.setData(Constants.AnalyticsCategories.Activity,
//                Constants.AnalyticsEvents.UserDelete,
//                String.format(Constants.AnalyticsActions.UserDeleted, mUser.name),
//                null);
//
//        String message = String.format(getString(R.string.user_removed_message), mUser.name);
//        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();
//        //finish();
//    }

    public interface OnUserDeleted {
        void onUserDeleted();
    }
}
