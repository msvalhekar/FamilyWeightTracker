package com.mk.familyweighttracker.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.mk.familyweighttracker.Activities.PregnantUserDetailActivity;
import com.mk.familyweighttracker.Activities.UsersListActivity;
import com.mk.familyweighttracker.Adapter.InfantChartListRecyclerViewAdapter;
import com.mk.familyweighttracker.Adapter.UserListRecyclerViewAdapter;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfantUserChartFragment extends PregnantUserBaseFragment /*implements OnChartValueSelectedListener*/ {

    private RecyclerView mRecyclerView;
    private View mFragmentView;
    InfantChartListRecyclerViewAdapter mChartsAdapter;

    public InfantUserChartFragment() {
        // Required empty public constructor
    }

    public boolean showShareChartMenu() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_infant_details_chart, container, false);

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                Constants.AnalyticsEvents.InfantDetailsChart,
                String.format(Constants.AnalyticsActions.UserDetailsChart, getUser().name, getUser().getTypeShortName()),
                null);

        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.item_list));
        assert mRecyclerView != null;

        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getUser().getReadingsCount() == 0)
            return;

        bindChartsList();
    }

    private void bindChartsList() {
        showHideEmptyListControl();

        mChartsAdapter = new InfantChartListRecyclerViewAdapter(getContext(), getUser());
        mChartsAdapter.setOnItemClickListener(new InfantChartListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InfantChartListRecyclerViewAdapter.ChartDetail chartDetail) {
//                Intent intent = new Intent(getContext(), PregnantUserDetailActivity.class);
//                intent.putExtra(Constants.ExtraArg.USER_ID, user.getId());
//                startActivityForResult(intent, Constants.RequestCode.USER_DATA_CHANGED);
            }
        });

        mRecyclerView.setAdapter(mChartsAdapter);
        //usersAdapter.notifyDataSetChanged();
    }

    private void showHideEmptyListControl() {
        mFragmentView.findViewById(R.id.empty_view).setVisibility(View.GONE);

        if(getUser().getReadingsCount() == 0) {
            mFragmentView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_title)).setText(R.string.user_readings_not_found_message);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_description)).setText(R.string.user_readings_add_user_message);
        }
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

        saveBitmapAs(mChartsAdapter.getChartBitmaps());
    }
}
