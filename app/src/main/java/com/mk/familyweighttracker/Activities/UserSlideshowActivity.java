package com.mk.familyweighttracker.Activities;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.PreferenceHelper;
import com.mk.familyweighttracker.Framework.StringHelper;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserSlideshowActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserSlideshowActivity extends TrackerBaseActivity {

    private long mUserId;
    private User mUser;
    ViewPager mSlidesPager;
    SlidesPagerAdapter mPagerAdapter;
    MediaPlayer mMediaPlayer;
    Timer mSlidesTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide_show);

        mUserId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mUser = new UserService().get(mUserId);

        Analytic.sendScreenView(mUser.getUserSlideshowActivity());

        initSlidesPagerControl();
    }

    @Override
    protected void onResume(){
        super.onResume();

        startBackgroundAudio();
        startSlideShow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            stopMediaAndFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initSlidesPagerControl() {
        mSlidesPager = ((ViewPager) findViewById(R.id.user_slideshow_pager));
        mPagerAdapter = new SlidesPagerAdapter(mUser.getReadings(true));
        mSlidesPager.setAdapter(mPagerAdapter);
        //mSlidesPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private void startBackgroundAudio() {
        String audioUriString = PreferenceHelper.getString(Constants.SharedPreference.SelectedBackgroundAudio, "");

        if(StringHelper.isNullOrEmpty(audioUriString)) {
            Toast.makeText(UserSlideshowActivity.this, R.string.background_audio_not_set_message, Toast.LENGTH_SHORT).show();
            return;
        }

        mMediaPlayer = MediaPlayer.create(this, Uri.parse(audioUriString));
        if(mMediaPlayer == null) {
            Toast.makeText(UserSlideshowActivity.this, R.string.background_audio_not_found_message, Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    private void startSlideShow() {
        final boolean[] isFirstTime = {true};
        mSlidesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int nextItemIndex = mSlidesPager.getCurrentItem();
                        if(!isFirstTime[0])
                            nextItemIndex++;

                        if (nextItemIndex == mPagerAdapter.getCount()) {
                            stopMediaAndFinish();
                        }
                        mSlidesPager.setCurrentItem(nextItemIndex, true);
                        isFirstTime[0] = false;
                    }
                });
            }
        }, 100, 3000);
    }

    private void stopMediaAndFinish() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        mSlidesTimer.cancel();
        finish();
    }

    public class SlidesPagerAdapter extends PagerAdapter {
        private List<UserReading> mUserReadings;

        public SlidesPagerAdapter(List<UserReading> userReadings) {
            mUserReadings = userReadings;
        }

        @Override
        public int getCount() {
            return mUserReadings.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = getLayoutInflater().inflate(R.layout.layout_slide_show_item, container, false);

            UserReading reading = mUserReadings.get(position);

            try {
                ((ImageView) itemView.findViewById(R.id.user_slideshow_item_image))
                        .setImageBitmap(reading.getImageAsBitmap(false, ImageUtility.SixHundred, ImageUtility.EightHundred));
            } catch (OutOfMemoryError e) { }

            String weekMsg = String.format(getString(R.string.slideshow_week_number_format), reading.Sequence);
            if(reading.isPrePregnancyReading())
                weekMsg = getString(R.string.pre_pregnancy_label);
            else if(reading.isDeliveryReading())
                weekMsg = getString(R.string.delivery_label);

            ((TextView) itemView.findViewById(R.id.user_slideshow_week))
                    .setText(weekMsg);

            TextView noteView = ((TextView) itemView.findViewById(R.id.user_slideshow_note));
            if(StringHelper.isNullOrEmpty(reading.Note)) {
                noteView.setText(R.string.slideshow_empty_note_message);
                noteView.setTextColor(Color.RED);
            } else {
                noteView.setText(reading.Note);
            }

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.5f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
