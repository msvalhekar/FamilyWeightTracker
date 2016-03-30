package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddNewUserActivity extends AppCompatActivity {

    private static final int LOAD_IMAGE_REQUEST = 1;
    private static final int CROP_IMAGE_REQUEST = 2;

    AddNewUserTask mAddNewUserTask;
    NewUserViewModel mNewUser = new NewUserViewModel();
    Uri mPickedImageUri;

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
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_REQUEST);
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
            if (trackingPeriods.get(index) == defaultValue) {
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
        public byte[] ImageBytes;
        public String Name;
        public TrackingPeriod TrackingPeriod;
        public int TrackingPeriodOnEvery;

        public User mapToUser() {
            return new User(0, mNewUser.Name, mNewUser.ImageBytes);
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
