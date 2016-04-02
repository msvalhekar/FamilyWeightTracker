package com.mk.familyweighttracker.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddUserRecordActivity;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsRecordsFragment extends Fragment {

    private static final int NEW_USER_RECORD_ADDED_REQUEST = 1;
    private long mSelectedUserId;

    private View mFragmentView;
    private RecyclerView mRecyclerView;

    public UserDetailsRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_records, container, false);

        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);

        initAddUserReadingControl();

        initReadingListControl();

        return mFragmentView;
    }

    private void initAddUserReadingControl() {
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.button_user_add_record);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<UserReading> readings = new UserService().get(mSelectedUserId).getReadings();

                handleIfNoReadingsForPregnancy(getContext(), readings.size());
            }
        });
    }

    private void handleIfNoReadingsForPregnancy(final Context context, int readingCount) {

        if(readingCount > 0) {
            Intent intent = new Intent(getContext(), AddUserRecordActivity.class);
            intent.putExtra(UserDetailActivity.ARG_USER_ID, mSelectedUserId);
            startActivityForResult(intent, NEW_USER_RECORD_ADDED_REQUEST);
            return;
        };

        final WeightUnit[] userWeightUnit = {WeightUnit.kg};
        final HeightUnit[] userHeightUnit = {HeightUnit.cm};

        final UserReading userReading = new UserReading();
        userReading.UserId = mSelectedUserId;
        userReading.Sequence = 0;
        userReading.TakenOn = new Date();

        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_user_first_reading, null);

        ((RadioGroup) dialogView.findViewById(R.id.add_first_reading_weight_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                     boolean isWeightUnitKg = (checkedId == R.id.add_first_reading_weight_unit_kg);
                     userWeightUnit[0] = isWeightUnitKg ? WeightUnit.kg : WeightUnit.lb;
                    }
                });
        ((RadioButton) dialogView.findViewById(R.id.add_first_reading_weight_unit_kg)).setChecked(true);

        ((RadioGroup) dialogView.findViewById(R.id.add_first_reading_height_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                     boolean isHeightUnitCm = (checkedId == R.id.add_first_reading_height_unit_cm);
                     userHeightUnit[0] = isHeightUnitCm ? HeightUnit.cm : HeightUnit.inch;
                    }
                });
        ((RadioButton) dialogView.findViewById(R.id.add_first_reading_height_unit_cm)).setChecked(true);

        final EditText weightView = ((EditText) dialogView.findViewById(R.id.add_first_reading_weight));
        weightView.setText("60.00");

        final EditText heightView = ((EditText) dialogView.findViewById(R.id.add_first_reading_height));
        heightView.setText("160");

        final android.support.v7.app.AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(context,
                                        "Pre-preganancy reading is must for accuracy of calculations.",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String weightString = weightView.getText().toString();
                            if(TextUtils.isEmpty(weightString)) {
                                weightView.setError("Value required");
                                return;
                            }
                            String heightString = heightView.getText().toString();
                            if(TextUtils.isEmpty(heightString)) {
                                heightView.setError("Value required");
                                return;
                            }

                            userReading.Weight = Double.valueOf(weightString);
                            userReading.Height = Integer.valueOf(heightString);

                            new UserService().addReading(userReading);
                            new UserService().update(mSelectedUserId, userWeightUnit[0], userHeightUnit[0]);

                            setupRecyclerView(mRecyclerView);
                            ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
                            alertDialog.dismiss();
                        }
                    });
            }
        });
        alertDialog.show();
    }

    private void initReadingListControl() {
        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.user_record_list));
        setupRecyclerView(mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        User user = new UserService().get(mSelectedUserId);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(user));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_USER_RECORD_ADDED_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // update the list for new record
                setupRecyclerView(mRecyclerView);
                ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final User mUser;

        public SimpleItemRecyclerViewAdapter(User user) {
            mUser = user;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_record_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final UserReading reading = mUser.getReadings().get(position);

            holder.setReading(mUser, reading);

//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, UserDetailActivity.class);
//                    intent.putExtra(UserDetailActivity.ARG_USER_ID, user.getId());
//                    context.startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mUser.getReadings().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNameView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.user_item_name);
            }

            public void setReading(User user, UserReading reading)
            {
                mNameView.setText(String.valueOf(reading.Weight) + " " + user.weightUnit + ", "
                                + String.valueOf(reading.Height) + " " + user.heightUnit);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }

    public interface OnNewReadingAdded {
        void onNewReadingAdded();
    }
}
