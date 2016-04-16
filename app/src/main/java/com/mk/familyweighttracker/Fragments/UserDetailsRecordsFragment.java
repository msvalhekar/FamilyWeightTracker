package com.mk.familyweighttracker.Fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;
import com.mk.familyweighttracker.Services.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsRecordsFragment extends Fragment implements OnNewReadingAdded {

    private long mSelectedUserId;
    private User mSelectedUser;
    private UserReading mNewUserReading = new UserReading();
    private long mNewSequenceValue;
    private double mNewWeightValue;
    private int mNewHeightValue;

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

        mWeekWeightGainRangeList = null;
        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);
        mSelectedUser = new UserService().get(mSelectedUserId);

        userReadingList.clear();
        for (UserReading reading: mSelectedUser.getReadings(false))
            userReadingList.add(reading);

        initAddUserReadingControl();

        initReadingListControl();

        return mFragmentView;
    }

    private void initAddUserReadingControl() {
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.button_user_add_record);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userReadingList.size() == 0) {
                    Toast.makeText(view.getContext(), "Add Pre-Pregnancy weight and height from Profile tab", Toast.LENGTH_LONG)
                        .show();
                    return;
                }
                acceptReading(getContext());
            }
        });
    }

    private void acceptReading(final Context context) {
        UserReading lastReading = userReadingList.get(0);

        mNewUserReading.UserId = mSelectedUserId;
        mNewUserReading.Sequence = lastReading.Sequence +1;
        mNewUserReading.TakenOn = new Date();
        mNewUserReading.Weight = lastReading.Weight;
        mNewUserReading.Height = lastReading.Height;

        final View dialogView = LayoutInflater.from(context).inflate(R.layout.add_user_reading, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd-MMM-yyyy");

        initMeasuredOnDateControl(dialogView);

        initWeekSequenceControl(dialogView, lastReading);

        initWeightSequenceControl(dialogView, lastReading);

        initHeightSequenceControl(dialogView, lastReading);

        final android.support.v7.app.AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(dateFormat.format(mNewUserReading.TakenOn))
                .setPositiveButton("ADD", null)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
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
                                new UserService().addReading(mNewUserReading);

                                onNewReadingAdded();

                                mIsOriginator = true;
                                ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
                                mIsOriginator = false;

                                alertDialog.dismiss();
                            }
                        });
            }
        });
        alertDialog.show();
    }

    private void initMeasuredOnDateControl(View view) {
        final Button dobView = ((Button) view.findViewById(R.id.add_reading_taken_on_btn));
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        dobView.setText(dateFormatter.format(mNewUserReading.TakenOn));

        dobView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(mNewUserReading.TakenOn);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);

                                mNewUserReading.TakenOn = newDate.getTime();
                                dobView.setText(dateFormatter.format(mNewUserReading.TakenOn));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(mSelectedUser.dateOfBirth.getTime());
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
    }

    private void initWeekSequenceControl(final View view, UserReading lastReading) {
        final NumberPicker sequencePicker = getWeekSequenceControl(view, lastReading);

        final Button seqButton = ((Button) view.findViewById(R.id.add_reading_sequence_btn));
        seqButton.setText(String.valueOf(mNewUserReading.Sequence));
        seqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOfNextSequence = Arrays.asList(sequencePicker.getDisplayedValues()).indexOf(String.valueOf(mNewUserReading.Sequence));
                sequencePicker.setValue(indexOfNextSequence);

                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                if (sequencePicker.getParent() != null)
                    ((ViewGroup) sequencePicker.getParent()).removeView(sequencePicker);
                layout.addView(sequencePicker);

                ViewGroup.LayoutParams params = sequencePicker.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                sequencePicker.setLayoutParams(params);
                layout.setHorizontalGravity(Gravity.CENTER);

                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage("Week Number")
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNewUserReading.Sequence = mNewSequenceValue;
                                ((Button) view.findViewById(R.id.add_reading_sequence_btn))
                                        .setText(String.valueOf(mNewUserReading.Sequence));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                alertDialog.show();

                TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
                if(messageView != null)
                    messageView.setGravity(Gravity.CENTER);
            }
        });
    }

    private NumberPicker getWeekSequenceControl(final View view, UserReading lastReading) {
        NumberPicker picker = new NumberPicker(view.getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        long startFrom = 1;
        long endAt = 40;
        final long incrementFactor = 1;

        final List<String> itemsToDisplay = new ArrayList<>();
        final List<String> pendingItems = new ArrayList<>();
        for (long seqValue = startFrom; seqValue <= endAt; seqValue += incrementFactor) {
            boolean found = false;
            for (UserReading reading : userReadingList) {
                if (reading.Sequence == seqValue) {
                    found = true;
                }
            }
            if(!found) {
                itemsToDisplay.add(String.valueOf(seqValue));
                if(seqValue < lastReading.Sequence)
                    pendingItems.add(String.valueOf(seqValue));
            }
        }

        String[] values = itemsToDisplay.toArray(new String[itemsToDisplay.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewSequenceValue = Long.valueOf(itemsToDisplay.get(newVal));
            }
        });

        String pendingLabel = pendingItems.size() > 0 ? "Pending week(s): " : "";
        ((TextView)view.findViewById(R.id.add_reading_week_pending))
                .setText(pendingLabel + TextUtils.join(", ", pendingItems));

        return picker;
    }

    private void initWeightSequenceControl(final View view, UserReading lastReading) {
        final NumberPicker valuePicker = getWeightSequenceControl(view, lastReading);

        ((TextView) view.findViewById(R.id.add_reading_weight_unit_label)).setText(mSelectedUser.weightUnit.toString());

        final Button buttonView = ((Button) view.findViewById(R.id.add_reading_weight_btn));
        buttonView.setText(String.valueOf(mNewUserReading.Weight));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOfNextValue = Arrays.asList(valuePicker.getDisplayedValues()).indexOf(String.format("%.2f", mNewUserReading.Weight));
                valuePicker.setValue(indexOfNextValue);

                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                if (valuePicker.getParent() != null)
                    ((ViewGroup) valuePicker.getParent()).removeView(valuePicker);
                layout.addView(valuePicker);

                ViewGroup.LayoutParams params = valuePicker.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                valuePicker.setLayoutParams(params);
                layout.setHorizontalGravity(Gravity.CENTER);

                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage("Weight")
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNewUserReading.Weight = mNewWeightValue;
                                ((Button) view.findViewById(R.id.add_reading_weight_btn))
                                        .setText(String.valueOf(mNewUserReading.Weight));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                alertDialog.show();

                TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (messageView != null)
                    messageView.setGravity(Gravity.CENTER);
            }
        });
    }

    private NumberPicker getWeightSequenceControl(final View view, UserReading lastReading) {
        NumberPicker picker = new NumberPicker(view.getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        double distance = 5; //mSelectedUser.weightUnit == WeightUnit.lb ? 10 : 5;
        double startFrom = lastReading.Weight - distance;
        double endAt = lastReading.Weight + distance;
        final double incrementFactor = 0.05; // 50 grams

        final List<String> itemsToDisplay = new ArrayList<>();
        for (double seqValue = lastReading.Weight; seqValue >= startFrom; seqValue -= incrementFactor) {
            itemsToDisplay.add(0, String.format("%1$.2f", seqValue));
        }
        for (double seqValue = lastReading.Weight + incrementFactor; seqValue <= endAt; seqValue += incrementFactor) {
            itemsToDisplay.add(String.format("%1$.2f", seqValue));
        }

        String[] values = itemsToDisplay.toArray(new String[itemsToDisplay.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewWeightValue = Double.valueOf(itemsToDisplay.get(newVal));
            }
        });

        return picker;
    }

    private void initHeightSequenceControl(final View view, UserReading lastReading) {
        final NumberPicker valuePicker = getHeightSequenceControl(view, lastReading);

        ((TextView) view.findViewById(R.id.add_reading_height_unit_label)).setText(mSelectedUser.heightUnit.toString());

        final Button buttonView = ((Button) view.findViewById(R.id.add_reading_height_btn));
        buttonView.setText(String.valueOf(mNewUserReading.Height));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOfNextValue = Arrays.asList(valuePicker.getDisplayedValues()).indexOf(String.valueOf(mNewUserReading.Height));
                valuePicker.setValue(indexOfNextValue);

                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                if (valuePicker.getParent() != null)
                    ((ViewGroup) valuePicker.getParent()).removeView(valuePicker);
                layout.addView(valuePicker);

                ViewGroup.LayoutParams params = valuePicker.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                valuePicker.setLayoutParams(params);
                layout.setHorizontalGravity(Gravity.CENTER);

                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage("Weight")
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNewUserReading.Height = mNewHeightValue;
                                ((Button) view.findViewById(R.id.add_reading_height_btn))
                                        .setText(String.valueOf(mNewUserReading.Height));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                alertDialog.show();

                TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (messageView != null)
                    messageView.setGravity(Gravity.CENTER);
            }
        });
    }

    private NumberPicker getHeightSequenceControl(final View view, UserReading lastReading) {
        NumberPicker picker = new NumberPicker(view.getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        double startFrom = lastReading.Height - 5;
        double endAt = lastReading.Height + 5;
        final double incrementFactor = 1;

        final List<String> itemsToDisplay = new ArrayList<>();
        for (double seqValue = lastReading.Height; seqValue >= startFrom; seqValue -= incrementFactor) {
            itemsToDisplay.add(0, String.format("%.0f", seqValue));
        }
        for (double seqValue = lastReading.Height + incrementFactor; seqValue <= endAt; seqValue += incrementFactor) {
            itemsToDisplay.add(String.format("%.0f", seqValue));
        }

        String[] values = itemsToDisplay.toArray(new String[itemsToDisplay.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewHeightValue = Integer.valueOf(itemsToDisplay.get(newVal));
            }
        });

        return picker;
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
    }

    private void initReadingListControl() {
        mRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter();

        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.user_record_list));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.notifyDataSetChanged();
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
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private static final int EMPTY_VIEW = 1;
        private static final int HELP_VIEW = 2;

        public SimpleItemRecyclerViewAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            if (userReadingList.size() == 0) {
                return EMPTY_VIEW;
            }
            if (position == 0) {
                return HELP_VIEW;
            }
            return super.getItemViewType(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HELP_VIEW) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_records_list_record_content_help, parent, false);
                return new HelpViewHolder(v);
            } else if (viewType == EMPTY_VIEW) {
                RelativeLayout layout = (RelativeLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
                ((TextView) layout.findViewById(R.id.empty_mesage_title)).setText("\nNo data found.");
                ((TextView) layout.findViewById(R.id.empty_mesage_description)).setText("\n\n\nUse below button to Add reading(s).");

                return new EmptyViewHolder(layout);
            }

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_records_list_record_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (position == 0) {
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(getContext())
                                .setMessage(Html.fromHtml(getLegendMessage()))
                                .setPositiveButton("Got it", null)
                                .create()
                                .show();
                    }
                });

                return;
            }

            final UserReading reading = userReadingList.get(position - 1);

            holder.setReading(reading);

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

        private String getLegendMessage() {
            StringBuilder builder = new StringBuilder();
            builder.append("<small>");
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
            builder.append("</small>");
            return builder.toString();
        }

        @Override
        public int getItemCount() {
            return userReadingList.size() + 1;
        }

        public class EmptyViewHolder extends ViewHolder {
            public EmptyViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class HelpViewHolder extends ViewHolder {
            public HelpViewHolder(View itemView) {
                super(itemView);
            }
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

                if(mUserReading.Sequence == 0) {
                    mView.findViewById(R.id.record_item_pregnancy_message).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.record_item_pregnancy_divider).setVisibility(View.VISIBLE);
                }

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
}
