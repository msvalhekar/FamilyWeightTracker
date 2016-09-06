package com.mk.familyweighttracker.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mk.familyweighttracker.Activities.AddFirstReadingActivity;
import com.mk.familyweighttracker.Activities.AddReadingActivity;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;
import com.mk.familyweighttracker.Services.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsRecordsFragment extends Fragment implements OnNewReadingAdded {

    private long mSelectedUserId;
    private User mSelectedUser;

    private List<UserReading> userReadingList = new ArrayList<>();
    List<WeekWeightGainRange> mWeekWeightGainRangeList;

    private View mFragmentView;
    private RecyclerView mRecyclerView;
    private SimpleItemRecyclerViewAdapter mRecyclerViewAdapter;
    private boolean bFirstReadingChanged;

    public UserDetailsRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_records, container, false);

        mWeekWeightGainRangeList = null;
        mSelectedUserId = getActivity().getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mSelectedUser = new UserService().get(mSelectedUserId);

        userReadingList.clear();
        for (UserReading reading: mSelectedUser.getReadings(false))
            userReadingList.add(reading);

        initAddUserReadingControl();

        initReadingListControl();

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                Constants.AnalyticsEvents.UserDetailsRecords,
                String.format(Constants.AnalyticsActions.UserDetailsRecords, mSelectedUser.name),
                null);

        return mFragmentView;
    }

    private void initAddUserReadingControl() {
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.button_user_add_record);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddReadingActivity.class);
                intent.putExtra(Constants.ExtraArg.USER_ID, mSelectedUserId);
                startActivityForResult(intent, Constants.RequestCode.ADD_READING);
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
        mSelectedUser = new UserService().get(mSelectedUserId);

        List<UserReading> latestReadings = mSelectedUser.getReadings(false);
        int i=0;
        for (; i< userReadingList.size(); i++) {
            if (userReadingList.get(i).Sequence != latestReadings.get(i).Sequence) {
                break;
            }
        }
        userReadingList.add(i, latestReadings.get(i));
        mRecyclerViewAdapter.notifyDataSetChanged();
        //mRecyclerViewAdapter.notifyItemInserted(i);
        //mRecyclerView.scrollToPosition(0);
        mFragmentView.findViewById(R.id.empty_view).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.user_records_list_record_content_help).setVisibility(View.VISIBLE);
    }

    private void initReadingListControl() {

        mRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter();
        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.user_record_list));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.notifyDataSetChanged();


        mFragmentView.findViewById(R.id.card_view)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getContext())
                                .setMessage(Html.fromHtml(getLegendMessage()))
                                .setPositiveButton("Got it", null)
                                .create()
                                .show();

                        Analytic.setData(Constants.AnalyticsCategories.Activity,
                                Constants.AnalyticsEvents.UserReadingHelp,
                                String.format(Constants.AnalyticsActions.ShowUserReadingHelp, mSelectedUser.name),
                                null);

                    }
                });

        mFragmentView.findViewById(R.id.empty_view).setVisibility(View.GONE);
        if(userReadingList.size() == 0) {
            mFragmentView.findViewById(R.id.user_records_list_record_content_help).setVisibility(View.GONE);

            mFragmentView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_title)).setText("No data found.");
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_description)).setText("Add reading(s) using '+' button below.");
        }
    }

    private String getLegendMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("<big>");
        builder.append("<b>" + "Period" + "</b>");
        builder.append("<br />" + "Week number, for which the reading was taken.");
        builder.append("<br /><b>" + "Date" + "</b>");
        builder.append("<br />" + "The date when the reading was recorded.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Actual Wt" + "</b>");
        builder.append("<br />" + "The actual weight recorded for this week.");
        builder.append("<br /><b>" + "(wt gain)" + "</b>");
        builder.append("<br />" + "The difference between actual weight recorded for this week and previous week.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Exp Min" + "</b>");
        builder.append("<br />" + "The minimum weight expected for this week, as per Pregnancy Weight Gain chart.");
        builder.append("<br /><b>" + "(v/s actual)" + "</b>");
        builder.append("<br />" + "The difference between actual weight recorded and minimum expected weight for this week.");
        builder.append("<br /><font color=\"#ff0000\">RED</font>: indicates that the actual weight is lower than minimum expected, may need to gain more weight.");
        builder.append("<br /><font color=\"#0000ff\">BLUE</font>: indicates that the actual weight is more than minimum expected, good.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Exp Max" + "</b>");
        builder.append("<br />" + "The maximum weight expected for this week, as per Pregnancy Weight Gain chart.");
        builder.append("<br /><b>" + "(v/s actual)" + "</b>");
        builder.append("<br />" + "The difference between actual weight recorded and maximum expected weight for this week.");
        builder.append("<br /><font color=\"#0000ff\">BLUE</font>: indicates that the actual weight is lower than minimum expected, good.");
        builder.append("<br /><font color=\"#ff0000\">RED</font>: indicates that the actual weight is more than maximum expected, may need to loose weight.");
        builder.append("</big>");
        return builder.toString();
    }

    private void setWeightGainRangeFor() {
        if (mWeekWeightGainRangeList != null)
            return;

        double baseWeight = mSelectedUser.getStartingWeight();
        if(baseWeight == 0) return;

        mWeekWeightGainRangeList = new PregnancyService()
            .getWeightGainTableFor(baseWeight, mSelectedUser.getWeightCategory(), mSelectedUser.weightUnit);
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

        if(requestCode == Constants.RequestCode.ADD_READING) {
            onNewReadingAdded();

            mIsOriginator = true;
            ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            mIsOriginator = false;
        }
        else if(requestCode == Constants.RequestCode.EDIT_READING) {
            if(bFirstReadingChanged) {
                mWeekWeightGainRangeList = null;
                bFirstReadingChanged = false;
            }

            mSelectedUser = new UserService().get(mSelectedUserId);

            userReadingList.clear();
            List<UserReading> latestReadings = mSelectedUser.getReadings(false);
            for (int i=0; i< latestReadings.size(); i++) {
                userReadingList.add(latestReadings.get(i));
            }
            mRecyclerViewAdapter.notifyDataSetChanged();

            mIsOriginator = true;
            ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            mIsOriginator = false;
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        public SimpleItemRecyclerViewAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_records_list_record_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final UserReading reading = userReadingList.get(position);

            holder.setReading(reading);
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

            public void setReading(UserReading reading)
            {
                mUserReading = reading;

                mView.findViewById(R.id.user_record_for_pregnancy_section)
                        .setVisibility(mUserReading.Sequence == 0 ? View.VISIBLE : View.GONE);

//                if(mUserReading.Sequence == 0) {
//                    mView.findViewById(R.id.user_record_for_pregnancy_section).setVisibility(View.VISIBLE);
//                } else {
//                    mView.findViewById(R.id.user_record_for_pregnancy_section).setVisibility(View.GONE);
//                }

                setPeriodControl();
                setActualWeightControl();
                setExpectedWeightControl();
                setImageControl();
                setViewControl();
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

            private void setImageControl() {
                ImageButton button = ((ImageButton) mView.findViewById(R.id.user_record_image_button));
                Bitmap bitmap = null;
                if(mUserReading.ImageBytes != null) {
                    bitmap = ImageUtility.getCircularBitmap(
                                BitmapFactory.decodeByteArray(mUserReading.ImageBytes, 0, mUserReading.ImageBytes.length));
                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), mUserReading.getDefaultImage());
                }
                button.setImageBitmap(bitmap);
            }

            private void setViewControl() {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mUserReading.Sequence == 0) {
                            bFirstReadingChanged = true;
                        }
                        Intent intent = new Intent(getContext(), AddReadingActivity.class);
                        intent.putExtra(Constants.ExtraArg.USER_ID, mSelectedUserId);
                        intent.putExtra(Constants.ExtraArg.EDIT_READING_ID, mUserReading.Id);
                        startActivityForResult(intent, Constants.RequestCode.EDIT_READING);
                    }
                });
            }
        }
    }
}
