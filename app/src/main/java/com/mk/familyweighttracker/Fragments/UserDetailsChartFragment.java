package com.mk.familyweighttracker.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;
import com.mk.familyweighttracker.Services.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsChartFragment extends Fragment implements OnChartValueSelectedListener, UserDetailsRecordsFragment.OnNewReadingAdded {

    private long mSelectedUserId;
    //private User mUser;
    private View mFragmentView;
    private LineChart mLineChart;
    private PregnancyService mPregnancyService = new PregnancyService();

    private List<WeekWeightGainRange> mWeightRangeList;

    public UserDetailsChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_chart, container, false);

        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);

        initChartControl();

        onNewReadingAdded();

        return mFragmentView;
    }

    private void setChartDescriptionControl(User user) {
        UserReading latestReading = user.getReadings(false).get(0);

        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_week_no))
                .setText(String.format("Week: %d", latestReading.Sequence));
        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_exp_this_week))
                .setText("Exp this Week");
        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_exp_40th_week))
                .setText("Exp on 40th Week");

        WeekWeightGainRange fortiethWeekRange = mWeightRangeList.get(mWeightRangeList.size()-1);
        WeekWeightGainRange latestWeekRange = null;
        for(WeekWeightGainRange range: mWeightRangeList) {
            if(range.WeekNumber == latestReading.Sequence) {
                latestWeekRange = range;
                break;
            }
        }

        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_actual))
                .setText(Html.fromHtml(String.format("Weight: %.2f kg", latestReading.Weight)));
        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_min_exp_this_week))
                .setText(Html.fromHtml(String.format("Min: %.2f", latestWeekRange.MinimumWeight)));
        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_min_exp_40th_week))
                .setText(Html.fromHtml(String.format("Min: %.2f", fortiethWeekRange.MinimumWeight)));

        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_max_exp_this_week))
                .setText(Html.fromHtml(String.format("Max: %.2f", latestWeekRange.MaximumWeight)));
        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_max_exp_40th_week))
                .setText(Html.fromHtml(String.format("Max: %.2f", fortiethWeekRange.MaximumWeight)));
    }

    private void initChartControl()
    {
        mLineChart = (LineChart) mFragmentView.findViewById(R.id.user_detail_chart_linechart);
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
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART_INSIDE);
        legend.setFormSize(15);
    }

    private void loadChartDataForPregnancy(User user)
    {
        mLineChart.resetTracking();

        List<String> xVals = getXaxisValues(mWeightRangeList.size());

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(getUserWeightValues(user.getReadings(true)));
        dataSets.add(getWeightRangeValues(mWeightRangeList, true));
        dataSets.add(getWeightRangeValues(mWeightRangeList, false));

        // make the first DataSet dashed
        //((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
        //((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
        //((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate();
    }

    private ILineDataSet getWeightRangeValues(List<WeekWeightGainRange> weightRangeList, boolean isMinimum) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        int index = 0;
        for (WeekWeightGainRange weightRange: weightRangeList) {
            double value = isMinimum ? weightRange.MinimumWeight : weightRange.MaximumWeight;
            values.add(new Entry((float) value, index++));
        }
        String label = isMinimum ? "exp min" : "exp max";
        LineDataSet lineDataSet = new LineDataSet(values, label);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawValues(false);

        int color = isMinimum ? Color.parseColor("#009999") : Color.parseColor("#FF66B2");
        lineDataSet.setColor(color);
        return lineDataSet;
    }

    private ILineDataSet getUserWeightValues(List<UserReading> userReadings) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        int index = 0;
        for (UserReading reading: userReadings) {
            values.add(new Entry((float) reading.Weight, index++));
        }

        LineDataSet lineDataSet = new LineDataSet(values, "actual wt");
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

    @Override
    public void onNewReadingAdded() {
        User user = new UserService().get(mSelectedUserId);
        if(user.getReadings(true).size() == 0) return;

        BodyWeightCategory weightCategory = user.getWeightCategory();
        mWeightRangeList = mPregnancyService.getWeightGainTableFor(user.getWeight(), weightCategory);

        loadChartDataForPregnancy(user);
        setChartDescriptionControl(user);
    }
}
