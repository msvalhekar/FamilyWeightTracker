package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Models.UserHeader;
import com.mk.familyweighttracker.R;

import com.mk.familyweighttracker.Services.UserService;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UsersListActivity extends AppCompatActivity {

    private static final int NEW_USER_ADDED_REQUEST = 1;
    private static final int USER_DATA_CHANGED_REQUEST = 2;

    private View mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_item_list);

        initToolbarControl();

        initAddNewUserControl();

        initUserListControl();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_USER_ADDED_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // update the list for new record
                setupRecyclerView((RecyclerView)mRecyclerView);
            }
        }
        if (requestCode == USER_DATA_CHANGED_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // update the list for new record
                boolean dataChanged = data.getBooleanExtra(UserDetailActivity.ARG_IS_DATA_CHANGED, false);
                if(dataChanged) {
                    Toast.makeText(getApplicationContext(), "Data changed. Refreshing...", Toast.LENGTH_SHORT).show();
                    setupRecyclerView((RecyclerView) mRecyclerView);
                }
            }
        }
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_users_list);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initAddNewUserControl() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_users_list_add_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewUserActivity.class);
                startActivityForResult(intent, NEW_USER_ADDED_REQUEST);
            }
        });
    }

    private void initUserListControl() {
        mRecyclerView = findViewById(R.id.item_list);
        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List users = new UserService().getAll();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(users));
    }

    private class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<UserHeader> mUsers;

        public SimpleItemRecyclerViewAdapter(List<UserHeader> users) {
            mUsers = users;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final UserHeader user = mUsers.get(position);

            holder.setUser(user);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UserDetailActivity.class);
                    intent.putExtra(UserDetailActivity.ARG_USER_ID, user.getId());
                    startActivityForResult(intent, USER_DATA_CHANGED_REQUEST);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mNameView;
            public UserHeader mUser;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.list_item_image);
                mNameView = (TextView) view.findViewById(R.id.list_item_name);
            }

            public void setUser(UserHeader user)
            {
                mUser = user;

                if( user.getImageBytes() != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(user.getImageBytes(), 0, user.getImageBytes().length);
                    mImageView.setImageBitmap(bitmap);
                } else {
                    mImageView.setImageResource(R.drawable.dummy_contact);
                }
                mNameView.setText(user.getName());
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }
}
