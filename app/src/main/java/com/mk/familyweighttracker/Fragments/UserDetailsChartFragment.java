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
import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.IUserDetailsFragment;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsChartFragment extends Fragment implements IUserDetailsFragment, OnChartValueSelectedListener {

    private User mUser;
    private LineChart mLineChart;

    public UserDetailsChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUser(User user) {
        mUser = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details_chart, container, false);

        initChart(view);

        loadChartDataForPregnenacy();

        return view;
    }

    private void initChart(View parentView)
    {
        mLineChart = (LineChart) parentView.findViewById(R.id.user_detail_chart_linechart);
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

    private void loadChartDataForPregnenacy()
    {
        mLineChart.resetTracking();

        double baseWeight = mUser.getWeight();

        List<String> xVals = getXaxisValues();

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(getUserWeightValues());

//        for (int z = 0; z < 2; z++) {
//
//            ArrayList<Entry> values = new ArrayList<Entry>();
//
//            for (int i = 0; i < xMax; i++) {
//                //WeightRange weightRange = calc.calculateWeightRangeForWeek(i, baseWeight, user.getBaseHeight());
//
//                //double val = (z%2 == 1) ? weightRange.Minimum : weightRange.Maximum;
//                double val = (z%2 == 1) ? weightRange.Minimum : weightRange.Maximum;
//                values.add(new Entry((float) val, i));
//            }
//
//            LineDataSet d = new LineDataSet(values, (z%2 == 1) ? "minimum" : "maximum");
//            //d.setLineWidth(1f);
//            d.setCircleRadius(1f);
//
//            int color = mColors[z%2 + 1];
//            d.setColor(color);
//            //d.setCircleColor(color);
//            dataSets.add(d);
//        }

        // make the first DataSet dashed
        //((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
        //((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
        //((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate();
    }

    private ILineDataSet getUserWeightValues() {
        ArrayList<Entry> values = new ArrayList<Entry>();

        int index = 0;
        for (UserReading reading: mUser.getReadings()) {
            values.add(new Entry((float) reading.Weight, index++));
        }

        LineDataSet lineDataSet = new LineDataSet(values, "actual");
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCircleRadius(4f);

        int color = ColorTemplate.COLORFUL_COLORS[0];
        lineDataSet.setColor(color);
        return lineDataSet;
    }

    private List<String> getXaxisValues()
    {
        PregnancyService pregnancyService = new PregnancyService();
        List<WeekWeightGainRange> weightGainTable = pregnancyService.getWeightGainTableFor(BodyWeightCategory.Normal);
        int xMax = weightGainTable.size();

        ArrayList<String> dataVals = new ArrayList<String>();
        for (int i = 0; i < xMax; i++) {
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
