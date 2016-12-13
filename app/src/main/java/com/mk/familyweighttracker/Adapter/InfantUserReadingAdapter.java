package com.mk.familyweighttracker.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mk.familyweighttracker.Fragments.PregnantUserReadingsFragment;
import com.mk.familyweighttracker.Models.MonthGrowthRange;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by mvalhekar on 18-11-2016.
 */
public class InfantUserReadingAdapter extends RecyclerView.Adapter<InfantUserReadingAdapter.ViewHolder> {

    PregnantUserReadingsFragment mFragment;
    private User mUser;
    private List<UserReading> mReadingList;

    public InfantUserReadingAdapter(PregnantUserReadingsFragment fragment, User user) {
        mFragment = fragment;
        mUser = user;
        mReadingList = user.getReadings(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.infant_records_list_record_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserReading reading = mReadingList.get(position);
        holder.setReading(reading);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(reading.Id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReadingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public UserReading mUserReading;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }

        public void setReading(UserReading reading) {
            mUserReading = reading;

            setExtraControl();
            setPeriodControl();
            setActualWeightControl();
            setActualHeightControl();
            setActualHeadCircumControl();
            setExpectedValuesControl();
        }

        private void setExtraControl() {
            View sectionView = mView.findViewById(R.id.extra_section);
            sectionView.setVisibility(View.GONE);
        }

        private void setPeriodControl() {
            ((TextView) mView.findViewById(R.id.record_item_period_no))
                    .setText(String.format("%02d", mUserReading.Sequence));

            ((ImageButton) mView.findViewById(R.id.record_image_button))
                    .setImageBitmap(mUserReading.getImageAsBitmap(true));

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM-yy");
            ((TextView) mView.findViewById(R.id.record_item_taken_on))
                    .setText(dateFormat.format(mUserReading.TakenOn));
        }

        private void setActualWeightControl() {
            ((TextView) mView.findViewById(R.id.record_item_actual_weight))
                    .setText(String.format("%.2f", mUserReading.Weight));

            TextView weightDiffView = ((TextView) mView.findViewById(R.id.record_item_actual_weight_diff));
            weightDiffView.setText(String.format(" (%.2f)", 0.0));

            UserReading previousReading = mUser.findReadingBefore(mUserReading.Sequence);
            if (previousReading != null) {
                double diff = mUserReading.Weight - previousReading.Weight;
                String diffSign = "";
                int diffColor = 1;
                if (diff < 0) {
                    diffColor = Color.RED;
                } else if (diff > 0) {
                    diffSign = "+";
                    diffColor = Color.BLUE;
                }
                weightDiffView.setText(String.format(" (%s%.2f)", diffSign, diff));
                if (diff != 0)
                    weightDiffView.setTextColor(diffColor);
            }
        }

        private void setActualHeightControl() {
            ((TextView) mView.findViewById(R.id.record_item_actual_height))
                    .setText(String.format("%.2f", mUserReading.Height));

            TextView heightDiffView = ((TextView) mView.findViewById(R.id.record_item_actual_height_diff));
            heightDiffView.setText(String.format(" (%.2f)", 0.0));

            UserReading previousReading = mUser.findReadingBefore(mUserReading.Sequence);
            if (previousReading != null) {
                double diff = mUserReading.Height - previousReading.Height;
                String diffSign = "";
                int diffColor = 1;
                if (diff < 0) {
                    diffColor = Color.RED;
                } else if (diff > 0) {
                    diffSign = "+";
                    diffColor = Color.BLUE;
                }
                heightDiffView.setText(String.format(" (%s%.2f)", diffSign, diff));
                if (diff != 0)
                    heightDiffView.setTextColor(diffColor);
            }
        }

        private void setActualHeadCircumControl() {
            ((TextView) mView.findViewById(R.id.record_item_actual_hc))
                    .setText(String.format("%.2f", mUserReading.HeadCircumference));

            TextView hcDiffView = ((TextView) mView.findViewById(R.id.record_item_actual_hc_diff));
            hcDiffView.setText(String.format(" (%.2f)", 0.0));

            UserReading previousReading = mUser.findReadingBefore(mUserReading.Sequence);
            if (previousReading != null) {
                double diff = mUserReading.HeadCircumference - previousReading.HeadCircumference;
                String diffSign = "";
                int diffColor = 1;
                if (diff < 0) {
                    diffColor = Color.RED;
                } else if (diff > 0) {
                    diffSign = "+";
                    diffColor = Color.BLUE;
                }
                hcDiffView.setText(String.format(" (%s%.2f)", diffSign, diff));
                if (diff != 0)
                    hcDiffView.setTextColor(diffColor);
            }
        }

        private void setExpectedValuesControl() {
            MonthGrowthRange monthGrowthRange = mFragment.getMonthGrowthRangeFor(mUserReading.Sequence);

            if (monthGrowthRange == null) return;

            ((TextView) mView.findViewById(R.id.record_item_weight_exp_min))
                    .setText(String.format("%.2f", monthGrowthRange.WeightMinimum));

            ((TextView) mView.findViewById(R.id.record_item_weight_exp_max))
                    .setText(String.format("%.2f", monthGrowthRange.WeightMaximum));

            ((TextView) mView.findViewById(R.id.record_item_height_exp_min))
                    .setText(String.format("%.2f", monthGrowthRange.HeightMinimum));

            ((TextView) mView.findViewById(R.id.record_item_height_exp_max))
                    .setText(String.format("%.2f", monthGrowthRange.HeightMaximum));

            ((TextView) mView.findViewById(R.id.record_item_hc_exp_min))
                    .setText(String.format("%.2f", monthGrowthRange.HeadCircumMinimum));

            ((TextView) mView.findViewById(R.id.record_item_hc_exp_max))
                    .setText(String.format("%.2f", monthGrowthRange.HeadCircumMaximum));
        }
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(long readingId);
    }
}