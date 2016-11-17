package com.mk.familyweighttracker.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddPregnancyReadingActivity;
import com.mk.familyweighttracker.Activities.AddPregnantUserActivity;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
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
public class PregnantUserReadingsFragment extends Fragment implements OnNewReadingAdded {

    private long mSelectedUserId;
    private User mSelectedUser;

    private List<UserReading> userReadingList = new ArrayList<>();
    List<WeekWeightGainRange> mWeekWeightGainRangeList;

    private View mFragmentView;
    private RecyclerView mRecyclerView;
    private UserReadingRecyclerViewAdapter readingAdapter;
    private boolean bFirstReadingChanged;

    public PregnantUserReadingsFragment() {
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
            public void onClick(final View view) {
                if (!mSelectedUser.maxReadingsReached()) {
//                    // TODO: 31-10-2016 show dialog with 0 and 1-40 and 41 readings and purpose
//                    // if not shown already - sharedPreference
//
//                    LinearLayout linearLayout = new LinearLayout(view.getContext());
//                    linearLayout.setOrientation(LinearLayout.VERTICAL);
//                    TableLayout tableLayout = new TableLayout(view.getContext());
//                    tableLayout.setOrientation(LinearLayout.HORIZONTAL);
//                    TableRow tableRow = new TableRow(view.getContext());
//                    TextView weekNumberView = new TextView(view.getContext());
//                    weekNumberView.setText("Week Number");
//                    tableRow.addView(weekNumberView);
//                    tableLayout.addView(tableRow);
//                    linearLayout.addView(tableLayout);
//                    //View typesView = mFragmentView.findViewById(R.id.pregnancy_reading_types);
//                    //((ViewGroup) typesView.getParent()).removeView(typesView);
//                    new AlertDialog.Builder(view.getContext())
//                            .setTitle("Pregnancy Reading Types")
//                            //.setCancelable(false)
//                            //.setView(R.id.pregnancy_reading_types)
//                            //.setView(typesView)
//                            .setView(linearLayout)
//                            .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(view.getContext(), "reading types shown", Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .create()
//                            .show();

                    if (promptIfDeliveryDateNotSet(view.getContext()))
                        return;

                    if (showDialogIfConflictingReading(view.getContext()))
                        return;

                    gotoAddReading();
                } else {
                    Toast.makeText(view.getContext(),
                            R.string.Maximum40WeeksDataSupportedMessage,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean showDialogIfConflictingReading(Context context) {
        long estimatedSequence = mSelectedUser.getEstimatedSequence();
        final UserReading reading = new UserService().getReadingBySequence(estimatedSequence);
        if (reading != null) {
            String messageFormat = getResources().getString(R.string.add_conflicting_week_message);

            new AlertDialog.Builder(context)
                    .setTitle(R.string.add_conflicting_week_title)
                    .setMessage(String.format(messageFormat, reading.Sequence))
                    .setPositiveButton(context.getString(R.string.add_conflicting_week_positive_action_label), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gotoEditReading(reading.Id);
                        }
                    })
                    .setNegativeButton(context.getString(R.string.add_conflicting_week_negative_action_label), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gotoAddReading();
                        }
                    })
                    .create()
                    .show();
            return true;
        }
        return false;
    }

    private void gotoEditReading(long readingId) {
        Intent intent = new Intent(getContext(), AddPregnancyReadingActivity.class);
        intent.putExtra(Constants.ExtraArg.USER_ID, mSelectedUserId);
        intent.putExtra(Constants.ExtraArg.EDIT_READING_ID, readingId);
        startActivityForResult(intent, Constants.RequestCode.EDIT_READING);
    }

    private void gotoAddReading() {
        Intent intent = new Intent(getContext(), AddPregnancyReadingActivity.class);
        intent.putExtra(Constants.ExtraArg.USER_ID, mSelectedUserId);
        startActivityForResult(intent, Constants.RequestCode.ADD_READING);
    }

    private boolean promptIfDeliveryDateNotSet(final Context context) {
        if (mSelectedUser.deliveryDueDate != null)
            return false;

        new AlertDialog.Builder(context)
                .setTitle(R.string.delivery_due_date_required_title)
                .setMessage(R.string.delivery_due_date_required_message)
                .setPositiveButton(context.getString(R.string.button_due_date_action_lable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, AddPregnantUserActivity.class);
                        intent.putExtra(Constants.ExtraArg.USER_ID, mSelectedUserId);
                        startActivityForResult(intent, Constants.RequestCode.EDIT_USER);
                    }
                })
                .create()
                .show();
        return true;
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
        readingAdapter.notifyDataSetChanged();

        mFragmentView.findViewById(R.id.empty_view).setVisibility(View.GONE);
        mFragmentView.findViewById(R.id.user_records_list_record_content_help).setVisibility(View.VISIBLE);
    }

    private void initReadingListControl() {

        readingAdapter = new UserReadingRecyclerViewAdapter();
        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.user_record_list));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(readingAdapter);
        readingAdapter.notifyDataSetChanged();

        mFragmentView.findViewById(R.id.card_view)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getContext())
                                .setMessage(Html.fromHtml(getLegendMessage()))
                                .setPositiveButton(getString(R.string.got_it_label), null)
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
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_title)).setText(R.string.user_readings_not_found_message);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_description)).setText(R.string.user_readings_add_reading_message);
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
            .getWeightGainTableFor(baseWeight, mSelectedUser.getWeightCategory(), mSelectedUser.weightUnit, mSelectedUser.haveTwins);
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
            readingAdapter.notifyDataSetChanged();

            mIsOriginator = true;
            ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            mIsOriginator = false;
        }
    }

    private class UserReadingRecyclerViewAdapter
            extends RecyclerView.Adapter<UserReadingRecyclerViewAdapter.ViewHolder> {

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

                View sectionView = mView.findViewById(R.id.user_record_for_extra_section);
                sectionView.setVisibility(View.GONE);

                if(mUserReading.isPrePregnancyReading()) {
                    sectionView.setVisibility(View.VISIBLE);
                    ((TextView) mView.findViewById(R.id.record_item_extra_message))
                            .setText(R.string.pre_pregnancy_label);
                } else if(mUserReading.isDeliveryReading()) {
                    sectionView.setVisibility(View.VISIBLE);
                    ((TextView) mView.findViewById(R.id.record_item_extra_message))
                            .setText(R.string.delivery_label);
                }

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

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM");
                ((TextView) mView.findViewById(R.id.record_item_taken_on))
                        .setText(dateFormat.format(mUserReading.TakenOn));
            }

            private void setImageControl() {
                ImageButton button = ((ImageButton) mView.findViewById(R.id.user_record_image_button));
                button.setImageBitmap(mUserReading.getImageAsBitmap(true));
            }

            private void setViewControl() {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mUserReading.isPrePregnancyReading()) {
                            bFirstReadingChanged = true;
                        }
                        gotoEditReading(mUserReading.Id);
                    }
                });
            }
        }
    }
}
