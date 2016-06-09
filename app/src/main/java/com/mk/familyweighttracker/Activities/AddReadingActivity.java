package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddReadingActivity extends TrackerBaseActivity {

    private boolean bEditMode;
    private User mSelectedUser;
    private UserReading mUserReadingToProcess;
    private double mNewHeightValue;
    private double mNewWeightValue;
    private Long mNewSequenceValue;
    private View activityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_reading);

        initToolbarControl();
        activityView = findViewById(R.id.add_user_reading_layout);

        long userId = getIntent().getLongExtra(Constants.ARG_USER_ID, 0);
        mSelectedUser = new UserService().get(userId);

        long readingId = getIntent().getLongExtra(Constants.ARG_EDIT_READING_ID, 0);
        mUserReadingToProcess = mSelectedUser.getReadingById(readingId);

        if(mUserReadingToProcess != null) {
            bEditMode = true;
            setTitle("Edit Reading");
            findViewById(R.id.add_reading_delete_button).setVisibility(View.VISIBLE);
        } else {
            bEditMode = false;
            setTitle("Add Reading");
            findViewById(R.id.add_reading_delete_button).setVisibility(View.GONE);
            UserReading previousReading = mSelectedUser.getLatestReading();

            mUserReadingToProcess = new UserReading();
            mUserReadingToProcess.UserId = userId;
            mUserReadingToProcess.Sequence = previousReading.Sequence + 1;
            mUserReadingToProcess.TakenOn = new Date();
            mUserReadingToProcess.Weight = previousReading.Weight;
            mUserReadingToProcess.Height = previousReading.Height;
        }

        //initMeasuredOnDateControl();
        initWeekSequenceControl(mUserReadingToProcess.Sequence);
        initWeightSequenceControl(mUserReadingToProcess.Weight);
        initHeightSequenceControl(mUserReadingToProcess.Height);
        initActionButtonControls();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_add_reading);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initActionButtonControls() {
        findViewById(R.id.add_reading_save_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddReading();
                    }
                });

        findViewById(R.id.add_reading_delete_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(v.getContext())
                                .setTitle("Confirm delete")
                                .setMessage("Are you sure you want to delete this reading?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onDeleteReading();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create()
                                .show();
                    }
                });
    }

    private void initMeasuredOnDateControl() {
        final Button dobView = ((Button) findViewById(R.id.add_reading_taken_on_btn));
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        dobView.setText(dateFormatter.format(mUserReadingToProcess.TakenOn));

        dobView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(mUserReadingToProcess.TakenOn);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);

                                mUserReadingToProcess.TakenOn = newDate.getTime();
                                dobView.setText(dateFormatter.format(mUserReadingToProcess.TakenOn));
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

    private void initWeekSequenceControl(long lastReading) {
        final NumberPicker sequencePicker = getWeekSequenceControl(lastReading);

        final Button seqButton = ((Button) findViewById(R.id.add_reading_sequence_btn));
        seqButton.setText(String.valueOf(mUserReadingToProcess.Sequence));
        seqButton.setClickable(!bEditMode);
        if(bEditMode) return;

        seqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOfNextSequence = Arrays.asList(sequencePicker.getDisplayedValues()).indexOf(String.valueOf(mUserReadingToProcess.Sequence));
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

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage("Week Number")
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Sequence = mNewSequenceValue;
                                ((Button)findViewById(R.id.add_reading_sequence_btn))
                                        .setText(String.valueOf(mUserReadingToProcess.Sequence));
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

    private NumberPicker getWeekSequenceControl(long lastReading) {
        NumberPicker picker = new NumberPicker(activityView.getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        long startFrom = 1;
        long endAt = 40;
        final long incrementFactor = 1;

        final List<String> itemsToDisplay = new ArrayList<>();
        final List<String> pendingItems = new ArrayList<>();
        for (long seqValue = startFrom; seqValue <= endAt; seqValue += incrementFactor) {
            boolean found = false;
            for (UserReading reading : mSelectedUser.getReadings(true)) {
                if (reading.Sequence == seqValue) {
                    found = true;
                }
            }
            if(!found) {
                itemsToDisplay.add(String.valueOf(seqValue));
                if(seqValue < lastReading)
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

        findViewById(R.id.add_user_pending_readings_divider).setVisibility(View.GONE);
        findViewById(R.id.add_user_pending_readings_section).setVisibility(View.GONE);
        if(pendingItems.size() > 0) {
            findViewById(R.id.add_user_pending_readings_divider).setVisibility(View.VISIBLE);
            findViewById(R.id.add_user_pending_readings_section).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.add_reading_week_pending))
                    .setText(TextUtils.join(", ", pendingItems));
        }

        return picker;
    }

    private void initWeightSequenceControl(double lastReading) {
        final NumberPicker valuePicker = getWeightSequenceControl(lastReading);

        ((TextView) findViewById(R.id.add_reading_weight_unit_label)).setText(mSelectedUser.weightUnit.toString());

        final Button buttonView = ((Button) findViewById(R.id.add_reading_weight_btn));
        buttonView.setText(String.format("%.2f", mUserReadingToProcess.Weight));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOfNextValue = Arrays.asList(valuePicker.getDisplayedValues()).indexOf(String.format("%.2f", mUserReadingToProcess.Weight));
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

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage("Weight (" + mSelectedUser.weightUnit.toString() + ")")
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Weight = mNewWeightValue;
                                ((Button) findViewById(R.id.add_reading_weight_btn))
                                        .setText(String.format("%.2f", mUserReadingToProcess.Weight));
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

    private NumberPicker getWeightSequenceControl(double lastReading) {
        NumberPicker picker = new NumberPicker(activityView.getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        double distance = 5; //mSelectedUser.weightUnit == WeightUnit.lb ? 10 : 5;
        double startFrom = lastReading - distance;
        double endAt = lastReading + distance;
        final double incrementFactor = 0.05; // 50 grams

        final List<String> itemsToDisplay = new ArrayList<>();
        for (double seqValue = lastReading; seqValue >= startFrom; seqValue -= incrementFactor) {
            itemsToDisplay.add(0, String.format("%1$.2f", seqValue));
        }
        for (double seqValue = lastReading + incrementFactor; seqValue <= endAt; seqValue += incrementFactor) {
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

    private void initHeightSequenceControl(double lastReading) {
        final NumberPicker valuePicker = getHeightSequenceControl(lastReading);

        ((TextView) findViewById(R.id.add_reading_height_unit_label)).setText(mSelectedUser.heightUnit.toString());

        final Button buttonView = ((Button) findViewById(R.id.add_reading_height_btn));
        buttonView.setText(String.format("%.1f", mUserReadingToProcess.Height));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOfNextValue = Arrays.asList(valuePicker.getDisplayedValues()).indexOf(String.format("%.1f", mUserReadingToProcess.Height));
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

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage("Height (" + mSelectedUser.heightUnit.toString() + ")")
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Height = mNewHeightValue;
                                ((Button) findViewById(R.id.add_reading_height_btn))
                                        .setText(String.format("%.1f", mUserReadingToProcess.Height));
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

    private NumberPicker getHeightSequenceControl(double lastReading) {
        NumberPicker picker = new NumberPicker(activityView.getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        double startFrom = lastReading - 5;
        double endAt = lastReading + 5;
        final double incrementFactor = 0.5;

        final List<String> itemsToDisplay = new ArrayList<>();
        for (double seqValue = lastReading; seqValue >= startFrom; seqValue -= incrementFactor) {
            itemsToDisplay.add(0, String.format("%.1f", seqValue));
        }
        for (double seqValue = lastReading + incrementFactor; seqValue <= endAt; seqValue += incrementFactor) {
            itemsToDisplay.add(String.format("%.1f", seqValue));
        }

        String[] values = itemsToDisplay.toArray(new String[itemsToDisplay.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewHeightValue = Double.valueOf(itemsToDisplay.get(newVal));
            }
        });

        return picker;
    }

    private void onAddReading() {
        ((TrackerApplication) getApplication())
                .sendAnalyticsData("AddReading", "AddReadingActivity", "onAdded", String.valueOf(mUserReadingToProcess.Sequence), 1);

        new UserService().addReading(mUserReadingToProcess);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        String message = String.format("Weight reading for week %d %s successfully.",
                mUserReadingToProcess.Sequence,
                bEditMode ? "updated" : "added");
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void onDeleteReading() {
        ((TrackerApplication) getApplication())
                .sendAnalyticsData("AddReading", "AddReadingActivity", "onDeleted", String.valueOf(mUserReadingToProcess.Sequence), 1);

        new UserService().deleteReading(mUserReadingToProcess.Id);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        String message = String.format("Reading for week %d removed successfully.", mUserReadingToProcess.Sequence);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
