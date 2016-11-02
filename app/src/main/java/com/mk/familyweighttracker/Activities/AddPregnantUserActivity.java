package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddPregnantUserActivity extends TrackerBaseActivity {

    private User mUser;
    private long mSelectedUserId;
    boolean mIsEditMode;
    ImageUtility.CropDetail cropDetail = new ImageUtility.CropDetail(800, 800, 1, 1);

    private ImageButton mImageButton;
    private EditText mNameText;
    private Button mDeliveryDueDateButton;
    private View mReminderDaySectionView;
    private View mReminderTimeSectionView;
    private Button mReminderTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pregnant_user);

        Analytic.sendScreenView(Constants.Activities.AddPregnantUserActivity);

        initToolbarControl();

        findAllControls();

        mIsEditMode = true;
        mSelectedUserId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mUser = new UserService().get(mSelectedUserId);

        if(mUser == null) {
            mIsEditMode = false;

            mUser = new User(0);
            mUser.isMale = false;
            mUser.trackingPeriod = TrackingPeriod.Week;
            mUser.haveTwins = false;
            mUser.enableReminder = true;
            mUser.reminderDay = 1;
            mUser.reminderHour = 8;
            mUser.reminderMinute = 0;
            mUser.weightUnit = WeightUnit.kg;
            mUser.heightUnit = HeightUnit.cm;
        }

        initImageButtonControl();
        initNameControl();
        initDateOfBirthControl();
        initTwinsControl();
        initReminderControl();
        initDeliveryDueDateControl();
        initActionButtonControls();

        setTitle(mIsEditMode ? R.string.title_activity_edit_user : R.string.title_activity_add_new_user);
    }

    private void findAllControls() {
        mImageButton = ((ImageButton) findViewById(R.id.add_user_image_button));
        mNameText = ((EditText) findViewById(R.id.add_user_name_edit_text));
        mDeliveryDueDateButton = ((Button) findViewById(R.id.add_user_delivery_due_button));
        mReminderDaySectionView = findViewById(R.id.add_user_reminder_day_section);
        mReminderTimeSectionView = findViewById(R.id.add_user_reminder_time_section);
        mReminderTimeButton = ((Button) findViewById(R.id.add_user_reminder_time_button));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == Constants.RequestCode.USER_IMAGE_LOAD) {
            if (resultCode == RESULT_OK && data != null) {
                ImageUtility.cropImage(this, data.getData(), Constants.RequestCode.USER_IMAGE_CROP, cropDetail);
            } else {
                Toast.makeText(this, R.string.ImageNotPickedMessage, Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == Constants.RequestCode.USER_IMAGE_CROP) {
            if (resultCode == RESULT_OK && data != null) {
                // get the returned data, the cropped bitmap
                mImageButton.setImageBitmap(BitmapFactory.decodeFile(StorageUtility.getTempImagePath()));
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
        mImageButton.setImageBitmap(mUser.getImageAsBitmap(false));

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.RequestCode.USER_IMAGE_LOAD);
            }
        });
    }

    private void saveImage() {
        File source = new File(StorageUtility.getTempImagePath());
        source.renameTo(new File(mUser.getImagePath()));
    }

    private void initNameControl() {
        mNameText.setText(mUser.name);
    }

    private void initDateOfBirthControl() {
        final Button dobButton = ((Button) findViewById(R.id.add_user_dob_button));
        dobButton.setText(mUser.getDateOfBirthStr());

        dobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(mUser.dateOfBirth == null ? new Date() : mUser.dateOfBirth);

                new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);

                                mUser.dateOfBirth = newDate.getTime();
                                dobButton.setText(mUser.getDateOfBirthStr(), TextView.BufferType.SPANNABLE);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    private void initTwinsControl() {
        android.support.v7.widget.SwitchCompat haveTwinsCkBox = ((SwitchCompat) findViewById(R.id.add_user_have_twins_checkbox));
        haveTwinsCkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUser.haveTwins = isChecked;

                String message = getResources().getString(
                        isChecked ? R.string.add_user_have_twins_yes : R.string.add_user_have_twins_no);

                ((Button) findViewById(R.id.add_user_have_twins_button)).setText(message);
            }
        });

        haveTwinsCkBox.setChecked(mUser.haveTwins);
    }

    private void initReminderControl() {
        android.support.v7.widget.SwitchCompat enableReminderCkBox
                = ((SwitchCompat) findViewById(R.id.add_user_remind_checkbox));

        enableReminderCkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUser.enableReminder = isChecked;

                int show = View.GONE;
                String message = getResources().getString(R.string.add_user_have_twins_no);
                if (isChecked) {
                    show = View.VISIBLE;
                    message = getResources().getString(R.string.add_user_have_twins_yes);
                }

                mReminderDaySectionView.setVisibility(show);
                mReminderTimeSectionView.setVisibility(show);
                ((Button) findViewById(R.id.add_user_remind_message_button)).setText(message);
            }
        });

        enableReminderCkBox.setChecked(mUser.enableReminder);
        initDayOfReminderControl();
        initTimeOfReminderControl();
    }

    private void initDayOfReminderControl() {
        mReminderDaySectionView.setVisibility(mUser.enableReminder ? View.VISIBLE : View.GONE);

        final List<String> days = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final android.support.v7.widget.AppCompatSpinner reminderDaySpinner
                = ((AppCompatSpinner) findViewById(R.id.add_user_reminder_day_spinner));
        final Button reminderDayButton = ((Button) findViewById(R.id.add_user_reminder_day_button));

        reminderDaySpinner.setAdapter(adapter);

        reminderDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUser.reminderDay = position;
                reminderDayButton.setText(days.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        reminderDaySpinner.setSelection(mUser.reminderDay);
        reminderDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                reminderDaySpinner.performClick();
            }
        });
    }

    private void initTimeOfReminderControl() {
        mReminderTimeSectionView.setVisibility(mUser.enableReminder ? View.VISIBLE : View.GONE);

        mReminderTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new TimePickerDialog(
                        v.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                setReminderTime(hourOfDay, minute);
                            }
                        },
                        mUser.reminderHour,
                        mUser.reminderMinute,
                        true)
                        .show();
            }
        });
        setReminderTime(mUser.reminderHour, mUser.reminderMinute);
    }

    private void setReminderTime(int hourOfDay, int minute) {
        mUser.reminderHour = hourOfDay;
        mUser.reminderMinute = minute;
        mReminderTimeButton.setText(String.format("%02d:%02d", mUser.reminderHour, mUser.reminderMinute));
    }

    private void initDeliveryDueDateControl() {
        mDeliveryDueDateButton.setText(mUser.getDeliveryDueDateStr());

        mDeliveryDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(mUser.deliveryDueDate == null ? new Date() : mUser.deliveryDueDate);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);

                                mUser.deliveryDueDate = newDate.getTime();
                                mDeliveryDueDateButton.setText(mUser.getDeliveryDueDateStr());
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
    }

    private void initActionButtonControls() {
        Button saveButton = ((Button) findViewById(R.id.add_user_save_button));

        saveButton.setText(mIsEditMode ? "Save" : "Add");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewUser();
            }
        });
    }

    private void onAddNewUser() {
        HashMap<String, ArrayList<String>> errors = validateInput();

        if (errors.size() > 0) {
            for (String key : errors.keySet()) {
                if (key == "Name") {
                    mNameText.setError(errors.get(key).get(0));
                    mNameText.requestFocus();
                    break;
                } else if (key == "DDD") {
                    mDeliveryDueDateButton.setError(errors.get(key).get(0));
                    mDeliveryDueDateButton.requestFocus();
                    break;
                }
            }
            return;
        }
        // otherwise
        SaveUser();
    }

    private void resetErrors() {
        // Reset errors.
        mNameText.setError(null);
        mDeliveryDueDateButton.setError(null);
    }

    private HashMap<String, ArrayList<String>> validateInput() {
        resetErrors();

        HashMap<String, ArrayList<String>> errors = new HashMap();

        mUser.name = mNameText.getText().toString();
        ArrayList<String> nameErrors = new ArrayList<>();
        if (TextUtils.isEmpty(mUser.name)) {
            nameErrors.add("Required");
        }
        else if (mIsEditMode) {
            User userWithSameName = new UserService().get(mUser.name);
            if(userWithSameName != null && userWithSameName.getId() != mSelectedUserId)
                nameErrors.add("This name is already used, try different");
        } else if (new UserService().isAlreadyAdded(mUser.name)) {
            nameErrors.add("This name is already used, try different");
        }
        if(nameErrors.size() > 0)
            errors.put("Name", nameErrors);

        if(mUser.deliveryDueDate == null) {
            ArrayList<String> deliverDateErrors = new ArrayList<>();
            deliverDateErrors.add("Required");
            errors.put("DDD", deliverDateErrors);
        }
        return errors;
    }

    public void SaveUser() {
        long userId = new UserService().add(mUser);
        saveImage();

        mUser = new UserService().get(userId);
        mUser.resetReminder();

        if(mIsEditMode) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.UserDetailActivity.class);
            intent.putExtra(Constants.ExtraArg.USER_ID, userId);
            startActivity(intent);
        }
        finish();

        String message = mIsEditMode ? getString(R.string.user_details_updated_message) : getString(R.string.user_added_message);
        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.UserAdded,
                String.format(mIsEditMode ? Constants.AnalyticsActions.UserEdited : Constants.AnalyticsActions.UserAdded, mUser.name),
                null);

        //addDummyReadings();
    }

    private void addDummyReadings() {
        for (int i=0;i<41;i++) {
            UserReading reading = new UserReading();
            reading.TakenOn = new Date();
            reading.UserId = mUser.getId();
            reading.Height = 155;
            reading.Weight = 55 + (i*0.2);
            reading.Sequence = i;
            new UserService().addReading(reading);
        }
    }
}
