package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddNewPregnantUserActivity extends AppCompatActivity {

    private static final int LOAD_IMAGE_REQUEST = 1;
    private static final int CROP_IMAGE_REQUEST = 2;

    AddNewUserTask mAddNewUserTask;
    NewUserViewModel mNewUser = new NewUserViewModel();
    Uri mPickedImageUri;

    private ImageButton mImageButton;
    private EditText mNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pregnant_user);

        initToolbarControl();

        mNameView = ((EditText) findViewById(R.id.add_user_name));

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR) - 18, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        mNewUser.IsMale = false;
        mNewUser.DateOfBirth = calendar.getTime();
        mNewUser.TrackingPeriod = TrackingPeriod.Week;
        mNewUser.EnableReminder = true;
        mNewUser.ReminderDay = 1;
        mNewUser.ReminderHour = 8;
        mNewUser.ReminderMinute = 0;

        initImageButtonControl();
        initDateOfBirthControl();
        initReminderControl();
        initDayOfReminderControl();
        initTimeOfReminderControl();
        initActionButtonControls();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == LOAD_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                mPickedImageUri = data.getData();
                allowToCropImageBeforeSelection(mPickedImageUri);
            } else {
                mPickedImageUri = null;
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == CROP_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");

                mImageButton.setImageBitmap(selectedBitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                try {
                    boolean success = selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    mNewUser.ImageBytes = stream.toByteArray();
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        mImageButton = ((ImageButton) findViewById(R.id.add_user_image_button));
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_REQUEST);
            }
        });
    }

    private void allowToCropImageBeforeSelection(Uri pickedImageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(pickedImageUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE_REQUEST);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
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
                mNewUser.ImageBytes = stream.toByteArray();
            }
            catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (Exception e) {
            e.printStackTrace();
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

    private void initReminderControl() {
        CheckBox reminderCheckbox = ((CheckBox) findViewById(R.id.add_user_set_reminder));

        reminderCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNewUser.EnableReminder = isChecked;
                int show = isChecked ? View.VISIBLE : View.GONE;
                findViewById(R.id.add_user_reminder_group).setVisibility(show);
            }
        });
        reminderCheckbox.setChecked(mNewUser.EnableReminder);
    }

    private void initDateOfBirthControl() {
        final Button dobView = ((Button) findViewById(R.id.add_user_dob));

        dobView.setText(getDateOfBirthMessage(), TextView.BufferType.SPANNABLE);

        dobView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(mNewUser.DateOfBirth);

                new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);

                                mNewUser.DateOfBirth = newDate.getTime();
                                dobView.setText(getDateOfBirthMessage(), TextView.BufferType.SPANNABLE);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    private Spanned getDateOfBirthMessage() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        String ageString = String.format("<small>(%s)</small>", Utility.calculateAge(mNewUser.DateOfBirth));
        return Html.fromHtml(String.format("%s    %s", dateFormatter.format(mNewUser.DateOfBirth), ageString));
    }

    private void initDayOfReminderControl() {
        final Button dayOfReminderView = ((Button) findViewById(R.id.add_user_reminder_day));
        final android.support.v7.widget.AppCompatSpinner daysSpinner = ((android.support.v7.widget.AppCompatSpinner)findViewById(R.id.add_user_reminder_day_spinner));

        final List<String> days = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(adapter);

        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNewUser.ReminderDay = position;
                dayOfReminderView.setText(days.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dayOfReminderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                daysSpinner.performClick();
            }
        });
    }

    private void initTimeOfReminderControl() {
        final Button timeOfReminderView = ((Button) findViewById(R.id.add_user_reminder_time));

        timeOfReminderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new TimePickerDialog(
                        v.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                setReminderTime(((Button) v), hourOfDay, minute);
                            }
                        },
                        mNewUser.ReminderHour,
                        mNewUser.ReminderMinute,
                        true)
                        .show();
            }
        });
        setReminderTime(timeOfReminderView, mNewUser.ReminderHour, mNewUser.ReminderMinute);
    }

    private void setReminderTime(Button view, int hourOfDay, int minute) {
        mNewUser.ReminderHour = hourOfDay;
        mNewUser.ReminderMinute = minute;
        view.setText(String.format("%02d:%02d", mNewUser.ReminderHour, mNewUser.ReminderMinute));
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
        public byte[] ImageBytes;
        public String Name;
        public boolean IsMale;
        public TrackingPeriod TrackingPeriod;
        public boolean EnableReminder;
        public int ReminderDay;
        public int ReminderHour;
        public int ReminderMinute;
        public Date DateOfBirth;

        public User mapToUser() {
            User user = new User(0);
            user.name = Name;
            user.imageBytes = ImageBytes;
            user.dateOfBirth = DateOfBirth;
            user.isMale = IsMale;
            user.trackingPeriod = TrackingPeriod;
            user.enableReminder = EnableReminder;
            user.reminderDay = ReminderDay;
            user.reminderHour = ReminderHour;
            user.reminderMinute = ReminderMinute;
            user.weightUnit = WeightUnit.kg;
            user.heightUnit = HeightUnit.cm;
            return user;
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
