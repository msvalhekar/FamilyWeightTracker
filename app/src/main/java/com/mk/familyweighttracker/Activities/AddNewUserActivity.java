package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddNewUserActivity extends AppCompatActivity {

    AddNewUserTask mAddNewUserTask;
    NewUserViewModel mNewUser = new NewUserViewModel();

    private ImageButton mImageButton;
    private EditText mNameView;
    private EditText mDoBView;
    private android.support.v7.widget.AppCompatSpinner mTrackingPeriodSpinner;
    private android.support.v7.widget.AppCompatSpinner mTrackingPeriodOnSpinner;

    DatePickerDialog mDoBDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        initToolbarControl();

        mNameView = ((EditText) findViewById(R.id.add_user_name));

        initImageButtonControl();
        initDateOfBirthControl();
        initTrackingPeriodControl(TrackingPeriod.Weekly);
        initActionButtonControls();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // update the list for new record
            }
        }
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_add_new_user);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initImageButtonControl() {
        mImageButton = ((ImageButton) findViewById(R.id.add_user_image));
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                int RESULT_LOAD_IMAGE = 1;
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    private void initTrackingPeriodControl(TrackingPeriod defaultValue) {
        mTrackingPeriodSpinner = ((android.support.v7.widget.AppCompatSpinner) findViewById(R.id.add_user_tracking_period));

        List<TrackingPeriod> trackingPeriods = Arrays.asList(TrackingPeriod.values());

        ArrayAdapter<TrackingPeriod> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trackingPeriods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrackingPeriodSpinner.setAdapter(adapter);

        mTrackingPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNewUser.TrackingPeriod = (TrackingPeriod) parent.getSelectedItem();
                setTrackingPeriodOnControl(mNewUser.TrackingPeriod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        for (int index = 0; index < trackingPeriods.size(); index++) {
            if(trackingPeriods.get(index) == defaultValue) {
                mTrackingPeriodSpinner.setSelection(index);
                break;
            }
        }
    }

    private void setTrackingPeriodOnControl(TrackingPeriod selectedPeriod) {
        mTrackingPeriodOnSpinner = ((android.support.v7.widget.AppCompatSpinner) findViewById(R.id.add_user_tracking_period_on));

        List<Integer> items = new ArrayList<>();
        for(int i = 1; i <= selectedPeriod.getValue(); i++)
            items.add(i);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrackingPeriodOnSpinner.setAdapter(adapter);

        mTrackingPeriodOnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNewUser.TrackingPeriodOnEvery = Integer.valueOf(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initDateOfBirthControl() {
        mDoBView = ((EditText) findViewById(R.id.add_user_dob));
        mDoBView.setInputType(InputType.TYPE_NULL);

        mDoBView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;

                mDoBDatePickerDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();

        mDoBDatePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        mDoBView.setText(dateFormatter.format(newDate.getTime()));
                    }
                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initActionButtonControls() {
        findViewById(R.id.add_user_cancel_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        findViewById(R.id.add_user_save_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddNewUser();
                    }
                });
    }

    private void resetErrors() {
        // Reset errors.
        mNameView.setError(null);
    }

    private HashMap<String, ArrayList<String>> validateInput() {
        resetErrors();

        HashMap<String, ArrayList<String>> errors = new HashMap();

        mNewUser.Name = mNameView.getText().toString();
        ArrayList<String> nameErrors = new ArrayList<>();
        if (TextUtils.isEmpty(mNewUser.Name)) {
            nameErrors.add("Required");
        }
        else if (new UserService().isAlreadyAdded(mNewUser.Name)) {
            nameErrors.add("Already used, try different");
        }
        if(nameErrors.size() > 0)
            errors.put("Name", nameErrors);

        return errors;
    }

    private void onAddNewUser() {
        if (mAddNewUserTask != null) return;

        HashMap<String, ArrayList<String>> errors = validateInput();

        if (errors.size() > 0) {
            for (String key: errors.keySet()) {
                if(key == "Name") {
                    mNameView.setError(((ArrayList<String>) errors.get(key)).get(0));
                    mNameView.requestFocus();
                    break;
                }
            }
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            // showProgress(true);
            mAddNewUserTask = new AddNewUserTask();
            mAddNewUserTask.execute((Void) null);
        }
    }

    public class NewUserViewModel {
        public long Id;
        public String ImagePath;
        public String Name;
        public TrackingPeriod TrackingPeriod;
        public int TrackingPeriodOnEvery;

        public User mapToUser() {
            return new User(0, mNewUser.Name, mNewUser.ImagePath);
        }
    }

    public class AddNewUserTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            mNewUser.Id = new UserService().add(mNewUser.mapToUser());
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddNewUserTask = null;
            //showProgress(false);

            if (success) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("newUserId", mNewUser.Id);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
//                HeightView.setError("error_incorrect_password");
//                HeightView.requestFocus();
            }
        }
    }
}
