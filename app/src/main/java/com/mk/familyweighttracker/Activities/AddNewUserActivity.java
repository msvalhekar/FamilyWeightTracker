package com.mk.familyweighttracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Date;

public class AddNewUserActivity extends AppCompatActivity {

    AddNewUserTask mAddNewUserTask;

    ImageButton mImageButton;
    EditText mNameView;
    EditText mWeightView;
    EditText mHeightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_add_new_user);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mImageButton = ((ImageButton) findViewById(R.id.add_user_image));
        mNameView = ((EditText) findViewById(R.id.add_user_name));
        mWeightView = ((EditText) findViewById(R.id.add_user_weight));
        mHeightView = ((EditText) findViewById(R.id.add_user_height));

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

    private void onAddNewUser() {
        if (mAddNewUserTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mHeightView.setError(null);
        mWeightView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        double weight = Double.valueOf(mWeightView.getText().toString());
        double height = Double.valueOf(mHeightView.getText().toString());

        boolean cancel = false;
        View focusView = null;

        if (!isNameValid(name)) {
            mNameView.setError("Required");
            focusView = mNameView;
            cancel = true;
        }
        else if (!isNameUsed(name)) {
            mNameView.setError("Already used, try different");
            focusView = mNameView;
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
            mAddNewUserTask = new AddNewUserTask(name, weight, height, name+".jpg");
            mAddNewUserTask.execute((Void) null);
        }
    }

    private boolean isHeightValid(double height) {
        return 0 < height && height < 200;
    }

    private boolean isWeightValid(double weight) {
        return 0 < weight && weight < 100;
    }

    private boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name);
    }

    private boolean isNameUsed(String name) {
        Boolean alreadyAdded = new UserService().isAlreadyAdded(name);
        return !alreadyAdded;
    }

    public class AddNewUserTask extends AsyncTask<Void, Void, Boolean>
    {
        private User newUser;

        AddNewUserTask(String name, double weight, double height, String imagePath) {
            newUser = new User(0, name, weight, height, imagePath);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            newUser = new UserService().addUser(newUser);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddNewUserTask = null;
            //showProgress(false);

            if (success) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("newUserId", newUser.getId());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
//                mHeightView.setError("error_incorrect_password");
//                mHeightView.requestFocus();
            }
        }
    }
}
