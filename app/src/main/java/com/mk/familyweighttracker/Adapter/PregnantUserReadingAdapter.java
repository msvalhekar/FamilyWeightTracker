package com.mk.familyweighttracker.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by mvalhekar on 18-11-2016.
 */
public class PregnantUserReadingAdapter extends RecyclerView.Adapter<PregnantUserReadingAdapter.ViewHolder> {

    private Context mContext;
    private User mUser;
    private List<UserReading> mReadingList;
    List<WeekWeightGainRange> mWeekWeightGainRangeList;

    public PregnantUserReadingAdapter(Context context, User user) {
        mContext = context;
        mUser = user;
        mReadingList = user.getReadings(true);

        setWeightGainRange();
    }

    private void setWeightGainRange() {
        double baseWeight = mUser.getStartingWeight();
        if(baseWeight == 0) return;

        mWeekWeightGainRangeList = new PregnancyService()
                .getWeightGainTableFor(baseWeight, mUser.getWeightCategory(), mUser.weightUnit, mUser.haveTwins);
    }

    private WeekWeightGainRange getWeightGainRangeFor(long weekNumber) {
        if(mWeekWeightGainRangeList == null)
            return null;

        for (WeekWeightGainRange range: mWeekWeightGainRangeList) {
            if(range.WeekNumber == weekNumber) {
                return range;
            }
        }
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_records_list_record_content, parent, false);
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

            View sectionView = mView.findViewById(R.id.user_record_for_extra_section);
            sectionView.setVisibility(View.GONE);

            if (mUserReading.isPrePregnancyReading()) {
                sectionView.setVisibility(View.VISIBLE);
                ((TextView) mView.findViewById(R.id.record_item_extra_message))
                        .setText(R.string.pre_pregnancy_label);
            } else if (mUserReading.isDeliveryReading()) {
                sectionView.setVisibility(View.VISIBLE);
                ((TextView) mView.findViewById(R.id.record_item_extra_message))
                        .setText(R.string.delivery_label);
            }

            setPeriodControl();
            setActualWeightControl();
            setExpectedWeightControl();
            setImageControl();
        }

        private void setExpectedWeightControl() {
            WeekWeightGainRange weekWeightGainRange = getWeightGainRangeFor(mUserReading.Sequence);

            if (weekWeightGainRange == null) return;

            ((TextView) mView.findViewById(R.id.record_item_weight_exp_min))
                    .setText(String.format("%.2f", weekWeightGainRange.MinimumWeight));

            double diff = mUserReading.Weight - weekWeightGainRange.MinimumWeight;
            String diffSign = "";
            int diffColor = 1;
            if (diff < 0) {
                diffColor = Color.RED;
            } else if (diff > 0) {
                diffSign = "+";
                diffColor = Color.BLUE;
            }
            TextView weightDiffView = ((TextView) mView.findViewById(R.id.record_item_weight_exp_min_diff));
            weightDiffView.setText(String.format("(%s%.2f)", diffSign, diff));
            if (diff != 0)
                weightDiffView.setTextColor(diffColor);

            ((TextView) mView.findViewById(R.id.record_item_weight_exp_max))
                    .setText(String.format("%.2f", weekWeightGainRange.MaximumWeight));

            double maxExpWtdiff = mUserReading.Weight - weekWeightGainRange.MaximumWeight;
            String maxExpWtDiffSign = "";
            int maxExpWtDiffColor = 1;
            if (maxExpWtdiff < 0) {
                maxExpWtDiffColor = Color.BLUE;
            } else if (maxExpWtdiff > 0) {
                maxExpWtDiffSign = "+";
                maxExpWtDiffColor = Color.RED;
            }
            TextView maxExpWeightDiffView = ((TextView) mView.findViewById(R.id.record_item_weight_exp_max_diff));
            maxExpWeightDiffView.setText(String.format("(%s%.2f)", maxExpWtDiffSign, maxExpWtdiff));
            if (maxExpWtdiff != 0)
                maxExpWeightDiffView.setTextColor(maxExpWtDiffColor);
        }

        private void setActualWeightControl() {
            ((TextView) mView.findViewById(R.id.record_item_weight))
                    .setText(String.format("%.2f", mUserReading.Weight));

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
                TextView weightDiffView = ((TextView) mView.findViewById(R.id.record_item_weight_diff));
                weightDiffView.setText(String.format("(%s%.2f)", diffSign, diff));
                if (diff != 0)
                    weightDiffView.setTextColor(diffColor);
            }
        }

        private void setPeriodControl() {
            ((TextView) mView.findViewById(R.id.record_item_period_no))
                    .setText(String.format("%s %02d", mUser.trackingPeriod, mUserReading.Sequence));

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM");
            ((TextView) mView.findViewById(R.id.record_item_taken_on))
                    .setText(dateFormat.format(mUserReading.TakenOn));
        }

        private void setImageControl() {
            ImageButton button = ((ImageButton) mView.findViewById(R.id.user_record_image_button));
            button.setImageBitmap(mUserReading.getImageAsBitmap(true));
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