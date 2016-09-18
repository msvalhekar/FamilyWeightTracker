package com.mk.familyweighttracker.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide_show);

        Analytic.sendScreenView(Constants.Activities.UserSlideshowActivity);

        mUserId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mUser = new UserService().get(mUserId);

        initSlidesPagerControl();

        startSlideShow();
    }

    private void initSlidesPagerControl() {
        mSlidesPager = ((ViewPager) findViewById(R.id.user_slideshow_pager));
        mPagerAdapter = new SlidesPagerAdapter(mUser.getReadings(true));
        mSlidesPager.setAdapter(mPagerAdapter);
    }

    private void startSlideShow() {
        final boolean[] isFirstTime = {true};
        //final int[] i = {0};
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int nextItemIndex = isFirstTime[0] ? mSlidesPager.getCurrentItem() : mSlidesPager.getCurrentItem()+1;
                        if(nextItemIndex == mPagerAdapter.getCount()) {
                            cancel();
                            finish();
                        }
                        mSlidesPager.setCurrentItem(nextItemIndex);
                        isFirstTime[0] = false;
                    }
                });
            }
        }, 100, 3000);
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

            ((ImageView) itemView.findViewById(R.id.user_slideshow_item_image))
                    .setImageBitmap(reading.getImageAsBitmap(false));

            ((TextView) itemView.findViewById(R.id.user_slideshow_week))
                    .setText(String.format(getString(R.string.slideshow_week_number_format), reading.Sequence));

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
}
