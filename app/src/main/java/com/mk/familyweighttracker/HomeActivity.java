package com.mk.familyweighttracker;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.Activities.UsersListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "MkWeighTracker.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    https://github.com/pardom/ActiveAndroid/wiki/Getting-started
        //ActiveAndroid.dispose();
        //getApplicationContext().deleteDatabase(DATABASE_NAME);
        ActiveAndroid.initialize(getApplicationContext());
        //Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        //configurationBuilder.addModelClass(UserModel.class);
        //ActiveAndroid.initialize(configurationBuilder.create());

        setContentView(R.layout.activity_home);

        initToolbarControl();

        List<DashboardItem> items = new ArrayList<>();
        //items.add(new DashboardItem("Calculate BMI", ""));
        items.add(new DashboardItem("Calculate Pregnancy Weight Gain", ""));
        //items.add(new DashboardItem("Track BMI", ""));
        items.add(new DashboardItem("Track Pregnancy Weight Gain", UsersListActivity.class.getName()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        DashboardItemRecyclerViewAdapter adapter = new DashboardItemRecyclerViewAdapter(this, items);

        RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.dashboard_item_grid));
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
        ActiveAndroid.dispose();
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private class DashboardItemRecyclerViewAdapter extends RecyclerView.Adapter<DashboardItemRecyclerViewAdapter.DashboardItemViewHolder> {
        private Context mContext;
        private List<DashboardItem> mItems;

        public DashboardItemRecyclerViewAdapter(Context context, List<DashboardItem> items) {
            mContext = context;
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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Class className = Class.forName(mItem.ActivityClassName);
                            Intent intent = new Intent(getApplicationContext(), className);
                            startActivity(intent);
                        } catch(Exception e) {
                            Toast.makeText(v.getContext(), "Work In Progress", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                textView = ((TextView) itemView.findViewById(R.id.grid_item_name));
            }

            public void setItem(DashboardItem item) {
                mItem = item;
                textView.setText(mItem.DisplayName);
            }
        }
    }

    private class DashboardItem {
        public String DisplayName;
        public String ActivityClassName;

        public DashboardItem(String displayName, String activityClassName) {
            this.DisplayName = displayName;
            this.ActivityClassName = activityClassName;
        }
    }
}
