package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

        Analytic.sendScreenView(Constants.Activities.AddFirstReadingActivity);

        initToolbarControl();
        activityView = findViewById(R.id.add_user_first_reading_layout);

        long userId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mSelectedUser = new UserService().get(userId);

        long readingId = getIntent().getLongExtra(Constants.ExtraArg.EDIT_READING_ID, 0);
        mUserReadingToProcess = mSelectedUser.getReadingById(readingId);

        bEditMode = true;
        if(mUserReadingToProcess == null) {
            bEditMode = false;

            mUserReadingToProcess = new UserReading();
            mUserReadingToProcess.UserId = userId;
            mUserReadingToProcess.Sequence = 0;
            mUserReadingToProcess.TakenOn = new Date();
            mUserReadingToProcess.Weight = UserReading.DEFAULT_BASE_WEIGHT;
            mUserReadingToProcess.Height = UserReading.DEFAULT_BASE_HEIGHT;
        }

        initLMPDateControl();
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

    private void initLMPDateControl() {
        final Button lmpDateView = ((Button) findViewById(R.id.add_first_reading_lmp_on_btn));
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        lmpDateView.setText(dateFormatter.format(mUserReadingToProcess.TakenOn));

        lmpDateView.setOnClickListener(new View.OnClickListener() {
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
                                lmpDateView.setText(dateFormatter.format(mUserReadingToProcess.TakenOn));
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

        String message = "Pre-pregnancy reading saved.";
        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.AddFirstReading,
                String.format(bEditMode ? Constants.AnalyticsActions.FirstReadingEdited : Constants.AnalyticsActions.FirstReadingAdded, mSelectedUser.name),
                null);
    }
}
