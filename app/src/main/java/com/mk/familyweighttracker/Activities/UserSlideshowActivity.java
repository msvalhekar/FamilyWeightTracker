package com.mk.familyweighttracker.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        Analytic.sendScreenView(Constants.Activities.UserSlideshowActivity);

        mUserId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        mUser = new UserService().get(mUserId);
        this.setTitle(String.format("%s - Slides", mUser.name));

        initToolbarControl();

        initSlidesPagerControl();

        initActionControls();
    }

    private void initActionControls() {
//        ((Button) findViewById(R.id.tracker_help_ok_button))
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                });
    }

    private void initSlidesPagerControl() {
        ViewPager slidesPager = ((ViewPager) findViewById(R.id.user_slideshow_pager));
        List<Bitmap> images = new ArrayList<>();
        for (UserReading reading: mUser.getReadings(true)) {
            images.add(reading.getImageAsBitmap(false));
        }
        SlidesPagerAdapter adapter = new SlidesPagerAdapter(images);
        slidesPager.setAdapter(adapter);
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_slide_show);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    public class SlidesPagerAdapter extends PagerAdapter {
        private List<Bitmap> mImages;

        public SlidesPagerAdapter(List<Bitmap> images) {
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = getLayoutInflater().inflate(R.layout.layout_slide_show_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.user_slideshow_item_image);
            imageView.setImageBitmap(mImages.get(position));

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
