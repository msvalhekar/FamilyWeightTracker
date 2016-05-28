package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Date;

public class AddFirstReadingActivity extends TrackerBaseActivity {

    private boolean bEditMode;
    private User mSelectedUser;
    private UserReading mUserReadingToProcess;
    private WeightUnit mNewWeightUnit = WeightUnit.kg;
    private HeightUnit mNewHeightUnit = HeightUnit.cm;

    private View activityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_first_reading);

        initToolbarControl();
        activityView = findViewById(R.id.add_user_first_reading_layout);

        long userId = getIntent().getLongExtra(Constants.ARG_USER_ID, 0);
        mSelectedUser = new UserService().get(userId);

        long readingId = getIntent().getLongExtra(Constants.ARG_EDIT_READING_ID, 0);
        mUserReadingToProcess = mSelectedUser.getReadingById(readingId);

        bEditMode = true;
        if(mUserReadingToProcess == null) {
            bEditMode = false;

            mUserReadingToProcess = new UserReading();
            mUserReadingToProcess.UserId = userId;
            mUserReadingToProcess.Sequence = 0;
            mUserReadingToProcess.TakenOn = new Date();
            mUserReadingToProcess.Weight = 60.0;
            mUserReadingToProcess.Height = 160;
        }

        initWeightTextControl();
        initHeightTextControl();
        initWeightUnitControls();
        initHeightUnitControls();
        initActionButtonControls();

        setTitle(bEditMode ? "Edit Pre-pregnancy Reading" : "Add Pre-pregnancy Reading");
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

    private void initHeightTextControl() {
        final EditText heightView = ((EditText) activityView.findViewById(R.id.add_first_reading_height));
        heightView.setText(String.valueOf(mUserReadingToProcess.Height));
    }

    private void initWeightTextControl() {
        final EditText weightView = ((EditText) activityView.findViewById(R.id.add_first_reading_weight));
        weightView.setText(String.valueOf(mUserReadingToProcess.Weight));
    }

    private void initWeightUnitControls() {
        ((RadioGroup) activityView.findViewById(R.id.add_first_reading_weight_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        boolean isWeightUnitKg = (checkedId == R.id.add_first_reading_weight_unit_kg);
                        mNewWeightUnit = isWeightUnitKg ? WeightUnit.kg : WeightUnit.lb;
                    }
                });

        if(mSelectedUser.weightUnit == WeightUnit.lb)
            ((RadioButton) activityView.findViewById(R.id.add_first_reading_weight_unit_lb)).setChecked(true);
        else
            ((RadioButton) activityView.findViewById(R.id.add_first_reading_weight_unit_kg)).setChecked(true);
    }

    private void initHeightUnitControls() {
        ((RadioGroup) activityView.findViewById(R.id.add_first_reading_height_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        boolean isHeightUnitCm = (checkedId == R.id.add_first_reading_height_unit_cm);
                        mNewHeightUnit = isHeightUnitCm ? HeightUnit.cm : HeightUnit.inch;
                    }
                });
        if(mSelectedUser.heightUnit == HeightUnit.inch)
            ((RadioButton) activityView.findViewById(R.id.add_first_reading_height_unit_inch)).setChecked(true);
        else
            ((RadioButton) activityView.findViewById(R.id.add_first_reading_height_unit_cm)).setChecked(true);
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_add_first_reading);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initActionButtonControls() {
        findViewById(R.id.add_first_reading_save_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText weightView = ((EditText) activityView.findViewById(R.id.add_first_reading_weight));
                        String weightString = weightView.getText().toString();
                        if (TextUtils.isEmpty(weightString)) {
                            weightView.setError("Value required");
                            return;
                        }

                        EditText heightView = ((EditText) activityView.findViewById(R.id.add_first_reading_height));
                        String heightString = heightView.getText().toString();
                        if (TextUtils.isEmpty(heightString)) {
                            heightView.setError("Value required");
                            return;
                        }

                        mUserReadingToProcess.Weight = Double.valueOf(weightString);
                        mUserReadingToProcess.Height = Double.valueOf(heightString);

                        onAddReading();
                    }
                });
    }

    private void onAddReading() {
        new UserService().addReading(mUserReadingToProcess);
        new UserService().updateUnits(mSelectedUser.getId(), mNewWeightUnit, mNewHeightUnit);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        String message = String.format("Pre-pregnancy reading %s successfully.", bEditMode ? "updated" : "added");
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
