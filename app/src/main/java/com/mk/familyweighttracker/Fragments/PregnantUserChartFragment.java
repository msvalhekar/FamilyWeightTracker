package com.mk.familyweighttracker.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserChartFragment extends PregnantUserBaseFragment implements OnChartValueSelectedListener {

    private View mFragmentView;
    private LineChart mLineChart;

    public PregnantUserChartFragment() {
        // Required empty public constructor
    }

    public boolean showShareChartMenu() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_chart, container, false);

        initChartControl();

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                getUser().getDetailsChartEvent(),
                String.format(Constants.AnalyticsActions.UserDetailsChart, getUser().name, getUser().getTypeShortName()),
                null);

        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getUser().getReadingsCount() == 0)
            return;

        setChartDescriptionControl();
        loadChartDataForPregnancy();
    }

    private void setChartDescriptionControl() {
        mFragmentView.findViewById(R.id.user_detail_chart_description_section).setVisibility(View.VISIBLE);

        UserReading latestReading = getUser().getLatestReading();

        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_week_no))
                .setText(String.format(getString(R.string.chart_desc_current_week_label), latestReading.Sequence));
        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_exp_this_week))
                .setText(R.string.chart_desc_exp_current_week_label);
        ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_exp_40th_week))
                .setText(R.string.chart_desc_40th_week_label);

        List<WeekWeightGainRange> weightGainRange = getWeightGainRangeTable();

        WeekWeightGainRange fortiethWeekRange = weightGainRange.get(weightGainRange.size()-1);
        WeekWeightGainRange latestWeekRange = null;
        for(WeekWeightGainRange range: weightGainRange) {
            if(range.WeekNumber == latestReading.Sequence) {
                latestWeekRange = range;
                break;
            }
        }

        if(latestWeekRange != null) {
            ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_actual))
                    .setText(Html.fromHtml(String.format("%s: %.2f %s", getString(R.string.weight_label), latestReading.Weight, getUser().weightUnit)));
            ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_min_exp_this_week))
                    .setText(Html.fromHtml(String.format("%s: %.2f", getString(R.string.chart_desc_exp_min_wt_label), latestWeekRange.MinimumWeight)));
            ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_min_exp_40th_week))
                    .setText(Html.fromHtml(String.format("%s: %.2f", getString(R.string.chart_desc_exp_min_wt_label), fortiethWeekRange.MinimumWeight)));

            ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_max_exp_this_week))
                    .setText(Html.fromHtml(String.format("%s: %.2f", getString(R.string.chart_desc_exp_max_wt_label), latestWeekRange.MaximumWeight)));
            ((TextView) mFragmentView.findViewById(R.id.user_detail_chart_description_weight_max_exp_40th_week))
                    .setText(Html.fromHtml(String.format("%s: %.2f", getString(R.string.chart_desc_exp_max_wt_label), fortiethWeekRange.MaximumWeight)));
        }
    }

    private void initChartControl() {
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

    private void loadChartDataForPregnancy() {
        mLineChart.resetTracking();
        List<WeekWeightGainRange> weightGainRange = getWeightGainRangeTable();

        List<String> xVals = getXaxisValues(weightGainRange.size());

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(getUserWeightValues(getUser().getReadings(true)));
        dataSets.add(getWeightRangeValues(weightGainRange, true));
        dataSets.add(getWeightRangeValues(weightGainRange, false));

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        //mLineChart.invalidate();
        mLineChart.animateX(1000);
        mLineChart.setDescription(String.format("%s - Weight (%s) for Pregnancy (%s)", getUser().name, getUser().weightUnit, getUser().trackingPeriod));
        mLineChart.setDescriptionTextSize(15);
    }

    private ILineDataSet getWeightRangeValues(List<WeekWeightGainRange> weightRangeList, boolean isMinimum) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        int index = 0;
        for (WeekWeightGainRange weightRange: weightRangeList) {
            double value = isMinimum ? weightRange.MinimumWeight : weightRange.MaximumWeight;
            values.add(new Entry((float) value, index++));
        }
        String label = isMinimum ? getString(R.string.chart_exp_min_legend) : getString(R.string.chart_exp_max_legend);
        LineDataSet lineDataSet = new LineDataSet(values, label);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawValues(false);

        int color = isMinimum ? Color.rgb(0, 153, 153) : Color.rgb(255, 102, 178);
        lineDataSet.setColor(color);
        return lineDataSet;
    }

    private ILineDataSet getUserWeightValues(List<UserReading> userReadings) {
        ArrayList<Entry> values = new ArrayList<Entry>();

        for (UserReading reading: userReadings) {
            if(!reading.isDeliveryReading())
                values.add(new Entry((float) reading.Weight, (int) reading.Sequence));
        }

        LineDataSet lineDataSet = new LineDataSet(values, getString(R.string.chart_actual_weight_legend));
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

    public void onShareChartMenu() {
        if(getUser().getReadingsCount() == 0) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Cannot proceed")
                    .setMessage("No chart data available.")
                    .setPositiveButton(R.string.ok_label, null)
                    .create()
                    .show();
            return;
        }

        if(mLineChart != null) {
            Map<String, Bitmap> map = new HashMap<String, Bitmap>();
            map.put(getUser().getChartPath(), mLineChart.getChartBitmap());
            saveBitmapAs(map);
        }
    }
}
