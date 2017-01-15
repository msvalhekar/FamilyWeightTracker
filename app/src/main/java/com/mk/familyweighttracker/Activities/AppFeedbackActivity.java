package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.StringHelper;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.R;

import java.util.List;

public class AppFeedbackActivity extends TrackerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_feedback);

        Analytic.sendScreenView(Constants.Activities.AppFeedbackActivity);

        initToolbarControl();

        initNoteControl();
        initActionButtonControls();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_app_feedback);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initActionButtonControls() {
        findViewById(R.id.app_feedback_send_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText noteEditView = ((EditText) findViewById(R.id.app_feedback_note_edittext));
                        String feedbackMessage = noteEditView.getText().toString();
                        if(StringHelper.isNullOrEmpty(feedbackMessage)) {
                            Toast.makeText(AppFeedbackActivity.this, R.string.error_feedback_content_required_message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        finish();

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mvalhekar@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject_message));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                            Toast.makeText(AppFeedbackActivity.this, R.string.app_feedback_thanks_message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AppFeedbackActivity.this, R.string.app_feedback_error_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        findViewById(R.id.app_feedback_cancel_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void initNoteControl() {
        final TextView noteTextView = ((TextView) findViewById(R.id.app_feedback_note_message));
        noteTextView.setText(getString(R.string.app_feedback_message));

        final TextView noteLengthView = ((TextView) findViewById(R.id.app_feedback_note_length));

        final EditText noteEditView = ((EditText) findViewById(R.id.app_feedback_note_edittext));
        noteEditView.setHint(R.string.app_feedback_hint_message);
        noteEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteLengthView.setText(String.format("%d / %d", s.length(), 500));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
