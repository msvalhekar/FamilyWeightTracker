package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Framework.WeightParser;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.io.File;
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
    private WeightParser mNewWeightParser;
    private Long mNewSequenceValue;
    private View activityView;
    TextView mWeightDialogTitleView;
    ImageUtility.CropDetail cropDetail = new ImageUtility.CropDetail(600, 800, 3, 4);

    private ImageButton getImageButton() {
        return ((ImageButton)findViewById(R.id.add_user_reading_image_button));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_reading);

        Analytic.sendScreenView(Constants.Activities.AddReadingActivity);

        initToolbarControl();
        activityView = findViewById(R.id.add_user_reading_layout);

        long userId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mSelectedUser = new UserService().get(userId);

        long readingId = getIntent().getLongExtra(Constants.ExtraArg.EDIT_READING_ID, 0);
        mUserReadingToProcess = mSelectedUser.getReadingById(readingId);

        if(mUserReadingToProcess != null) {
            bEditMode = true;
            setTitle(getString(mUserReadingToProcess.isFirstReading() ? R.string.EditPrePregnancyReadingMessage : R.string.EditReadingMessage));
            findViewById(R.id.add_reading_delete_button).setVisibility(View.VISIBLE);
        } else {
            bEditMode = false;
            findViewById(R.id.add_reading_delete_button).setVisibility(View.GONE);
            UserReading previousReading = mSelectedUser.getLatestReading();

            mUserReadingToProcess = new UserReading();
            mUserReadingToProcess.UserId = userId;
            mUserReadingToProcess.TakenOn = new Date();
            mUserReadingToProcess.Sequence = 0;
            mUserReadingToProcess.Weight = UserReading.DEFAULT_BASE_WEIGHT;
            mUserReadingToProcess.Height = UserReading.DEFAULT_BASE_HEIGHT;
            if(previousReading != null) {
                mUserReadingToProcess.Sequence = getNextSequence(previousReading);
                mUserReadingToProcess.Weight = previousReading.Weight;
                mUserReadingToProcess.Height = previousReading.Height;
            }
            setTitle(getString(mUserReadingToProcess.isFirstReading() ? R.string.AddPrePregnancyReadingMessage : R.string.AddReadingMessage));
        }

        initImageButtonControl();
        initNoteControl();
        initMeasuredOnDateControl();
        initWeekSequenceControl(mUserReadingToProcess.Sequence);
        initWeightSequenceControl();
        initWeightUnitControl();
        initHeightUnitControl();
        initHeightSequenceControl(mUserReadingToProcess.Height);
        initActionButtonControls();
    }

    private long getNextSequence(UserReading previousReading) {
        long nextSequence = previousReading.Sequence + 1;
        if(nextSequence > 40) {
            List<UserReading> readings = mSelectedUser.getReadings(false);
            UserReading prevReading = null;
            UserReading currentReading = null;
            for (int i = 0; i < readings.size(); i++) {
                currentReading = readings.get(i);
                if(prevReading != null && (prevReading.Sequence - currentReading.Sequence) > 1) {
                    nextSequence = prevReading.Sequence -1;
                    break;
                }
                prevReading = currentReading;
            }
            if(prevReading.Sequence == currentReading.Sequence)
                nextSequence = currentReading.Sequence -1;
        }
        return nextSequence;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == Constants.RequestCode.READING_IMAGE_LOAD) {
            if (resultCode == RESULT_OK && data != null) {
                ImageUtility.cropImage(this, data.getData(), Constants.RequestCode.READING_IMAGE_CROP, cropDetail);
            } else {
                Toast.makeText(this, R.string.ImageNotPickedMessage, Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == Constants.RequestCode.READING_IMAGE_CROP) {
            if (resultCode == RESULT_OK && data != null) {
                // get the returned data,  the cropped bitmap
                getImageButton().setImageBitmap(BitmapFactory.decodeFile(StorageUtility.getTempImagePath()));
            }
        }
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
                        if (mUserReadingToProcess.isFirstReading()) {
                            new AlertDialog.Builder(v.getContext())
                                    .setTitle(getString(R.string.PrePregnancyReadingRemovalError))
                                    .setMessage(getString(R.string.PrePregnancyReadingRemovalErrorMessage))
                                    .setPositiveButton("Ok", null)
                                    .create()
                                    .show();
                            return;
                        }
                        new AlertDialog.Builder(v.getContext())
                                .setTitle(getString(R.string.ReadingRemovalConfirmation))
                                .setMessage(getString(R.string.ReadingRemovalConfirmationMessage))
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

    private void initImageButtonControl() {
        getImageButton().setImageBitmap(mUserReadingToProcess.getImageAsBitmap(false));

        getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.RequestCode.READING_IMAGE_LOAD);
            }
        });
    }

    private void initMeasuredOnDateControl() {
        ((TextView) findViewById(R.id.add_reading_taken_on_lable))
                .setText(getResources().getText(mUserReadingToProcess.isFirstReading()
                        ? R.string.add_user_first_reading_lmp_date_label
                        : R.string.add_user_reading_date_label));

        final Button takenOnView = ((Button) findViewById(R.id.add_reading_taken_on_btn));
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        takenOnView.setText(dateFormatter.format(mUserReadingToProcess.TakenOn));

        takenOnView.setOnClickListener(new View.OnClickListener() {
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
                                takenOnView.setText(dateFormatter.format(mUserReadingToProcess.TakenOn));
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

    private void initNoteControl() {
        final TextView noteLengthView = ((TextView) findViewById(R.id.add_reading_note_length));

        final EditText noteView = ((EditText) findViewById(R.id.add_reading_note_edittext));
        noteView.setText(mUserReadingToProcess.Note);
        noteView.setHint(Html.fromHtml(getString(R.string.ReadingNoteHintMessage)));
        noteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteLengthView.setText(String.format("%d / %d", s.length(), 200));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void initWeekSequenceControl(long lastReading) {

        final Button seqButton = ((Button) findViewById(R.id.add_reading_sequence_btn));
        seqButton.setText(String.valueOf(mUserReadingToProcess.Sequence));
        boolean nonEditable = bEditMode || mUserReadingToProcess.isFirstReading();
        seqButton.setClickable(!nonEditable);
        if(nonEditable) return;

        final NumberPicker sequencePicker = getWeekSequenceControl(lastReading);

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
                        .setMessage(getString(R.string.WeekNumberMessage))
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Sequence = mNewSequenceValue;
                                ((Button) findViewById(R.id.add_reading_sequence_btn))
                                        .setText(String.valueOf(mUserReadingToProcess.Sequence));
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

        findViewById(R.id.add_user_pending_readings_section).setVisibility(View.GONE);
        if(pendingItems.size() > 0) {
            findViewById(R.id.add_user_pending_readings_section).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.add_reading_week_pending))
                    .setText(String.format("%s %s", TextUtils.join(", ", pendingItems), getString(R.string.WeeksMessage)));
        }

        return picker;
    }

    private void initWeightUnitControl() {
        findViewById(R.id.add_reading_weight_unit_section).setVisibility(View.GONE);
        if(!mUserReadingToProcess.isFirstReading()) return;

        if(mSelectedUser.getReadingsCount() > 1) return; // dont allow to change unit if other readings are added

        findViewById(R.id.add_reading_weight_unit_section).setVisibility(View.VISIBLE);

        ((RadioGroup) activityView.findViewById(R.id.add_reading_weight_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        boolean isWeightUnitKg = (checkedId == R.id.add_reading_weight_unit_kg);
                        mSelectedUser.weightUnit = isWeightUnitKg ? WeightUnit.kg : WeightUnit.lb;
                        ((TextView) findViewById(R.id.add_reading_weight_unit_value)).setText(mSelectedUser.weightUnit.toString());
                    }
                });

        ((RadioButton) activityView
                .findViewById(mSelectedUser.weightUnit == WeightUnit.kg ? R.id.add_reading_weight_unit_kg : R.id.add_reading_weight_unit_lb))
                .setChecked(true);
    }

    private void initWeightSequenceControl() {
        ((TextView) findViewById(R.id.add_reading_weight_label))
                .setText(String.format("%s *", getString(R.string.add_user_reading_weight_label)));

        final NumberPicker twoPlacesAfterDecimalPicker = getWeightSequenceControl(WeightParser.TWO_PLACES_AFTER_DECIMAL);
        final NumberPicker onePlacesAfterDecimalPicker = getWeightSequenceControl(WeightParser.ONE_PLACE_AFTER_DECIMAL);
        final NumberPicker onePlaceB4DecimalPicker = getWeightSequenceControl(WeightParser.ONE_PLACE_BEFORE_DECIMAL);
        final NumberPicker twoPlacesB4DecimalPicker = getWeightSequenceControl(WeightParser.TWO_PLACES_BEFORE_DECIMAL);
        final NumberPicker threePlacesB4DecimalPicker = getWeightSequenceControl(WeightParser.THREE_PLACES_BEFORE_DECIMAL);

        ((TextView) findViewById(R.id.add_reading_weight_unit_value)).setText(mSelectedUser.weightUnit.toString());

        final Button buttonView = ((Button) findViewById(R.id.add_reading_weight_btn));
        buttonView.setText(String.format("%.2f", mUserReadingToProcess.Weight));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.HORIZONTAL);

                mNewWeightParser = new WeightParser(mUserReadingToProcess.Weight);

                setWeightControl(layout, threePlacesB4DecimalPicker, Integer.toString(mNewWeightParser.ThreePlacesBeforeDecimal));
                setWeightControl(layout, twoPlacesB4DecimalPicker, Integer.toString(mNewWeightParser.TwoPlacesBeforeDecimal));
                setWeightControl(layout, onePlaceB4DecimalPicker, Integer.toString(mNewWeightParser.OnePlaceBeforeDecimal));
                setWeightControl(layout, onePlacesAfterDecimalPicker, Integer.toString(mNewWeightParser.OnePlaceAfterDecimal));
                setWeightControl(layout, twoPlacesAfterDecimalPicker, Integer.toString(mNewWeightParser.TwoPlacesAfterDecimal));

                layout.setHorizontalGravity(Gravity.CENTER);

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage(String.format("Weight (%.2f %s)", mNewWeightParser.getWeight(), mSelectedUser.weightUnit.toString()))
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Weight = mNewWeightParser.getWeight();
                                ((Button) findViewById(R.id.add_reading_weight_btn))
                                        .setText(String.format("%.2f", mUserReadingToProcess.Weight));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                alertDialog.show();

                mWeightDialogTitleView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (mWeightDialogTitleView != null)
                    mWeightDialogTitleView.setGravity(Gravity.CENTER);
            }
        });
    }

    private void setWeightControl(LinearLayout layout, NumberPicker numberPicker, String value){
        int indexOfNextValue = Arrays.asList(numberPicker.getDisplayedValues()).indexOf(value);
        numberPicker.setValue(indexOfNextValue);
        if (numberPicker.getParent() != null)
            ((ViewGroup) numberPicker.getParent()).removeView(numberPicker);
        layout.addView(numberPicker);

        ViewGroup.LayoutParams params = numberPicker.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = 110;
        numberPicker.setLayoutParams(params);

        TextView textView = new TextView(activityView.getContext());
        textView.setText(" ");
        int id = numberPicker.getId();
        if(id == WeightParser.ONE_PLACE_BEFORE_DECIMAL) {
            textView.setText(Html.fromHtml("<big>.</big>"));
            numberPicker.measure(0, 0);
            textView.setHeight(numberPicker.getMeasuredHeight());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTypeface(null, Typeface.BOLD);
        }
        layout.addView(textView);
    }

    private NumberPicker getWeightSequenceControl(int id) {
        NumberPicker picker = new NumberPicker(activityView.getContext());
        picker.setId(id);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final List<String> itemsToDisplay = new ArrayList<>();
        for (int seqValue = 0; seqValue < 10; seqValue ++) {
            itemsToDisplay.add(seqValue, Integer.toString(seqValue));
        }

        String[] values = itemsToDisplay.toArray(new String[itemsToDisplay.size()]);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int newValue = Integer.valueOf(itemsToDisplay.get(newVal));
                mNewWeightParser.setValue(picker.getId(), newValue);
                mWeightDialogTitleView.setText(Html.fromHtml(
                        String.format("%s (<font color='blue'>%.2f</font> %s)",
                                getString(R.string.add_user_reading_weight_label),
                                mNewWeightParser.getWeight(),
                                mSelectedUser.weightUnit.toString())));
            }
        });

        return picker;
    }

    private void initHeightUnitControl() {
        findViewById(R.id.add_reading_height_unit_section).setVisibility(View.GONE);
        if(!mUserReadingToProcess.isFirstReading()) return;

        if(mSelectedUser.getReadingsCount() > 1) return; // dont allow to change unit if other readings are added

        findViewById(R.id.add_reading_height_unit_section).setVisibility(View.VISIBLE);

        ((RadioGroup) activityView.findViewById(R.id.add_reading_height_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        boolean isHeightUnitCm = (checkedId == R.id.add_reading_height_unit_cm);
                        mSelectedUser.heightUnit = isHeightUnitCm ? HeightUnit.cm : HeightUnit.inch;
                        ((TextView) findViewById(R.id.add_reading_height_unit_value)).setText(mSelectedUser.heightUnit.toString());
                    }
                });

        ((RadioButton) activityView
                .findViewById(mSelectedUser.heightUnit == HeightUnit.inch ? R.id.add_reading_height_unit_inch : R.id.add_reading_height_unit_cm))
                .setChecked(true);
    }

    private void initHeightSequenceControl(double lastReading) {
        String heightLabel = getString(R.string.add_user_reading_height_label);
        ((TextView) findViewById(R.id.add_reading_height_label))
                .setText(mUserReadingToProcess.isFirstReading() ? String.format("%s *", heightLabel) : heightLabel);

        final NumberPicker valuePicker = getHeightSequenceControl(lastReading);

        ((TextView) findViewById(R.id.add_reading_height_unit_value)).setText(mSelectedUser.heightUnit.toString());

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

        double startFrom = lastReading - 15;
        double endAt = lastReading + 15;
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
        if(mUserReadingToProcess.isFirstReading()) {
            new UserService().updateUnits(mSelectedUser.getId(), mSelectedUser.weightUnit, mSelectedUser.heightUnit);
        }

        updateNote();
        saveImage();
        new UserService().addReading(mUserReadingToProcess);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        String message = String.format(getString(R.string.WeekNReadingSavedMessage), mUserReadingToProcess.Sequence);
        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.AddReading,
                String.format(bEditMode ? Constants.AnalyticsActions.ReadingEdited : Constants.AnalyticsActions.ReadingAdded,
                        mSelectedUser.name,
                        mUserReadingToProcess.Sequence),
                null);
    }

    private void saveImage() {
        File source = new File(StorageUtility.getTempImagePath());
        source.renameTo(new File(mUserReadingToProcess.getImagePath()));
    }

    private void updateNote() {
        final EditText noteView = ((EditText) findViewById(R.id.add_reading_note_edittext));
        mUserReadingToProcess.Note = noteView.getText().toString();
    }

    private void onDeleteReading() {
        new UserService().deleteReading(mUserReadingToProcess.Id);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        String message = String.format(getString(R.string.WeekNReadingRemovedMessage), mUserReadingToProcess.Sequence);
        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.DeleteReading,
                String.format(Constants.AnalyticsActions.ReadingDeleted, mSelectedUser.name, mUserReadingToProcess.Sequence),
                null);
    }
}
