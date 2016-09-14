package com.mk.familyweighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Services.UserService;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private List<User> mUsers = new UserService().getAll();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Analytic.sendScreenView(Constants.Activities.HomeActivity);

        StorageUtility.createDirectories();

        initToolbarControl();

        List<DashboardItem> items = new ArrayList<>();
        //items.add(new DashboardItem("Calculate BMI", ""));
        //items.add(new DashboardItem("Calculate Pregnancy Weight Gain", ""));
        //items.add(new DashboardItem("Track BMI", ""));
        items.add(new DashboardItem("Track Pregnancy Weight Gain", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUsers.size() > 0) {
                    Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.UserDetailActivity.class);
                    intent.putExtra(Constants.ExtraArg.USER_ID, mUsers.get(0).getId());
                    startActivity(intent);
                    return;
                } else {
                    Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.AddPregnantUserActivity.class);
                    startActivity(intent);
                }
            }
        }));
//        items.add(new DashboardItem("May I Help You?", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TrackerApplication.getApp(), com.mk.familyweighttracker.Activities.TrackerHelpActivity.class);
//                startActivity(intent);
//            }
//        }));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        DashboardItemRecyclerViewAdapter adapter = new DashboardItemRecyclerViewAdapter(items);

        RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.dashboard_grid));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_home);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private class DashboardItemRecyclerViewAdapter extends RecyclerView.Adapter<DashboardItemRecyclerViewAdapter.DashboardItemViewHolder> {
        private List<DashboardItem> mItems;

        public DashboardItemRecyclerViewAdapter(List<DashboardItem> items) {
            mItems = items;
        }

        @Override
        public DashboardItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dashboard_item_content, null);
            DashboardItemViewHolder vh = new DashboardItemViewHolder(layoutView);
            return vh;
        }

        @Override
        public void onBindViewHolder(DashboardItemViewHolder holder, int position) {
            DashboardItem item = mItems.get(position);
            holder.setItem(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class DashboardItemViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;
            private DashboardItem mItem;

            public DashboardItemViewHolder(View itemView) {
                super(itemView);
                textView = ((TextView) itemView.findViewById(R.id.dashboard_gird_item_title));
            }

            public void setItem(DashboardItem item) {
                mItem = item;
                textView.setText(mItem.Title);
                textView.setOnClickListener(mItem.ClickListener);
            }
        }
    }

    private class DashboardItem {
        public String Title;
        public View.OnClickListener ClickListener;

        public DashboardItem(String displayName, View.OnClickListener clickListener) {
            this.Title = displayName;
            this.ClickListener = clickListener;
        }
    }
}
