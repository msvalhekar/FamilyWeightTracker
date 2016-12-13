package com.mk.familyweighttracker.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.PreferenceHelper;
import com.mk.familyweighttracker.Framework.StringHelper;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserMediaFragment extends PregnantUserBaseFragment {

    private View mFragmentView;

    public PregnantUserMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_media, container, false);

        initActionControls();

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                getUser().getDetailsMediaEvent(),
                String.format(Constants.AnalyticsActions.UserDetailsMedia, getUser().name, getUser().getTypeShortName()),
                null);

        return mFragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri audioUri = null;
        if(requestCode == Constants.RequestCode.MEDIA_BROWSE_AUDIO) {
            if(resultCode == Activity.RESULT_OK) {
                audioUri = data.getData();

                updateAudioTextControl(audioUri);
            }

            String audioUriStr = audioUri == null ? "" : audioUri.toString();
            PreferenceHelper.putString(Constants.SharedPreference.SelectedBackgroundAudio, audioUriStr);
        }
    }

    private void initActionControls() {
        mFragmentView.findViewById(R.id.media_slideshow_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getUser().getReadingsCount() == 0) {
                        Toast.makeText(v.getContext(), R.string.user_readings_not_found_message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.UserSlideshowActivity.class);
                    intent.putExtra(Constants.ExtraArg.USER_ID, getUserId());
                    startActivity(intent);
                }
            });

        String audioUriString = PreferenceHelper.getString(Constants.SharedPreference.SelectedBackgroundAudio, "");
        if(!StringHelper.isNullOrEmpty(audioUriString)) {
            updateAudioTextControl(Uri.parse(audioUriString));
        }

        mFragmentView.findViewById(R.id.media_select_bkgd_music_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent selectAudioIntent = new Intent();
                    selectAudioIntent.setType("audio/*");
                    selectAudioIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(selectAudioIntent, Constants.RequestCode.MEDIA_BROWSE_AUDIO);
                }
            });

        mFragmentView.findViewById(R.id.media_export_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Export", Toast.LENGTH_SHORT).show();
                }
            });

        mFragmentView.findViewById(R.id.media_share_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Share", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateAudioTextControl(Uri audioUri) {
        String audioName = getFilename(audioUri);
        ((TextView) mFragmentView.findViewById(R.id.media_bkgd_music_name)).setText(audioName);
    }

    private String getFilename(Uri uri) {
        String scheme = uri.getScheme();
        if (scheme.equals("content")) {
            String[] proj = { MediaStore.Video.Media.TITLE };
            try {
            Cursor cursor = this.getContext().getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                cursor.moveToFirst();
                String fileName = cursor.getString(columnIndex);
                return StringHelper.isNullOrEmpty(fileName) ? uri.getLastPathSegment() : fileName;
            }
            } catch (Exception e) {
            }
        }
        return uri.getLastPathSegment();
    }
}

