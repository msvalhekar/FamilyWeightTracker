package com.mk.familyweighttracker.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mk.familyweighttracker.Activities.AddNewUserActivity;
import com.mk.familyweighttracker.Activities.AddUserRecordActivity;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.IUserDetailsFragment;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsRecordsFragment extends Fragment implements IUserDetailsFragment {

    private static final int NEW_USER_RECORD_ADDED_REQUEST = 1;
    private User mUser;
    private RecyclerView mRecyclerView;

    public UserDetailsRecordsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_details_records, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.button_user_add_record);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddUserRecordActivity.class);
                intent.putExtra(UserDetailActivity.ARG_USER_ID, mUser.getId());
                startActivityForResult(intent, NEW_USER_RECORD_ADDED_REQUEST);
            }
        });

        mRecyclerView = ((RecyclerView) view.findViewById(R.id.user_record_list));
        setupRecyclerView(mRecyclerView);

        return view;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(new UserService().get(mUser.getId())));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_USER_RECORD_ADDED_REQUEST) {
            // Make sure the request was successful
            if (resultCode == -1 /*RESULT_OK*/) {
                // update the list for new record
                setupRecyclerView((RecyclerView)mRecyclerView);
                ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final User mUser;

        public SimpleItemRecyclerViewAdapter(User user) {
            mUser = user;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_record_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final UserReading reading = mUser.getReadings().get(position);

            holder.setReading(reading);

//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, UserDetailActivity.class);
//                    intent.putExtra(UserDetailActivity.ARG_USER_ID, user.getId());
//                    context.startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mUser.getReadings().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNameView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.user_item_name);
            }

            public void setReading(UserReading reading)
            {
                mNameView.setText(String.valueOf(reading.Weight));
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }

    public interface OnNewReadingAdded {
        void onNewReadingAdded();
    }
}
