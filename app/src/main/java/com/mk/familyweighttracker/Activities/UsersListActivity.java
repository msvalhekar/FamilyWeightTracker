package com.mk.familyweighttracker.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import com.mk.familyweighttracker.Adapter.UserListRecyclerViewAdapter;
import com.mk.familyweighttracker.Adapter.UserTypeSelectionAdapter;
import com.mk.familyweighttracker.Enums.UserType;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

public class UsersListActivity extends TrackerBaseActivity {

    private RecyclerView mRecyclerView;
    private List<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_item_list);

        Analytic.sendScreenView(Constants.Activities.UsersListActivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_users_list);
        setSupportActionBar(toolbar);

        initToolbarControl();

        initAddNewUserControl();

        mRecyclerView = ((RecyclerView) findViewById(R.id.item_list));
        assert mRecyclerView != null;
    }

    @Override
    protected void onResume(){
        super.onResume();

        resetUserList();
        bindUserList();
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_users_list);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Respond to the action bar's Up/Home button
                finish();
                break;

            case R.id.user_list_share:
                navigateToShareIntent();
                break;

            case R.id.user_list_rate:
                navigateToAppStore();
                break;

            case R.id.user_list_feedback:
                navigateToFeedbackActivity(getApplication());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAddNewUserControl() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_users_list_add_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final UserTypeSelectionAdapter userTypesAdapter = new UserTypeSelectionAdapter(view.getContext());

                new AlertDialog.Builder(view.getContext())
                        .setTitle(getString(R.string.add_user_options_title))
                        .setAdapter(userTypesAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserType selectedUserType = (UserType) userTypesAdapter.getItem(which);
                                gotoAddUserActivityOfType(selectedUserType);
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private void gotoAddUserActivityOfType(UserType userType) {
        switch (userType) {
            case Pregnancy:
            case Infant:
                Intent intent = new Intent(TrackerApplication.getApp(), AddPregnantUserActivity.class);
                intent.putExtra(Constants.ExtraArg.USER_TYPE, userType.toString());
                startActivityForResult(intent, Constants.RequestCode.ADD_USER);
                break;
        }
    }

    private List<User> getUsers() {
        if (mUserList == null) {
            mUserList = new UserService().getAll();
        }
        return mUserList;
    }

    private void resetUserList() {
        mUserList = null;
    }

    private void bindUserList() {
        showHideEmptyListControl();

        UserListRecyclerViewAdapter usersAdapter = new UserListRecyclerViewAdapter(UsersListActivity.this, getUsers());
        usersAdapter.setOnItemClickListener(new UserListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(UsersListActivity.this, PregnantUserDetailActivity.class);
                intent.putExtra(Constants.ExtraArg.USER_ID, user.getId());
                startActivityForResult(intent, Constants.RequestCode.USER_DATA_CHANGED);
            }
        });

        mRecyclerView.setAdapter(usersAdapter);
        //usersAdapter.notifyDataSetChanged();
    }

    private void showHideEmptyListControl() {
        findViewById(R.id.empty_view).setVisibility(View.GONE);

        if(getUsers().size() == 0) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.empty_mesage_title)).setText(R.string.user_readings_not_found_message);
            ((TextView) findViewById(R.id.empty_mesage_description)).setText(R.string.user_readings_add_user_message);
        }
    }
}
