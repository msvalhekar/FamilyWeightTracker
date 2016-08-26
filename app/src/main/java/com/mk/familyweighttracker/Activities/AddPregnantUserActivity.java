package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddPregnantUserActivity extends TrackerBaseActivity {

    AddUserAsyncTask mAddUserAsyncTask;
    private User mUser;
    private long mSelectedUserId;
    boolean mIsEditMode;

    private View mOkCancelActionsSectionView;
    private Button mCancelButton;
    private Button mSaveButton;
    private ImageButton mImageButton;
    private EditText mNameText;
    private Button mDobButton;
    private RadioGroup mGenderRadioGroup;
    private RadioButton mGenderMaleRdButton;
    private RadioButton mGenderFemaleRdButton;
    private android.support.v7.widget.SwitchCompat mEnableReminderCkBox;
    private Button mReminderMessageButton;
    private View mReminderDaySectionView;
    private android.support.v7.widget.AppCompatSpinner mReminderDaySpinner;
    private Button mReminderDayButton;
    private View mReminderTimeSectionView;
    private Button mReminderTimeButton;

    Uri mPickedImageUri;

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
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(calendar.get(Calendar.YEAR) - 18, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            mUser = new User(0);
            mUser.isMale = false;
            mUser.dateOfBirth = calendar.getTime();
            mUser.trackingPeriod = TrackingPeriod.Week;
            mUser.enableReminder = true;
            mUser.reminderDay = 1;
            mUser.reminderHour = 8;
            mUser.reminderMinute = 0;
        }

        initImageButtonControl();
        initNameControl();
        initDateOfBirthControl();
        initReminderControl();
        initActionButtonControls();

        setTitle(mIsEditMode ? R.string.title_activity_edit_user : R.string.title_activity_add_new_user);
    }

    private void findAllControls() {
        mOkCancelActionsSectionView = findViewById(R.id.add_user_ok_action_section);
        mSaveButton = ((Button) findViewById(R.id.add_user_save_button));

        mImageButton = ((ImageButton) findViewById(R.id.add_user_image_button));
        mNameText = ((EditText) findViewById(R.id.add_user_name_edit_text));
        mDobButton = ((Button) findViewById(R.id.add_user_dob_button));

        mGenderRadioGroup = ((RadioGroup) findViewById(R.id.add_user_gender_switch));
        mGenderMaleRdButton = ((RadioButton) findViewById(R.id.add_user_gender_male));
        mGenderFemaleRdButton = ((RadioButton) findViewById(R.id.add_user_gender_female));

        mEnableReminderCkBox = ((SwitchCompat) findViewById(R.id.add_user_remind_checkbox));
        mReminderMessageButton = ((Button) findViewById(R.id.add_user_remind_message_button));
        mReminderDaySectionView = findViewById(R.id.add_user_reminder_day_section);
        mReminderDaySpinner = ((AppCompatSpinner) findViewById(R.id.add_user_reminder_day_spinner));
        mReminderDayButton = ((Button) findViewById(R.id.add_user_reminder_day_button));
        mReminderTimeSectionView = findViewById(R.id.add_user_reminder_time_section);
        mReminderTimeButton = ((Button) findViewById(R.id.add_user_reminder_time_button));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == Constants.RequestCode.IMAGE_LOAD) {
            if (resultCode == RESULT_OK && data != null) {
                mPickedImageUri = data.getData();
                allowToCropImageBeforeSelection();
            } else {
                mPickedImageUri = null;
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == Constants.RequestCode.IMAGE_CROP) {
            if (resultCode == RESULT_OK && data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                if(extras != null) {
                    // get the cropped bitmap
                    Bitmap selectedBitmap = extras.getParcelable("data");

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    try {
                        boolean success = selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        mUser.imageBytes = stream.toByteArray();
                        mImageButton.setImageBitmap(BitmapFactory.decodeByteArray(mUser.imageBytes, 0, mUser.imageBytes.length));
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Log.e(Constants.LogTag.App, e.getMessage());
                    }
                }
            } else {
                saveUserImage(mPickedImageUri);
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
        if(mUser.imageBytes != null) {
            mImageButton.setImageBitmap(BitmapFactory.decodeByteArray(mUser.imageBytes, 0, mUser.imageBytes.length));
        }

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.RequestCode.IMAGE_LOAD);
            }
        });
    }

    private void allowToCropImageBeforeSelection() {

        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image cropper app", Toast.LENGTH_SHORT).show();
            // todo: still need to save selected image
            return;
        } else {
            intent.setData(mPickedImageUri);
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("crop", true);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName,  res.activityInfo.name));
                startActivityForResult(i, Constants.RequestCode.IMAGE_CROP);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(this, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Cropper App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        Constants.RequestCode.IMAGE_CROP);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mPickedImageUri != null) {
                            getContentResolver().delete(mPickedImageUri, null, null);
                            mPickedImageUri = null;
                        }
                    }
                });

                builder.create().show();
            }
        }
    }

    private void saveUserImage(Uri pickedImageUri) {
        try {
            String[] filepath = {MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImageUri, filepath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filepath[0]));
            cursor.close();

            Bitmap scaledBitmap = compressImage(imagePath);
            mImageButton.setImageBitmap(scaledBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                boolean success = scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                mUser.imageBytes = stream.toByteArray();
            }
            catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.e(Constants.LogTag.App, e.getMessage());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e(Constants.LogTag.App, e.getMessage());
        }
    }

    public Bitmap compressImage(String filePath) {
        //http://voidcanvas.com/whatsapp-like-image-compression-in-android/

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        // max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 150.0f;
        float maxWidth = 150.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        // width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        // setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        // this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        // check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Constants.LogTag.App, e.getMessage());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constants.LogTag.App, e.getMessage());
        }

        return scaledBitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void initNameControl() {
        mNameText.setText(mUser.name);
    }

    private void initDateOfBirthControl() {
        mDobButton.setText(mUser.getDateOfBirthStr(), TextView.BufferType.SPANNABLE);

        mDobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(mUser.dateOfBirth);

                new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);

                                mUser.dateOfBirth = newDate.getTime();
                                mDobButton.setText(mUser.getDateOfBirthStr(), TextView.BufferType.SPANNABLE);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    private void initReminderControl() {
        mEnableReminderCkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUser.enableReminder = isChecked;

                int show = View.GONE;
                String message = "No";
                if (isChecked) {
                    show = View.VISIBLE;
                    message = "Yes";
                }

                mReminderDaySectionView.setVisibility(show);
                mReminderTimeSectionView.setVisibility(show);
                mReminderMessageButton.setText(message);
            }
        });

        mEnableReminderCkBox.setChecked(mUser.enableReminder);
        initDayOfReminderControl();
        initTimeOfReminderControl();
    }

    private void initDayOfReminderControl() {
        mReminderDaySectionView.setVisibility(mUser.enableReminder ? View.VISIBLE : View.GONE);

        final List<String> days = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReminderDaySpinner.setAdapter(adapter);

        mReminderDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUser.reminderDay = position;
                mReminderDayButton.setText(days.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mReminderDaySpinner.setSelection(mUser.reminderDay);
        mReminderDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mReminderDaySpinner.performClick();
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

    private void initActionButtonControls() {
        mSaveButton.setText(mIsEditMode ? "Save" : "Add");
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewUser();
            }
        });
    }

    private void onAddNewUser() {
        if (mAddUserAsyncTask != null) return;

        HashMap<String, ArrayList<String>> errors = validateInput();

        if (errors.size() > 0) {
            for (String key : errors.keySet()) {
                if (key == "Name") {
                    mNameText.setError(((ArrayList<String>) errors.get(key)).get(0));
                    mNameText.requestFocus();
                    break;
                }
            }
        } else {
            mAddUserAsyncTask = new AddUserAsyncTask();
            mAddUserAsyncTask.execute(this);
        }
    }

    private void resetErrors() {
        // Reset errors.
        mNameText.setError(null);
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

        return errors;
    }

    public void SaveUser() {
        long userId = new UserService().add(mUser);

        mUser = new UserService().get(userId);
        mUser.resetReminder(getApplicationContext());

        if(mIsEditMode) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            Intent intent = new Intent(getApplicationContext(), com.mk.familyweighttracker.Activities.UserDetailActivity.class);
            intent.putExtra(Constants.ExtraArg.USER_ID, userId);
            startActivity(intent);
        }
        finish();

        String message = mIsEditMode ? "Profile updated" : "Welcome and Congratulations";
        int toastLength = mIsEditMode ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        Toast.makeText(getApplicationContext(), message, toastLength).show();

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.UserAdded,
                String.format(mIsEditMode ? Constants.AnalyticsActions.UserEdited : Constants.AnalyticsActions.UserAdded, mUser.name),
                null);
    }

    public class AddUserAsyncTask extends AsyncTask<AddPregnantUserActivity, Void, Boolean>
    {
        AddPregnantUserActivity activity;

        @Override
        protected Boolean doInBackground(AddPregnantUserActivity... params) {
            activity = params[0];
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddUserAsyncTask = null;

            if (success) {
                activity.SaveUser();
            } else {
            }
        }
    }

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);

            mOptions = options;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.crop_selector_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.crop_selector_title))
                        .setText(item.title);
                return convertView;
            }
            return null;
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }
}
