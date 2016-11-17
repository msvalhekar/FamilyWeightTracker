package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mk.familyweighttracker.Adapter.PregnantUserTabPagerAdapter;
import com.mk.familyweighttracker.Fragments.PregnantUserProfileFragment;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.OnNewReadingAdded;
import com.mk.familyweighttracker.Framework.SlidingTabLayout;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PregnantUserDetailActivity extends TrackerBaseActivity
        implements OnNewReadingAdded, PregnantUserProfileFragment.OnUserDeleted {

    private long mUserId;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        setTitle(getString(R.string.user_detail_activity_title));

        mUserId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, 0);
        if(getUser() == null) {
            Toast.makeText(PregnantUserDetailActivity.this, "User does not exist, try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.UserDetailsActivity,
                String.format(Constants.AnalyticsActions.UserDetailsLoaded, getUser().name),
                null);

        initToolbarControl();
        initInteractionControl();
        initDetailTabControl();

        saveUserImageIfRequired();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                break;

//            case R.id.user_detail_help:
//                View layout = getLayoutInflater().inflate(R.layout.help_popup_user_detail_readings, null);
//
//                PopupWindow window = new PopupWindow(this);
//                window.setContentView(layout);
//                window.setWidth(900);
//                window.setHeight(900);
//                window.setFocusable(true);
//                window.showAtLocation(layout, Gravity.NO_GRAVITY, 40, 20);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public long getUserId() {
        return getUser().getId();
    }

    public User getUser() {
        if(mUser == null)
            mUser = new UserService().get(mUserId);
        return mUser;
    }

    public void onUserDataChange() {
        mUser = null;
    }

    private void saveUserImageIfRequired() {
        User user = getUser();
        if(user == null || user.imageBytes == null)
            return;

        try {
            FileOutputStream outputStream = new FileOutputStream(user.getImagePath());
            outputStream.write(user.imageBytes, 0, user.imageBytes.length);
            outputStream.flush();
            outputStream.close();

            user.imageBytes = null;
            new UserService().add(user);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean mIsOriginator = false;
    @Override
    public boolean isOriginator() {
        return mIsOriginator;
    }

    @Override
    public void onNewReadingAdded() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments) {
            if(fragment instanceof OnNewReadingAdded &&
                ((OnNewReadingAdded) fragment).isOriginator() == false) {
                    ((OnNewReadingAdded) fragment).onNewReadingAdded();
            }
        }
    }

    @Override
    public void onUserDeleted() {
        mUser.removeReminder();
        new UserService().remove(mUserId);

        Analytic.setData(Constants.AnalyticsCategories.Activity,
                Constants.AnalyticsEvents.UserDelete,
                String.format(Constants.AnalyticsActions.UserDeleted, mUser.name),
                null);

        String message = String.format(getString(R.string.user_removed_message), mUser.name);
        Toast.makeText(TrackerApplication.getApp(), message, Toast.LENGTH_SHORT).show();

        finish();
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_user_detail);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDetailTabControl() {
        ViewPager viewPager = ((ViewPager) findViewById(R.id.user_detail_pager));
        viewPager.setAdapter(new PregnantUserTabPagerAdapter(getSupportFragmentManager()));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        slidingTabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the slidingTabLayout Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabLayout.setViewPager(viewPager);
    }

    private void initInteractionControl() {
        findViewById(R.id.app_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
                startActivity(shareIntent);
            }
        });

        findViewById(R.id.app_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedbackIntent = new Intent(v.getContext(), AppFeedbackActivity.class);
                feedbackIntent.putExtra(Constants.ExtraArg.USER_ID, mUserId);
                startActivity(feedbackIntent);
            }
        });

        findViewById(R.id.app_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAppStore();
            }
        });
    }

    private String getShareText() {
        return String.format("\'%s\'", getString(R.string.app_name)) +
                getString(R.string.app_share_line_2) +
                getString(R.string.app_share_line_3) +
                getString(R.string.app_share_line_4) +
                "\n\n" +
                String.format(Constants.PLAY_STORE_APP_SEARCH_URL, TrackerApplication.getApp().getPackageName());
    }

}
