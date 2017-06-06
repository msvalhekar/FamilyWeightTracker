package com.mk.familyweighttracker.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.ImageShapeType;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;

import java.util.List;

/**
 * Created by mvalhekar on 16-11-2016.
 */
public class UserListRecyclerViewAdapter extends RecyclerView.Adapter<UserListRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUserList;

    public UserListRecyclerViewAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
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
                onItemClickListener.onItemClick(user);
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

        public void setUser(User user) {
            mUser = user;

            mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {

                    int imageViewWidth = mView.findViewById(R.id.list_item_image).getWidth();
                    int viewWidth = mView.getWidth() - imageViewWidth;
                    int nameViewWidth = viewWidth / 3;
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
            TextView bmiCategoryView = ((TextView) mView.findViewById(R.id.list_item_bmi_category));

            if (Double.isNaN(mUser.getBmi())) {
                bmiView.setText("");
                bmiCategoryView.setText("");
                return;
            }

            bmiView.setText(String.format(mContext.getString(R.string.bmi_with_value_label), mUser.getBmiStr()));

            bmiCategoryView.setText("");
            if (mUser.isPregnant()) {
                BodyWeightCategory weightCategory = mUser.getWeightCategory();
                bmiCategoryView.setText(weightCategory.toString());

                if (weightCategory == BodyWeightCategory.UnderWeight) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255, 255, 153));
                } else if (weightCategory == BodyWeightCategory.Normal) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(153, 255, 153));
                } else if (weightCategory == BodyWeightCategory.OverWeight) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255, 255, 153));
                } else if (weightCategory == BodyWeightCategory.Obese) {
                    bmiCategoryView.setBackgroundColor(Color.rgb(255, 102, 102));
                }
            }
        }

        private void setNameControl() {
            TextView nameView = ((TextView) mView.findViewById(R.id.list_item_name));
            nameView.setText(mUser.name);
        }

        private void setAgeControl() {
            String ageValue = "";
            if (mUser.dateOfBirth != null) {
                ageValue = Utility.getAge(mUser.dateOfBirth, !mUser.isPregnant());
            }
            ((TextView) mView.findViewById(R.id.list_item_age)).setText(ageValue);
        }

        private void setHeightControl() {
            double currentHeight = 0;
            String heightValue = "";
            UserReading latestReading = mUser.getLatestReading();
            if (latestReading != null) {
                currentHeight = latestReading.Height;
                heightValue = String.format("%.1f", currentHeight) + " " + mUser.heightUnit;
            }
            ((TextView) mView.findViewById(R.id.list_item_height)).setText(heightValue);

            String heightDiffValue = "";
            TextView heightDiffView = (TextView) mView.findViewById(R.id.list_item_height_diff);
            if (latestReading != null) {
                List<UserReading> readings = mUser.getReadings(true);
                if (readings.size() > 0) {
                    UserReading previousReading = readings.get(0);
                    double diff = currentHeight - previousReading.Height;
                    heightDiffValue = String.format("(%s%.1f)", (diff > 0) ? "+" : "", diff);

                    if (diff < 0)
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
            if (latestReading != null) {
                currentWeight = latestReading.Weight;
                weightValue = String.format("%.2f", currentWeight) + " " + mUser.weightUnit;
            }
            ((TextView) mView.findViewById(R.id.list_item_weight)).setText(weightValue);

            String weightDiffValue = "";
            TextView weightDiffView = (TextView) mView.findViewById(R.id.list_item_weight_diff);
            if (latestReading != null) {
                List<UserReading> readings = mUser.getReadings(true);
                if (readings.size() > 0) {
                    UserReading previousReading = readings.get(0);
                    double diff = currentWeight - previousReading.Weight;
                    weightDiffValue = String.format("(%s%.2f)", (diff > 0) ? "+" : "", diff);

                    if (diff < 0)
                        weightDiffView.setTextColor(Color.RED);
                    else
                        weightDiffView.setTextColor(Color.BLUE);
                }
            }
            weightDiffView.setText(weightDiffValue);
        }

        private void setUserImageControl() {
            ImageView imageView = (ImageView) mView.findViewById(R.id.list_item_image);
            try {
                imageView.setImageBitmap(mUser.getImageAsBitmap(ImageShapeType.Oval, ImageUtility.TwoHundred));
            } catch (OutOfMemoryError e) { }
        }
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
}