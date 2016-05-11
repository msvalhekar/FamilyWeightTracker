package com.mk.familyweighttracker.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddFirstReadingActivity;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Date;

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

        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);
        mUser = new UserService().get(mSelectedUserId);

        initControls();

        initPrePregnancyControls();

        initDeleteUserControl();

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
    }

    private void initPrePregnancyControls() {

        Button button = ((Button) mFragmentView.findViewById(R.id.show_user_first_reading_button));
        button.setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.show_user_pregnancy_section)
                .setVisibility(View.GONE);

        if(mUser.getReadings(true).size() == 0) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), AddFirstReadingActivity.class);
                            intent.putExtra(UserDetailActivity.ARG_USER_ID, mSelectedUserId);
                            intent.putExtra(UserDetailActivity.ARG_EDIT_READING_ID, (long)0);
                            startActivityForResult(intent, UserDetailActivity.READING_ADD_REQUEST);
                        }
                    });
        } else {
            mFragmentView.findViewById(R.id.show_user_pregnancy_section)
                    .setVisibility(View.VISIBLE);

            ((TextView) mFragmentView.findViewById(R.id.show_user_pregnancy_label))
                    .setText("Pre-pregnancy details");

            ((TextView) mFragmentView.findViewById(R.id.show_user_pre_pregnancy_weight))
                    .setText(mUser.getStartingWeightStr());

            ((TextView) mFragmentView.findViewById(R.id.show_user_pre_pregnancy_height))
                    .setText(mUser.getStartingHeightStr());

            ((TextView) mFragmentView.findViewById(R.id.show_user_pre_pregnancy_bmi))
                    .setText(mUser.getBmiStr());

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == UserDetailActivity.READING_ADD_REQUEST) {
            mUser = new UserService().get(mSelectedUserId);
            initPrePregnancyControls();

            mIsOriginator = true;
            ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            mIsOriginator = false;
        }
    }

    public interface OnUserDeleted {
        void onUserDeleted();
    }
}
