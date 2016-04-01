package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddUserRecordActivity extends AppCompatActivity {

    long mSelectedUserId;
    User mSelectedUser;
    AddUserReadingTask mAddUserReadingTask;
    UserReading mUserReading = new UserReading();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_record);

        mSelectedUserId = getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);
        mSelectedUser = new UserService().get(mSelectedUserId);
        List<UserReading> readings = mSelectedUser.getReadings();

        UserReading lastReading = null;
        if(readings.size() > 0)
            lastReading = readings.get(readings.size() - 1);

        initToolbarControl();

        mUserReading.UserId = mSelectedUser.getId();
        mUserReading.TakenOn = Calendar.getInstance().getTime();

        initSequenceControl(lastReading);
        initWeightControl(lastReading);
        initHeightControl(lastReading);

        initActionButtonControls();
    }

    private void initWeightControl(UserReading lastReading) {
        NumberPicker picker = ((NumberPicker) findViewById(R.id.add_reading_weight_picker));

        double startWeight = 70;
        double endWeight = 90;
        final double incrementFactor = 0.05;

        if (lastReading != null) {
            startWeight = lastReading.Weight -10;
            endWeight = lastReading.Weight +10;
        }

        final List<String> items = new ArrayList<>();
        for (double i = startWeight; i<endWeight; i+=incrementFactor)
            items.add(String.format("%1$.2f", i));

        String[] values = items.toArray(new String[items.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mUserReading.Weight = Double.valueOf(items.get(newVal));
            }
        });
        picker.setValue(values.length / 2);
        mUserReading.Weight = Double.valueOf(items.get(values.length / 2));
    }

    private void initHeightControl(UserReading lastReading) {
        NumberPicker picker = ((NumberPicker) findViewById(R.id.add_reading_height_picker));

        int startHeight = 140;
        int endHeight = 170;
        int incrementFactor = 1;

        if (lastReading != null) {
            startHeight = lastReading.Height -5;
            endHeight = lastReading.Height +5;
        }

        final List<String> items = new ArrayList<>();
        for (int i = startHeight; i<endHeight; i+=incrementFactor)
            items.add(String.format("%3d", i));

        String[] values = items.toArray(new String[items.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mUserReading.Height = Integer.valueOf(items.get(newVal));
            }
        });
        picker.setValue(values.length / 2);
        mUserReading.Height = Integer.valueOf(items.get(values.length / 2));
    }

    private void initSequenceControl(UserReading lastReading) {
        android.support.v7.widget.AppCompatSpinner sequenceSpinner =
                ((android.support.v7.widget.AppCompatSpinner) findViewById(R.id.add_reading_sequence));

        final long nextSequenceNumber = (lastReading != null ? lastReading.Sequence : 0) +1;

        List<String> items = new ArrayList<>();
        String sequenceName = "Day";
        if( mSelectedUser.trackingPeriod == TrackingPeriod.Weekly) {
            sequenceName = "Week";
        } else if( mSelectedUser.trackingPeriod == TrackingPeriod.Monthly) {
            sequenceName = "Month";
        } else if( mSelectedUser.trackingPeriod == TrackingPeriod.Yearly) {
            sequenceName = "Year";
        }

        for (long i = nextSequenceNumber; i <= nextSequenceNumber +10; i++)
            items.add(sequenceName + " " + String.valueOf(i));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sequenceSpinner.setAdapter(adapter);

        sequenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUserReading.Sequence = nextSequenceNumber + position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mUserReading.Sequence = nextSequenceNumber;
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_add_user_reading);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    private void initActionButtonControls() {
        findViewById(R.id.add_reading_cancel_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        findViewById(R.id.add_reading_save_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddUserReading();
                    }
                });
    }

    private void onAddUserReading() {
        if (mAddUserReadingTask != null) {
            return;
        }

        mAddUserReadingTask = new AddUserReadingTask();
        mAddUserReadingTask.execute((Void) null);
    }

    private class AddUserReadingTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            new UserService().addReading(mUserReading);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddUserReadingTask = null;
            //showProgress(false);

            if (success) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
//                mHeightView.setError("error_incorrect_password");
//                mHeightView.requestFocus();
            }
        }
    }
}
