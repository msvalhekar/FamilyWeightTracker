package com.mk.familyweighttracker.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.AndroidCharacter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Framework.Utility;
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
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UsersListActivity extends TrackerBaseActivity {

    private static final int NEW_USER_ADDED_REQUEST = 1;
    private static final int USER_DATA_CHANGED_REQUEST = 2;
    public static final String NEW_USER_ID_KEY = "newUserId";

    private RecyclerView mRecyclerView;
    private SimpleItemRecyclerViewAdapter mRecyclerViewAdapter;
    private List<User> mUserList = new ArrayList<>();
    private Bitmap mContactDefaultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_item_list);

        initToolbarControl();

        initAddNewUserControl();

        mUserList.clear();
        for (User user: new UserService().getAll())
            mUserList.add(user);

        initUserListControl();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

            // Check which request we're responding to
        if (requestCode == NEW_USER_ADDED_REQUEST) {
            // update the list for new user
            onRefreshList();

            long newUserId = data.getLongExtra(NEW_USER_ID_KEY, 0);
            if(newUserId != 0) {
                setReminderNotification(newUserId);
            }
        }
        if (requestCode == USER_DATA_CHANGED_REQUEST) {
            // update the list for new record
            boolean dataChanged = data.getBooleanExtra(UserDetailActivity.ARG_IS_DATA_CHANGED, false);
            if(dataChanged) {
                onRefreshList();
            }
        }
    }

    private void setReminderNotification(long userId) {
        User user = new UserService().get(userId);
        if(user == null || !user.enableReminder)
            return;
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_users_list);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initAddNewUserControl() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_users_list_add_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPregnantUserActivity.class);
                startActivityForResult(intent, NEW_USER_ADDED_REQUEST);
            }
        });
    }

    private void initUserListControl() {
        showEmptyListControl();

        mRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter();

        mRecyclerView = ((RecyclerView) findViewById(R.id.item_list));
        assert mRecyclerView != null;

        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private boolean showEmptyListControl() {
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        if(mUserList.size() == 0) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.empty_mesage_title)).setText("No data found.");
            ((TextView) findViewById(R.id.empty_mesage_description)).setText("Use below button to Add member(s).");
            return true;
        }
        return false;
    }

    private void onRefreshList() {
        List<User> latestUsers = new UserService().getAll();

        mUserList.clear();
        for (User user: latestUsers)
            mUserList.add(user);

        showEmptyListControl();

        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        public SimpleItemRecyclerViewAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.users_list_user_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final User user = mUserList.get(position);

            holder.setUser(user);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UserDetailActivity.class);
                    intent.putExtra(UserDetailActivity.ARG_USER_ID, user.getId());
                    startActivityForResult(intent, USER_DATA_CHANGED_REQUEST);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public User mUser;

            public ViewHolder(View view) {
                super(view);
                mView = view;
            }

            public void setUser(User user)
            {
                mUser = user;

                mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        int imageViewWidth = mView.findViewById(R.id.list_item_image).getWidth();
                        int viewWidth = mView.getWidth() - imageViewWidth;
                        int nameViewWidth = viewWidth * 1 / 3;
                        ((TextView) mView.findViewById(R.id.list_item_name)).setWidth(nameViewWidth);
                        ((TextView) mView.findViewById(R.id.list_item_weight)).setWidth(nameViewWidth);
                        ((TextView) mView.findViewById(R.id.list_item_height)).setWidth(nameViewWidth);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                            mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

                setUserImageControl();
                setNameControl();
                setAgeControl();
                setWeightControl();
                setHeightControl();
                setBmiControl();
            }

            private void setBmiControl() {
                TextView bmiView = ((TextView) mView.findViewById(R.id.list_item_bmi));
                if(Double.isNaN(mUser.getBmi())){
                    bmiView.setText("");
                    return;
                }

                bmiView.setText("BMI: " + mUser.getBmiStr());

                TextView bmiCategoryView = ((TextView) mView.findViewById(R.id.list_item_bmi_category));
                BodyWeightCategory weightCategory = mUser.getWeightCategory();
                bmiCategoryView.setText(weightCategory.toString());
                if(weightCategory == BodyWeightCategory.UnderWeight) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255,255,153));
                } else if(weightCategory == BodyWeightCategory.Normal) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(153,255,153));
                } else if(weightCategory == BodyWeightCategory.OverWeight) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255,255,153));
                } else if(weightCategory == BodyWeightCategory.Obese) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255,102,102));
                }
            }

            private void setNameControl() {
                TextView nameView = ((TextView) mView.findViewById(R.id.list_item_name));
                nameView.setText(mUser.name);
            }

            private void setAgeControl() {
                String ageValue = "";
                if(mUser.dateOfBirth != null) {
                    ageValue = Utility.calculateAge(mUser.dateOfBirth);
                }
                ((TextView) mView.findViewById(R.id.list_item_age)).setText(ageValue);
            }

            private void setHeightControl() {
                double currentHeight = 0;
                String heightValue = "";
                UserReading latestReading = mUser.getLatestReading();
                if(latestReading != null) {
                    currentHeight = latestReading.Height;
                    heightValue = String.format("%.1f", currentHeight) + " " + mUser.heightUnit;
                }
                ((TextView) mView.findViewById(R.id.list_item_height)).setText(heightValue);

                String heightDiffValue = "";
                TextView heightDiffView = (TextView) mView.findViewById(R.id.list_item_height_diff);
                if(latestReading != null){
                    UserReading previosReading = mUser.findReadingBefore(latestReading.Sequence);
                    if(previosReading != null) {
                        double diff = currentHeight - previosReading.Height;
                        heightDiffValue = String.format("(%s%.1f)", (diff > 0) ? "+" : "", diff);

                        if(diff < 0)
                            heightDiffView.setTextColor(Color.RED);
                        else
                            heightDiffView.setTextColor(Color.BLUE);
                    }
                }
                heightDiffView.setText(heightDiffValue);
            }

            private void setWeightControl() {
                double currentWeight = 0;
                String weightValue = "";
                UserReading latestReading = mUser.getLatestReading();
                if(latestReading != null) {
                    currentWeight = latestReading.Weight;
                    weightValue = String.format("%.2f", currentWeight) + " " + mUser.weightUnit;
                }
                ((TextView) mView.findViewById(R.id.list_item_weight)).setText(weightValue);

                String weightDiffValue = "";
                TextView weightDiffView = (TextView) mView.findViewById(R.id.list_item_weight_diff);
                if(latestReading != null){
                    UserReading previousReading = mUser.findReadingBefore(latestReading.Sequence);
                    if(previousReading != null) {
                        double diff = currentWeight - previousReading.Weight;
                        weightDiffValue = String.format("(%s%.2f)", (diff > 0) ? "+" : "", diff);


                        if(diff < 0)
                            weightDiffView.setTextColor(Color.RED);
                        else
                            weightDiffView.setTextColor(Color.BLUE);
                    }
                }
                weightDiffView.setText(weightDiffValue);
            }

            private void setUserImageControl() {
                ImageView imageView = (ImageView) mView.findViewById(R.id.list_item_image);
                if (mUser.imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(mUser.imageBytes, 0, mUser.imageBytes.length);
                    imageView.setImageBitmap(Utility.getCircularBitmap(bitmap));
                } else {
                    if (mContactDefaultBitmap == null) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contact_default);
                        mContactDefaultBitmap = Utility.getCircularBitmap(bitmap);
                    }
                    imageView.setImageBitmap(mContactDefaultBitmap);
                }
            }
        }
    }
}
