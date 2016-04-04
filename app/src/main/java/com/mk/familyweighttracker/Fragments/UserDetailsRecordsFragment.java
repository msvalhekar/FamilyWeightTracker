package com.mk.familyweighttracker.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddUserRecordActivity;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;
import com.mk.familyweighttracker.Services.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsRecordsFragment extends Fragment {

    private static final int NEW_USER_RECORD_ADDED_REQUEST = 1;
    private long mSelectedUserId;
    private User mSelectedUser;

    private List<UserReading> userReadingList = new ArrayList<>();
    List<WeekWeightGainRange> mWeekWeightGainRangeList;

    private View mFragmentView;
    private RecyclerView mRecyclerView;
    private SimpleItemRecyclerViewAdapter mRecyclerViewAdapter;

    public UserDetailsRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_records, container, false);

        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);
        mSelectedUser = new UserService().get(mSelectedUserId);

        userReadingList.clear();
        for (UserReading reading: mSelectedUser.getReadings(false))
            userReadingList.add(reading);

        initAddUserReadingControl();

        initReadingListControl();
        setWeightGainRangeFor();

        return mFragmentView;
    }

    private void initAddUserReadingControl() {
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.button_user_add_record);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                handleIfNoReadingsForPregnancy(getContext(), userReadingList.size());
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

                            onNewReadingAdded();

                            alertDialog.dismiss();
                        }
                    });
            }
        });
        alertDialog.show();
    }

    private void onNewReadingAdded() {
        mSelectedUser = new UserService().get(mSelectedUserId);

        List<UserReading> latestReadings = mSelectedUser.getReadings(false);
        for (int i=0; i< latestReadings.size(); i++) {
            if( userReadingList.size() > i) {
                if (userReadingList.get(i).Sequence != latestReadings.get(i).Sequence) {
                    userReadingList.add(i, latestReadings.get(i));
                    mRecyclerViewAdapter.notifyItemInserted(i);
                    break;
                }
            } else {
                userReadingList.add(i, latestReadings.get(i));
                mRecyclerViewAdapter.notifyItemInserted(i);
                break;
            }
        }

        mRecyclerView.scrollToPosition(0);
        ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
    }

    private void initReadingListControl() {
        mRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(userReadingList);

        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.user_record_list));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void setWeightGainRangeFor() {
        if (mWeekWeightGainRangeList != null)
            return;

        double baseWeight = mSelectedUser.getWeight();
        if(baseWeight == 0) return;

        mWeekWeightGainRangeList = new PregnancyService()
                .getWeightGainTableFor(baseWeight, mSelectedUser.getWeightCategory());
    }

    private WeekWeightGainRange getWeightGainTableFor(long weekNumber) {
        setWeightGainRangeFor();
        if(mWeekWeightGainRangeList == null)
            return null;

        for (WeekWeightGainRange record: mWeekWeightGainRangeList) {
            if(record.WeekNumber == weekNumber)
                return new WeekWeightGainRange( record.WeekNumber, record.MinimumWeight, record.MaximumWeight);
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == NEW_USER_RECORD_ADDED_REQUEST) {
            // update the list for new record
            //mSelectedUser = new UserService().get(mSelectedUserId);
            onNewReadingAdded();
//            List<UserReading> latestReadings = mSelectedUser.getReadings(false);
//            for (int i=0; i< latestReadings.size(); i++) {
//                if (userReadingList.get(i).Sequence != latestReadings.get(i).Sequence) {
//                    userReadingList.add(i, latestReadings.get(i));
//                    mRecyclerViewAdapter.notifyItemInserted(i);
//                    break;
//                }
//            }

            //((OnNewReadingAdded) getActivity()).onNewReadingAdded();
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        //private final User mUser;
        private List<UserReading> userReadingList;

        public SimpleItemRecyclerViewAdapter(List<UserReading> userReadingList) {
            this.userReadingList = userReadingList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_records_list_record_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            //final UserReading reading = mUser.getReadings().get(position);
            final UserReading reading = userReadingList.get(position);

            boolean highlightView = userReadingList.size() -1 == position;

            holder.setReading(reading, highlightView);

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
            return userReadingList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public UserReading mUserReading;

            public ViewHolder(View view) {
                super(view);
                mView = view;
            }

            public void setReading(UserReading reading, boolean highlightView)
            {
                mUserReading = reading;

                setPeriodControl();
                setActualWeightControl();
                setExpectedWeightControl();
            }

            private void setExpectedWeightControl() {
                WeekWeightGainRange weekWeightGainRange = getWeightGainTableFor(mUserReading.Sequence);

                if(weekWeightGainRange == null) return;

                ((TextView) mView.findViewById(R.id.record_item_weight_exp_min))
                        .setText(String.format("%.2f", weekWeightGainRange.MinimumWeight));

                double diff = mUserReading.Weight - weekWeightGainRange.MinimumWeight;
                String diffSign = "";
                int diffColor = 1;
                if(diff < 0) {
                    diffColor = Color.RED;
                } else if(diff > 0) {
                    diffSign = "+";
                    diffColor = Color.BLUE;
                }
                TextView weightDiffView = ((TextView) mView.findViewById(R.id.record_item_weight_exp_min_diff));
                weightDiffView.setText(String.format("(%s%.2f)", diffSign, diff));
                if(diff != 0)
                    weightDiffView.setTextColor(diffColor);

                ((TextView) mView.findViewById(R.id.record_item_weight_exp_max))
                        .setText(String.format("%.2f", weekWeightGainRange.MaximumWeight));

                double maxExpWtdiff = mUserReading.Weight - weekWeightGainRange.MaximumWeight;
                String maxExpWtDiffSign = "";
                int maxExpWtDiffColor = 1;
                if(maxExpWtdiff < 0) {
                    maxExpWtDiffColor = Color.BLUE;
                } else if(maxExpWtdiff > 0){
                    maxExpWtDiffSign = "+";
                    maxExpWtDiffColor = Color.RED;
                }
                TextView maxExpWeightDiffView = ((TextView) mView.findViewById(R.id.record_item_weight_exp_max_diff));
                maxExpWeightDiffView.setText(String.format("(%s%.2f)", maxExpWtDiffSign, maxExpWtdiff));
                if(maxExpWtdiff != 0)
                    maxExpWeightDiffView.setTextColor(maxExpWtDiffColor);
            }

            private void setActualWeightControl() {
                ((TextView) mView.findViewById(R.id.record_item_weight))
                        .setText(String.format("%.2f", mUserReading.Weight));

                UserReading previousReading = mSelectedUser.findReadingBefore(mUserReading.Sequence);
                if(previousReading != null) {
                    double diff = mUserReading.Weight - previousReading.Weight;
                    String diffSign = "";
                    int diffColor = 1;
                    if (diff < 0) {
                        diffColor = Color.RED;
                    } else if (diff > 0) {
                        diffSign = "+";
                        diffColor = Color.BLUE;
                    }
                    TextView weightDiffView = ((TextView) mView.findViewById(R.id.record_item_weight_diff));
                    weightDiffView.setText(String.format("(%s%.2f)", diffSign, diff));
                    if (diff != 0)
                        weightDiffView.setTextColor(diffColor);
                }
            }

            private void setPeriodControl() {
                ((TextView) mView.findViewById(R.id.record_item_period_no))
                        .setText(String.format("%s %02d", mSelectedUser.trackingPeriod, mUserReading.Sequence));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                ((TextView) mView.findViewById(R.id.record_item_taken_on))
                        .setText(dateFormat.format(mUserReading.TakenOn));
            }
        }
    }

    public interface OnNewReadingAdded {
        void onNewReadingAdded();
    }
}
