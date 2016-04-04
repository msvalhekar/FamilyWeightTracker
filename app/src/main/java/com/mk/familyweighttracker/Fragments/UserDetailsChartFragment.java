package com.mk.familyweighttracker.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
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

        loadChartDataForPregnancy();

        return mFragmentView;
    }

    private void initChartControl()
    {
        mLineChart = (LineChart) mFragmentView.findViewById(R.id.user_detail_chart_linechart);
        mLineChart.setOnChartValueSelectedListener(this);

        mLineChart.setDrawGridBackground(false);
        //mLineChart.setDescription("MK");
        mLineChart.setDrawBorders(false);

        mLineChart.getAxisLeft().setDrawAxisLine(false);
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getAxisRight().setDrawAxisLine(false);
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
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
    }

    private void loadChartDataForPregnancy()
    {
        User user = new UserService().get(mSelectedUserId);

        if(user.getReadings(true).size() == 0) return;

        mLineChart.resetTracking();

        BodyWeightCategory weightCategory = user.getWeightCategory();
        List<WeekWeightGainRange> weightRangeList = mPregnancyService.getWeightGainTableFor(user.getWeight(), weightCategory);

        List<String> xVals = getXaxisValues(weightRangeList.size());

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(getUserWeightValues(user.getReadings(true)));
        dataSets.add(getWeightRangeValues(weightRangeList, true));
        dataSets.add(getWeightRangeValues(weightRangeList, false));

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
        String label = isMinimum ? "minimum" : "maximum";
        LineDataSet lineDataSet = new LineDataSet(values, label);
        lineDataSet.setLineWidth(0.5f);
        lineDataSet.setCircleRadius(1f);

        int colorIndex = isMinimum ? 1 : 2;

        int color = ColorTemplate.COLORFUL_COLORS[colorIndex];
        lineDataSet.setColor(color);
        return lineDataSet;
    }

    private ILineDataSet getUserWeightValues(List<UserReading> userReadings) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        int index = 0;
        for (UserReading reading: userReadings) {
            values.add(new Entry((float) reading.Weight, index++));
        }

        LineDataSet lineDataSet = new LineDataSet(values, "actual");
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCircleRadius(4f);

        int color = ColorTemplate.COLORFUL_COLORS[0];
        lineDataSet.setColor(color);
        return lineDataSet;
    }

    private List<String> getXaxisValues(int xValueCount)
    {
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
        loadChartDataForPregnancy();
    }
}
