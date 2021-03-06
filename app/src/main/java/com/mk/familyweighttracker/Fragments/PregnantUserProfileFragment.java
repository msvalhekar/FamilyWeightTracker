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
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddPregnantUserActivity;
import com.mk.familyweighttracker.Enums.ImageShapeType;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserProfileFragment extends PregnantUserBaseFragment {

    private View mFragmentView;

    public PregnantUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_profile, container, false);

        initActionControls();

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                getUser().getDetailsProfileEvent(),
                String.format(Constants.AnalyticsActions.UserDetailsProfile, getUser().name, getUser().getTypeShortName()),
                null);

        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        initUserDetailsControls();
        initTwinsControls();
        initDeliveryDueDateControls();
        initPrePregnancyControls();
        initReminderControls();
    }

    private void initDeliveryDueDateControls() {
        if(!getUser().isPregnant()) {
            mFragmentView.findViewById(R.id.view_user_delivery_section_divider).setVisibility(View.GONE);
            mFragmentView.findViewById(R.id.view_user_delivery_due_date_section).setVisibility(View.GONE);
            mFragmentView.findViewById(R.id.view_user_delivery_remaining_section).setVisibility(View.GONE);
            return;
        }

        ((TextView) mFragmentView.findViewById(R.id.view_user_delivery_due_date))
                .setText(getUser().getDeliveryDueDateStr());

        mFragmentView.findViewById(R.id.view_user_delivery_remaining_section).setVisibility(View.GONE);
        if(getUser().deliveryDueDate != null) {
            mFragmentView.findViewById(R.id.view_user_delivery_remaining_section).setVisibility(View.VISIBLE);
            ((TextView) mFragmentView.findViewById(R.id.view_user_delivery_remaining))
                    .setText(getUser().getDeliveryRemainingStr());
        }
    }

    private void initUserDetailsControls() {
        try {
            ((ImageButton) mFragmentView.findViewById(R.id.view_user_image_button))
                    .setImageBitmap(getUser().getImageAsBitmap(ImageShapeType.Oval, ImageUtility.OneHundred));
        } catch (OutOfMemoryError e) { }

        ((TextView) mFragmentView.findViewById(R.id.view_user_name)).setText(getUser().name);

        if(getUser().dateOfBirth != null) {
            ((TextView) mFragmentView.findViewById(R.id.view_user_dob))
                    .setText(String.format("%s (%s)", getUser().getDateOfBirthStr(), Utility.getAge(getUser().dateOfBirth, !getUser().isPregnant())));
        }
    }

    private void initPrePregnancyControls() {

        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_divider).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_weight_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_height_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_bmi_section).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.view_user_pre_pregnancy_lmp_section).setVisibility(View.GONE);

        if(getUser().isPregnant() && getUser().getReadingsCount() > 0 && getUser().getPrepregnancyReading() != null) {

            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_divider).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_weight_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_height_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_bmi_section).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.view_user_pre_pregnancy_lmp_section).setVisibility(View.VISIBLE);

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_weight))
                    .setText(getUser().getStartingWeightStr());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_height))
                    .setText(getUser().getStartingHeightStr());

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_bmi))
                    .setText(String.format("%s - %s", getUser().getBmiStr(), getUser().getWeightCategory().toString()));

            ((TextView) mFragmentView.findViewById(R.id.view_user_prepreg_lmp))
                    .setText(getUser().getLastMenstrualPeriodStr());
        }
    }

    private void initTwinsControls() {
        if(!getUser().isPregnant()) {
            mFragmentView.findViewById(R.id.view_user_delivery_twins_section).setVisibility(View.GONE);
            return;
        }
        String message = getResources().getString(getUser().haveTwins ? R.string.yes_label : R.string.no_label);
        ((TextView) mFragmentView.findViewById(R.id.view_user_delivery_twins)).setText(message);
    }

    private void initReminderControls() {
        ((TextView) mFragmentView.findViewById(R.id.view_user_reminder_day_label))
                .setText(getUser().isPregnant()
                        ? R.string.user_details_weekly_reminder_text
                        : R.string.user_details_monthly_reminder_text);

        ((TextView) mFragmentView.findViewById(R.id.view_user_reminder_day)).setText(R.string.value_not_set_message);
        if(getUser().enableReminder) {
            final List<String> days = getUser().isPregnant() ? Utility.getWeekDays() : Utility.getMonthDays();
            String reminderText = String.format("%02d:%02d %s %s",
                    getUser().reminderHour,
                    getUser().reminderMinute,
                    getString(R.string.profile_reminder_message),
                    days.get(getUser().reminderDay));
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
                                onUserDelete();
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
                intent.putExtra(Constants.ExtraArg.USER_ID, getUserId());
                startActivityForResult(intent, Constants.RequestCode.EDIT_USER);
            }
        });
    }

    private void onUserDelete() {
        Analytic.setData(Constants.AnalyticsCategories.Activity,
                getUser().getDeleteUserEvent(),
                String.format(Constants.AnalyticsActions.UserDeleted, getUser().name, getUser().getTypeShortName()),
                null);

        getUser().removeReminder();
        new UserService().remove(getUserId());

        String message = String.format(getString(R.string.user_removed_message), getUser().name);
        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();

        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if(resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case Constants.RequestCode.ADD_READING:
            case Constants.RequestCode.EDIT_USER:
                onUserDataChange();
                break;
        }
    }
}
