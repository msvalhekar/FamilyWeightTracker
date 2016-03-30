package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Date;

public class AddUserRecordActivity extends AppCompatActivity {

    long mSelectedUserId;
    AddUserReadingTask mAddUserReadingTask;

    EditText mSequenceView;
    EditText mWeightView;
    EditText mHeightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_record);

        mSelectedUserId = getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);

        initToolbarControl();

        mSequenceView = ((EditText) findViewById(R.id.add_reading_sequence));
        mWeightView = ((EditText) findViewById(R.id.add_reading_weight));
        mHeightView = ((EditText) findViewById(R.id.add_reading_height));

        initActionButtonControls();
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

        // Reset errors.
        mHeightView.setError(null);
        mWeightView.setError(null);
        mSequenceView.setError(null);

        // Store values at the time of the login attempt.
        int sequence = Integer.valueOf(mSequenceView.getText().toString());
        double weight = Double.valueOf(mWeightView.getText().toString());
        double height = Double.valueOf(mHeightView.getText().toString());
        Date takenOn = new Date(); //// TODO: 26-03-2016 take from user

        boolean cancel = false;
        View focusView = null;

        if (!isSequenceValid(sequence)) {
            mSequenceView.setError("Required");
            focusView = mSequenceView;
            cancel = true;
        }
        else if (!isWeightValid(weight)) {
            mWeightView.setError("Required");
            focusView = mWeightView;
            cancel = true;
        }
        else if (!isHeightValid(height)) {
            mHeightView.setError("Required");
            focusView = mHeightView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            // showProgress(true);
            mAddUserReadingTask = new AddUserReadingTask(sequence, weight, height, takenOn);
            mAddUserReadingTask.execute((Void) null);
        }
    }

    private boolean isHeightValid(double height) {
        return 0 < height && height < 200;
    }

    private boolean isWeightValid(double weight) {
        return 0 < weight && weight < 200;
    }

    private boolean isSequenceValid(int sequence) {
        return 0 < sequence && sequence < 100;
    }

    private class AddUserReadingTask extends AsyncTask<Void, Void, Boolean>
    {
        private final long mSequence;
        private final double mWeight;
        private final double mHeight;
        private final Date mTakenOn;

        AddUserReadingTask(int sequence, double weight, double height, Date takenOn) {
            mSequence = sequence;
            mWeight = weight;
            mHeight = height;
            mTakenOn = takenOn;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            UserReading reading = new UserReading(mSelectedUserId, mSequence, mWeight, mHeight, mTakenOn);
            new UserService().addReading(reading);
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
