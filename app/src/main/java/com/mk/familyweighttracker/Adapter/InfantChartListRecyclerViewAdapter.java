package com.mk.familyweighttracker.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.mk.familyweighttracker.Models.MonthGrowthRange;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Repositories.InfantRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 09-12-2016.
 */
public class InfantChartListRecyclerViewAdapter extends RecyclerView.Adapter<InfantChartListRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private List<ChartDetail> mChartDetailList;
    private List<MonthGrowthRange> mMonthGrowthRangeList;
    private User mUser;

    public InfantChartListRecyclerViewAdapter(Context context, User user) {
        mContext = context;
        mUser = user;
        mMonthGrowthRangeList = new InfantRepository().createMonthGrowthRangeTableFor(user.isMale, user.weightUnit, user.heightUnit, user.headCircumUnit);
        setChartDetailList();
    }

    private void setChartDetailList() {
        mChartDetailList = new ArrayList<>();
        mChartDetailList.add(new ChartDetail("Weight"));
        mChartDetailList.add(new ChartDetail("Height"));
        mChartDetailList.add(new ChartDetail("Head Circumference"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.infant_user_chart_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChartDetail chartDetail = mChartDetailList.get(position);

        holder.setChartDetail(chartDetail);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(chartDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChartDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnChartValueSelectedListener {
        public final View mView;
        public ChartDetail mChartDetail;
        private LineChart mLineChart;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }

        public void setChartDetail(ChartDetail chartDetail) {
            mChartDetail = chartDetail;

            setTitleControl();
            setChartControl();
        }

        private void setTitleControl() {
            TextView textView = ((TextView) mView.findViewById(R.id.chart_title));
            textView.setText(mChartDetail.title);
        }

        private void setChartControl() {
            mLineChart = (LineChart) mView.findViewById(R.id.chart);
            mLineChart.setOnChartValueSelectedListener(this);

            mLineChart.setDrawGridBackground(false);
            mLineChart.setDescription("");
            mLineChart.setDrawBorders(false);

            mLineChart.getAxisLeft().setDrawAxisLine(true);
            mLineChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return String.format("%.2f", value);
                }
            });
            mLineChart.getAxisLeft().setDrawGridLines(true);
            mLineChart.getAxisRight().setDrawAxisLine(true);
            mLineChart.getAxisRight().setDrawGridLines(false);
            mLineChart.getXAxis().setDrawAxisLine(true);
            mLineChart.getXAxis().setDrawGridLines(true);

            // enable touch gestures
            mLineChart.setTouchEnabled(true);

            // enable scaling and dragging
            mLineChart.setDragEnabled(true);
            mLineChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mLineChart.setPinchZoom(false);

            Legend legend = mLineChart.getLegend();
            legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
            legend.setFormSize(15);

            loadChartData();
        }

        private void loadChartData() {
            mLineChart.resetTracking();

            List<String> xVals = getXaxisValues(mChartDetail.expMinList.size());

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(getActualValues());
            dataSets.add(getExpectedRangeValues(true));
            dataSets.add(getExpectedRangeValues(false));

            LineData data = new LineData(xVals, dataSets);
            mLineChart.setData(data);
            //mLineChart.invalidate();
            mLineChart.animateX(1000);
        }

        private ILineDataSet getExpectedRangeValues(boolean isMinimum) {
            ArrayList<Entry> values = new ArrayList<Entry>();

            int index = 0;
            List<Double> data = isMinimum ? mChartDetail.expMinList : mChartDetail.expMaxList;
            for (double value: data) {
                values.add(new Entry((float) value, index++));
            }
            String label = isMinimum ? "3rd percentile" : "97th percentile";
            //getString(R.string.chart_exp_min_legend) : getString(R.string.chart_exp_max_legend);
            LineDataSet lineDataSet = new LineDataSet(values, label);
            lineDataSet.setLineWidth(2f);
            lineDataSet.setDrawValues(false);

            int color = isMinimum ? Color.rgb(0, 153, 153) : Color.rgb(255, 102, 178);
            lineDataSet.setColor(color);
            return lineDataSet;
        }

        private ILineDataSet getActualValues() {
            ArrayList<Entry> values = new ArrayList<Entry>();

            for (Entry entry: mChartDetail.actualList) {
                    values.add(entry);
            }

            LineDataSet lineDataSet = new LineDataSet(values, "actualList");
            //LineDataSet lineDataSet = new LineDataSet(values, getString(R.string.chart_actual_weight_legend));
            lineDataSet.setLineWidth(5f);
            lineDataSet.setCircleRadius(2f);

            lineDataSet.setColor(Color.BLUE);
            return lineDataSet;
        }

        private List<String> getXaxisValues(int xValueCount) {
            ArrayList<String> dataVals = new ArrayList<String>();
            for (int i = 0; i < xValueCount; i++) {
                dataVals.add((i) + "");
            }
            return dataVals;
        }

        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        }

        @Override
        public void onNothingSelected() {
        }
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ChartDetail chartDetail);
    }

    public class ChartDetail {
        public String title;
        public List<Entry> actualList;
        public List<Double> expMinList;
        public List<Double> expMaxList;

        public ChartDetail(String title) {
            this.title = title;
            loadData();
        }

        private void loadData() {
            actualList = new ArrayList<>();
            expMinList = new ArrayList<>();
            expMaxList = new ArrayList<>();

            switch (title) {
                case "Weight":
                    for (MonthGrowthRange monthGrowthRange : mMonthGrowthRangeList) {
                        expMinList.add(monthGrowthRange.WeightMinimum);
                        expMaxList.add(monthGrowthRange.WeightMaximum);
                    }
                    for (UserReading userReading : mUser.getReadings(true)) {
                        if(userReading.Sequence < mMonthGrowthRangeList.size())
                            actualList.add(new Entry((float)userReading.Weight, (int)userReading.Sequence));
                    }
                    break;
                case "Height":
                    for (MonthGrowthRange monthGrowthRange : mMonthGrowthRangeList) {
                        expMinList.add(monthGrowthRange.HeightMinimum);
                        expMaxList.add(monthGrowthRange.HeightMaximum);
                    }
                    for (UserReading userReading : mUser.getReadings(true)) {
                        if(userReading.Sequence < mMonthGrowthRangeList.size())
                            actualList.add(new Entry((float)userReading.Height, (int)userReading.Sequence));
                    }
                    break;
                case "Head Circumference":
                    for (MonthGrowthRange monthGrowthRange : mMonthGrowthRangeList) {
                        expMinList.add(monthGrowthRange.HeadCircumMinimum);
                        expMaxList.add(monthGrowthRange.HeadCircumMaximum);
                    }
                    for (UserReading userReading : mUser.getReadings(true)) {
                        if(userReading.Sequence < mMonthGrowthRangeList.size())
                            actualList.add(new Entry((float)userReading.HeadCircumference, (int)userReading.Sequence));
                    }
                    break;
            }
        }
    }
}