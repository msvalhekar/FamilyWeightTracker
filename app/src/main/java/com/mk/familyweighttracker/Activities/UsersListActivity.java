package com.mk.familyweighttracker.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;

import com.mk.familyweighttracker.Services.UserService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UsersListActivity extends AppCompatActivity {

    private static final int NEW_USER_ADDED_REQUEST = 1;
    private static final int USER_DATA_CHANGED_REQUEST = 2;

    private View mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_item_list);

        initToolbarControl();

        initAddNewUserControl();

        initUserListControl();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

            // Check which request we're responding to
        if (requestCode == NEW_USER_ADDED_REQUEST) {
            // update the list for new record
            setupRecyclerView((RecyclerView)mRecyclerView);
        }
        if (requestCode == USER_DATA_CHANGED_REQUEST) {
            // update the list for new record
            boolean dataChanged = data.getBooleanExtra(UserDetailActivity.ARG_IS_DATA_CHANGED, false);
            if(dataChanged) {
                setupRecyclerView((RecyclerView) mRecyclerView);
            }
        }
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
                Intent intent = new Intent(getApplicationContext(), AddNewUserActivity.class);
                startActivityForResult(intent, NEW_USER_ADDED_REQUEST);
            }
        });
    }

    private void initUserListControl() {
        mRecyclerView = findViewById(R.id.item_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List users = new UserService().getAll();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(users));
    }

    private class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<User> mUsers;

        public SimpleItemRecyclerViewAdapter(List<User> users) {
            mUsers = users;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.users_list_user_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final User user = mUsers.get(position);

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
            return mUsers.size();
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

                        setUserImageControl();
                        setNameControl();
                        setAgeControl();
                        setWeightControl();
                        setHeightControl();
                        setBmiControl();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                            mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }

            private void setBmiControl() {
                TextView bmiView = ((TextView) mView.findViewById(R.id.list_item_bmi));
                bmiView.setText(String.format("BMI: %.2f", mUser.getBmi()));

                TextView bmiCategoryView = ((TextView) mView.findViewById(R.id.list_item_bmi_category));
                BodyWeightCategory weightCategory = mUser.getWeightCategory();
                bmiCategoryView.setText(weightCategory.toString());
                if(weightCategory == BodyWeightCategory.UnderWeight) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255,255,153));
                } else
                if(weightCategory == BodyWeightCategory.Normal) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(153,255,153));
                } else
                if(weightCategory == BodyWeightCategory.OverWeight) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255,255,153));
                } else
                if(weightCategory == BodyWeightCategory.Obese) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255,102,102));
                }
            }

            private void setNameControl() {
                TextView nameView = ((TextView) mView.findViewById(R.id.list_item_name));
                nameView.setText(mUser.name);
            }

            private void setAgeControl() {
                String dateOfBirthValue = "";
                String ageValue = "";
                if(mUser.dateOfBirth != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                    dateOfBirthValue = dateFormat.format(mUser.dateOfBirth);
                    ageValue = getAge(mUser.dateOfBirth);
                }
                ((TextView) mView.findViewById(R.id.list_item_age)).setText(ageValue);
                ((TextView) mView.findViewById(R.id.list_item_dob)).setText(dateOfBirthValue);
            }

            private String getAge(Date dateOfBirth)
            {
                Calendar dob = Calendar.getInstance();
                dob.setTime(dateOfBirth);

                Calendar now = Calendar.getInstance();
                now.setTime(new java.util.Date());

                if(dob.after(now)) return "Invalid DoB";

                //calculate age .
                int ageYear = (now.get(Calendar.YEAR) - dob.get(Calendar.YEAR));
                int ageMonth = (now.get(Calendar.MONTH) - dob.get(Calendar.MONTH));
                int ageDays = (now.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH));

                if(ageYear == 0) {
                    if(ageMonth == 0) {
                        return String.format("%d days", ageDays);
                    } else {
                        if (ageDays < 0) {
                            GregorianCalendar gregCal = new GregorianCalendar(
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH)-1,
                                    now.get(Calendar.DAY_OF_MONTH));
                            ageDays += gregCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                            ageMonth --;
                        }
                        if(ageMonth == 0)
                            return String.format("%d days", ageDays);
                        else
                            return String.format("%d(%dd) months", ageMonth, ageDays);
                    }
                } else {
                    if(ageMonth == 0)
                        return String.format("%d yrs", ageYear);
                    else if(ageMonth < 0){
                        ageMonth += 12;
                        ageYear--;
                        if(ageYear == 0)
                            return String.format("%d months", ageMonth);
                    } else {
                        return String.format("%d(%dm) yrs", ageYear, ageMonth);
                    }
                }
                return "";
            }

            private void setHeightControl() {
                int currentHeight = 0;
                if(mUser.getReadings().size() > 0) {
                    UserReading lastReading = mUser.getReadings().get(mUser.getReadings().size() -1);
                    currentHeight = lastReading.Height;
                }
                String heightValue = "Ht: NA";
                if(mUser.getReadings().size() > 0)
                    heightValue = String.valueOf(currentHeight) + " " + mUser.heightUnit;
                ((TextView) mView.findViewById(R.id.list_item_height)).setText(heightValue);

                String heightDiffValue = "";
                if(mUser.getReadings().size() > 1) {
                    UserReading secondLastReading = mUser.getReadings().get(mUser.getReadings().size() -2);
                    int diff = currentHeight - secondLastReading.Height;
                    heightDiffValue = String.format("(%s%d)", (diff < 0) ? "-" : (diff == 0) ? "" : "+", diff);
                }
                ((TextView) mView.findViewById(R.id.list_item_height_diff)).setText(heightDiffValue);
            }

            private void setWeightControl() {
                double currentWeight = 0;
                if(mUser.getReadings().size() > 0) {
                    UserReading lastReading = mUser.getReadings().get(mUser.getReadings().size() -1);
                    currentWeight = lastReading.Weight;
                }
                String weightValue = "Wt: NA";
                if(mUser.getReadings().size() > 0)
                    weightValue = String.valueOf(currentWeight) + " " + mUser.weightUnit;
                ((TextView) mView.findViewById(R.id.list_item_weight)).setText(weightValue);

                String weightDiffValue = "";
                if(mUser.getReadings().size() > 1) {
                    UserReading secondLastReading = mUser.getReadings().get(mUser.getReadings().size() -2);
                    double diff = currentWeight - secondLastReading.Weight;
                    weightDiffValue = String.format("(%s%.2f)", (diff < 0) ? "-" : (diff == 0) ? "" : "+", diff);
                }
                ((TextView) mView.findViewById(R.id.list_item_weight_diff)).setText(weightDiffValue);
            }

            private void setUserImageControl() {
                ImageView imageView = (ImageView) mView.findViewById(R.id.list_item_image);
                if( mUser.imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(mUser.imageBytes, 0, mUser.imageBytes.length);
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.dummy_contact);
                }
            }
        }
    }
}
