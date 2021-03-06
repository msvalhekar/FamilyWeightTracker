package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.mk.familyweighttracker.Enums.ImageShapeType;
import com.mk.familyweighttracker.Enums.PregnancyReadingType;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.StringHelper;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;
import com.mk.familyweighttracker.Views.NumericPickerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPregnancyReadingActivity extends TrackerBaseActivity {

    private boolean bEditMode;
    private User mUser;
    private UserReading mUserReadingToProcess;
    private Long mNewSequenceValue;
    private View activityView;
    ImageUtility.CropDetail cropDetail = new ImageUtility.CropDetail(600, 800, 3, 4);

    private ImageButton getImageButton() {
        return ((ImageButton)findViewById(R.id.add_user_reading_image_button));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_reading);

        initToolbarControl();
        activityView = findViewById(R.id.add_user_reading_layout);

        long userId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mUser = new UserService().get(userId);

        long readingId = getIntent().getLongExtra(Constants.ExtraArg.EDIT_READING_ID, 0);
        mUserReadingToProcess = mUser.getReadingById(readingId);

        if(mUserReadingToProcess != null) {
            bEditMode = true;
            findViewById(R.id.add_reading_delete_button).setVisibility(View.VISIBLE);
        } else {
            bEditMode = false;
            findViewById(R.id.add_reading_delete_button).setVisibility(View.GONE);

            long sequence = getSequenceFor();
            if(sequence < 0) {
                Toast.makeText(this, "Cannot add readings.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if(( mUser.isPregnant() && sequence >= User.MAXIMUM_PREGNANCY_READINGS_COUNT) ||
               (!mUser.isPregnant() && sequence >= User.MAXIMUM_INFANT_READINGS_COUNT)) {

                String errorMessage = String.format(getResources().getString(R.string.SuggestReadingBeyondSupportedDataMessage), sequence);
                if(mUser.isPregnant() && sequence >= User.MAXIMUM_PREGNANCY_READINGS_COUNT) {
                    errorMessage = String.format(getResources().getString(R.string.SuggestReadingBeyondDeliveryDueDateMessage), sequence);
                }

                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            mUserReadingToProcess = UserReading.createReading(mUser, sequence);
        }

        initImageButtonControl();
        initNoteControl();
        initMeasuredOnDateControl();
        initWeekSequenceControl();
        initWeightUnitControl();
        initWeightSequenceControl();
        initHeightUnitControl();
        initHeightSequenceControl();
        initHeadCircumUnitControl();
        initHeadCircumSequenceControl();
        initMissingReadingsControl();
        initActionButtonControls();
        setTitle();

        Analytic.sendScreenView(mUser.getAddReadingActivity());
    }

    private long getSequenceFor() {
        String readyTypeValue = getIntent().getStringExtra(Constants.ExtraArg.ADD_READING_TYPE);
        if(!StringHelper.isNullOrEmpty(readyTypeValue)) {
            PregnancyReadingType readingType = PregnancyReadingType.getType(readyTypeValue);
            if (readingType == PregnancyReadingType.PrePregnancy)
                return 0;
            if (readingType == PregnancyReadingType.Delivery)
                return 41;
        }
        return mUser.getNextAvailableSequence();
    }

    private void setTitle() {
        int messageId = bEditMode ? R.string.EditReadingMessage : R.string.AddReadingMessage;
        if(mUser.isPregnant()) {
            if (mUserReadingToProcess.isPrePregnancyReading()) {
                messageId = bEditMode ? R.string.EditPrePregnancyReadingMessage : R.string.AddPrePregnancyReadingMessage;
            } else if (mUserReadingToProcess.isDeliveryReading()) {
                messageId = bEditMode ? R.string.EditDeliveryReadingMessage : R.string.AddDeliveryReadingMessage;
            }
        }
        setTitle(getString(messageId));
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
                        if (mUser.isPregnant() && mUserReadingToProcess.isPrePregnancyReading()) {
                            new AlertDialog.Builder(v.getContext())
                                    .setTitle(getString(R.string.PrePregnancyReadingRemovalError))
                                    .setMessage(getString(R.string.PrePregnancyReadingRemovalErrorMessage))
                                    .setPositiveButton(R.string.ok_label, null)
                                    .create()
                                    .show();
                            return;
                        }
                        new AlertDialog.Builder(v.getContext())
                                .setTitle(getString(R.string.ReadingRemovalConfirmation))
                                .setMessage(getString(R.string.ReadingRemovalConfirmationMessage))
                                .setPositiveButton(R.string.yes_label, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onDeleteReading();
                                    }
                                })
                                .setNegativeButton(R.string.no_label, null)
                                .create()
                                .show();
                    }
                });
    }

    private void initImageButtonControl() {
        ((TextView) findViewById(R.id.add_reading_image_label))
                .setText(getResources().getText(mUser.isPregnant()
                        ? R.string.image_bump_label
                        : R.string.image_baby_label));

        try {
            getImageButton().setImageBitmap(
                    mUserReadingToProcess.getImageAsBitmap(ImageShapeType.Rectangle, ImageUtility.SeventyFive, ImageUtility.OneHundred));
        } catch (OutOfMemoryError e) { }

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
        ((TextView) findViewById(R.id.add_reading_taken_on_label))
                .setText(getResources().getText(mUser.isPregnant() && mUserReadingToProcess.isPrePregnancyReading()
                        ? R.string.add_user_first_reading_lmp_date_label
                        : R.string.reading_measured_date_label));

        final Button takenOnView = ((Button) findViewById(R.id.add_reading_taken_on_btn));
        takenOnView.setText(mUserReadingToProcess.getDisplayTakenOn());

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
                                takenOnView.setText(mUserReadingToProcess.getDisplayTakenOn());
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                if (mUser.dateOfBirth != null) {
                    datePickerDialog.getDatePicker().setMinDate(mUser.dateOfBirth.getTime());
                }
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
    }

    private void initNoteControl() {
        final TextView noteLengthView = ((TextView) findViewById(R.id.add_reading_note_length));

        final EditText noteView = ((EditText) findViewById(R.id.add_reading_note_edittext));
        noteView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Constants.Settings.USER_NOTE_LENGTH)});

        noteView.setText(mUserReadingToProcess.Note);
        noteView.setHint(Html.fromHtml(getString(R.string.ReadingNoteHintMessage)));
        noteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteLengthView.setText(count == 0 ? "" : String.format("%d / %d", count, Constants.Settings.USER_NOTE_LENGTH));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initWeekSequenceControl() {
        ((TextView) findViewById(R.id.add_reading_period_label))
                .setText(mUser.getSequenceLabel());

        final Button seqButton = ((Button) findViewById(R.id.add_reading_sequence_btn));
        seqButton.setText(String.valueOf(mUserReadingToProcess.Sequence));

        seqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bEditMode) {
                    Toast.makeText(
                            AddPregnancyReadingActivity.this,
                            mUser.getEditSequenceErrorMessage(),
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (mUser.isPregnant() && (mUserReadingToProcess.isPrePregnancyReading() || mUserReadingToProcess.isDeliveryReading())) {
                    return;
                }

                NumberPicker sequencePicker = getWeekSequenceControl();
                int indexOfNextSequence = Arrays.asList(sequencePicker.getDisplayedValues())
                        .indexOf(String.valueOf(mUserReadingToProcess.Sequence));
                if (indexOfNextSequence != -1)
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
                        .setMessage(mUser.getSequenceChangeLabel())
                        .setPositiveButton(R.string.set_label, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Sequence = mNewSequenceValue;
                                setTitle();
                                ((Button) findViewById(R.id.add_reading_sequence_btn))
                                        .setText(String.valueOf(mUserReadingToProcess.Sequence));
                            }
                        })
                        .setNegativeButton(R.string.cancel_label, null)
                        .create();
                alertDialog.show();

                TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (messageView != null)
                    messageView.setGravity(Gravity.CENTER);
            }
        });
    }

    private NumberPicker getWeekSequenceControl() {
        NumberPicker picker = new NumberPicker(activityView.getContext());
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        long startFrom = 1;
        long endAt = mUser.getMaximumReadingCount();
        if(mUser.isPregnant())
            endAt = endAt-2;

        final long incrementFactor = 1;

        List<UserReading> readings = mUser.getReadings(true);
        final List<String> itemsToDisplay = new ArrayList<>();
        for (long seqValue = startFrom; seqValue <= endAt; seqValue += incrementFactor) {
            boolean found = false;
            for (UserReading reading : readings) {
                if (reading.Sequence == seqValue) {
                    found = true;
                }
            }
            if(!found) {
                itemsToDisplay.add(String.valueOf(seqValue));
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

        return picker;
    }

    private void initMissingReadingsControl() {

        List<UserReading> readings = mUser.getReadings(true);
        final List<String> pendingItems = new ArrayList<>();
        for (long seqValue = 1; seqValue < mUserReadingToProcess.Sequence; seqValue++) {
            boolean found = false;
            for (UserReading reading : readings) {
                if (reading.Sequence == seqValue) {
                    found = true;
                }
            }
            if(!found)
                pendingItems.add(String.valueOf(seqValue));
        }

        findViewById(R.id.add_user_pending_readings_section).setVisibility(View.GONE);
        if(pendingItems.size() > 0) {
            findViewById(R.id.add_user_pending_readings_section).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.add_reading_week_pending))
                    .setText(String.format("%s %s",
                            TextUtils.join(", ", pendingItems),
                            mUser.getMissingPeriodLabel()));
        }
    }

    private void initWeightUnitControl() {
        findViewById(R.id.add_reading_weight_section_divider).setVisibility(View.GONE);
        findViewById(R.id.add_reading_weight_unit_section).setVisibility(View.GONE);

        if(!mUserReadingToProcess.isPrePregnancyReading()) return;

        if(mUser.getReadingsCount() > 1) return; // dont allow to change unit if other readings are added

        findViewById(R.id.add_reading_weight_section_divider).setVisibility(View.VISIBLE);
        findViewById(R.id.add_reading_weight_unit_section).setVisibility(View.VISIBLE);

        ((RadioGroup) activityView.findViewById(R.id.add_reading_weight_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        boolean isWeightUnitKg = (checkedId == R.id.add_reading_weight_unit_kg);
                        mUser.weightUnit = isWeightUnitKg ? WeightUnit.kg : WeightUnit.lb;
                        ((TextView) findViewById(R.id.add_reading_weight_unit_value)).setText(mUser.weightUnit.toString());
                    }
                });

        ((RadioButton) activityView
                .findViewById(mUser.weightUnit == WeightUnit.kg ? R.id.add_reading_weight_unit_kg : R.id.add_reading_weight_unit_lb))
                .setChecked(true);
    }

    private void initWeightSequenceControl() {
        ((TextView) findViewById(R.id.add_reading_weight_label))
                .setText(String.format("%s *", getString(R.string.weight_label)));

        ((TextView) findViewById(R.id.add_reading_weight_unit_value)).setText(mUser.weightUnit.toString());

        final Button buttonView = ((Button) findViewById(R.id.add_reading_weight_btn));
        buttonView.setText(String.format("%.2f", mUserReadingToProcess.Weight));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumericPickerView layout = new NumericPickerView(v.getContext());
                layout.setNumber(mUserReadingToProcess.Weight);

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage(getWeightDialogTitle(false, layout.getNumber()))
                        .setPositiveButton(R.string.set_label, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Weight = layout.getNumber();
                                ((Button) findViewById(R.id.add_reading_weight_btn))
                                        .setText(String.format("%.2f", mUserReadingToProcess.Weight));
                            }
                        })
                        .setNegativeButton(R.string.cancel_label, null)
                        .create();
                alertDialog.show();

                final TextView dialogTitleView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (dialogTitleView != null) {
                    dialogTitleView.setGravity(Gravity.CENTER);

                    layout.setOnValueChangedListener(new NumericPickerView.OnValueChangeListener() {
                        @Override
                        public void onValueChange() {
                            dialogTitleView.setText(getWeightDialogTitle(true, layout.getNumber()));
                        }
                    });
                }
            }
        });
    }

    private Spanned getWeightDialogTitle(Boolean isModified, double number) {
        String valueFormat = "%.2f";
        if(isModified)
            valueFormat = "<font color='blue'>%.2f</font>";

        String valueString = String.format(valueFormat, number);
        return Html.fromHtml(String.format("%s (%s %s)", getString(R.string.weight_label), valueString, mUser.weightUnit.toString()));
    }

    private void initHeightUnitControl() {
        findViewById(R.id.add_reading_height_section_divider).setVisibility(View.GONE);
        findViewById(R.id.add_reading_note_section_divider).setVisibility(View.GONE);
        findViewById(R.id.add_reading_height_unit_section).setVisibility(View.GONE);

        if(!mUserReadingToProcess.isPrePregnancyReading()) return;

        if(mUser.getReadingsCount() > 1) return; // dont allow to change unit if other readings are added

        findViewById(R.id.add_reading_height_section_divider).setVisibility(View.VISIBLE);
        findViewById(R.id.add_reading_note_section_divider).setVisibility(View.VISIBLE);
        findViewById(R.id.add_reading_height_unit_section).setVisibility(View.VISIBLE);

        ((RadioGroup) activityView.findViewById(R.id.add_reading_height_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        boolean isHeightUnitCm = (checkedId == R.id.add_reading_height_unit_cm);
                        mUser.heightUnit = isHeightUnitCm ? HeightUnit.cm : HeightUnit.inch;
                        ((TextView) findViewById(R.id.add_reading_height_unit_value)).setText(mUser.heightUnit.toString());
                    }
                });

        ((RadioButton) activityView
                .findViewById(mUser.heightUnit == HeightUnit.inch ? R.id.add_reading_height_unit_inch : R.id.add_reading_height_unit_cm))
                .setChecked(true);
    }

    private void initHeightSequenceControl() {
        ((TextView) findViewById(R.id.add_reading_height_label))
                .setText(String.format("%s *", getString(R.string.height_label)));

        ((TextView) findViewById(R.id.add_reading_height_unit_value)).setText(mUser.heightUnit.toString());

        final Button buttonView = ((Button) findViewById(R.id.add_reading_height_btn));
        buttonView.setText(String.format("%.2f", mUserReadingToProcess.Height));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumericPickerView layout = new NumericPickerView(v.getContext());
                layout.setNumber(mUserReadingToProcess.Height);

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage(getHeightDialogTitle(false, layout.getNumber()))
                        .setPositiveButton(getString(R.string.set_label), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.Height = layout.getNumber();
                                ((Button) findViewById(R.id.add_reading_height_btn))
                                        .setText(String.format("%.2f", mUserReadingToProcess.Height));
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel_label), null)
                        .create();
                alertDialog.show();

                final TextView dialogTitleView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (dialogTitleView != null) {
                    dialogTitleView.setGravity(Gravity.CENTER);

                    layout.setOnValueChangedListener(new NumericPickerView.OnValueChangeListener() {
                        @Override
                        public void onValueChange() {
                            dialogTitleView.setText(getHeightDialogTitle(true, layout.getNumber()));
                        }
                    });
                }
            }
        });
    }

    private Spanned getHeightDialogTitle(Boolean isModified, double number) {
        String valueFormat = "%.2f";
        if(isModified)
            valueFormat = "<font color='blue'>%.2f</font>";

        String valueString = String.format(valueFormat, number);
        return Html.fromHtml(String.format("%s (%s %s)", getString(R.string.height_label), valueString, mUser.heightUnit.toString()));
    }

    private void initHeadCircumUnitControl() {
        findViewById(R.id.add_reading_head_circum_section_divider).setVisibility(View.GONE);
        //findViewById(R.id.add_reading_note_section_divider).setVisibility(View.GONE);
        findViewById(R.id.add_reading_head_circum_unit_section).setVisibility(View.GONE);

        if(mUser.isPregnant()) return;

        if(!mUserReadingToProcess.isPrePregnancyReading()) return;

        if(mUser.getReadingsCount() > 1) return; // dont allow to change unit if other readings are added

        findViewById(R.id.add_reading_head_circum_section_divider).setVisibility(View.VISIBLE);
        //findViewById(R.id.add_reading_note_section_divider).setVisibility(View.VISIBLE);
        findViewById(R.id.add_reading_head_circum_unit_section).setVisibility(View.VISIBLE);

        ((RadioGroup) activityView.findViewById(R.id.add_reading_head_circum_unit_switch))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        boolean isHeadCircumUnitCm = (checkedId == R.id.add_reading_head_circum_unit_cm);
                        mUser.headCircumUnit = isHeadCircumUnitCm ? HeightUnit.cm : HeightUnit.inch;
                        ((TextView) findViewById(R.id.add_reading_head_circum_unit_value)).setText(mUser.headCircumUnit.toString());
                    }
                });

        ((RadioButton) activityView
                .findViewById(mUser.headCircumUnit == HeightUnit.inch ? R.id.add_reading_head_circum_unit_inch : R.id.add_reading_head_circum_unit_cm))
                .setChecked(true);
    }

    private void initHeadCircumSequenceControl() {
        findViewById(R.id.add_reading_head_circum_section).setVisibility(View.GONE);

        if(mUser.isPregnant()) return;

        findViewById(R.id.add_reading_head_circum_section).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.add_reading_head_circum_label))
                .setText(String.format("%s *", getString(R.string.head_circum_label)));

        ((TextView) findViewById(R.id.add_reading_head_circum_unit_value)).setText(mUser.headCircumUnit.toString());

        final Button buttonView = ((Button) findViewById(R.id.add_reading_head_circum_btn));
        buttonView.setText(String.format("%.2f", mUserReadingToProcess.HeadCircumference));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumericPickerView layout = new NumericPickerView(v.getContext());
                layout.setNumber(mUserReadingToProcess.HeadCircumference);

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setView(layout)
                        .setCancelable(false)
                        .setMessage(getHeadCircumDialogTitle(false, layout.getNumber()))
                        .setPositiveButton(getString(R.string.set_label), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUserReadingToProcess.HeadCircumference = layout.getNumber();
                                ((Button) findViewById(R.id.add_reading_head_circum_btn))
                                        .setText(String.format("%.2f", mUserReadingToProcess.HeadCircumference));
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel_label), null)
                        .create();
                alertDialog.show();

                final TextView dialogTitleView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (dialogTitleView != null) {
                    dialogTitleView.setGravity(Gravity.CENTER);

                    layout.setOnValueChangedListener(new NumericPickerView.OnValueChangeListener() {
                        @Override
                        public void onValueChange() {
                            dialogTitleView.setText(getHeadCircumDialogTitle(true, layout.getNumber()));
                        }
                    });
                }
            }
        });
    }

    private Spanned getHeadCircumDialogTitle(Boolean isModified, double number) {
        String valueFormat = "%.2f";
        if(isModified)
            valueFormat = "<font color='blue'>%.2f</font>";

        String valueString = String.format(valueFormat, number);
        return Html.fromHtml(String.format("%s (%s %s)", getString(R.string.head_circum_label), valueString, mUser.headCircumUnit.toString()));
    }

    private void onAddReading() {
        if(mUserReadingToProcess.isPrePregnancyReading()) {
            new UserService().updateUnits(mUser.getId(), mUser.weightUnit, mUser.heightUnit, mUser.headCircumUnit);
        }

        updateNote();
        saveImage();
        new UserService().addReading(mUserReadingToProcess);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        if(!mUser.isPregnant() || !mUserReadingToProcess.isDeliveryReading()) {
            String message = mUser.getReadingSavedMessage(mUserReadingToProcess.Sequence);
            Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();
        }

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                mUser.getAddReadingEvent(),
                String.format(bEditMode ? Constants.AnalyticsActions.ReadingEdited : Constants.AnalyticsActions.ReadingAdded,
                        mUser.name,
                        mUser.getTypeShortName(),
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

        String message = mUser.getReadingRemovedMessage(mUserReadingToProcess.Sequence);
        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                mUser.getDeleteReadingEvent(),
                String.format(Constants.AnalyticsActions.ReadingDeleted, mUser.name, mUser.getTypeShortName(), mUserReadingToProcess.Sequence),
                null);
    }
}
