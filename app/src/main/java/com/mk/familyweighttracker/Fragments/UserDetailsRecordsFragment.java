package com.mk.familyweighttracker.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddUserRecordActivity;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsRecordsFragment extends Fragment {

    private static final int NEW_USER_RECORD_ADDED_REQUEST = 1;
    private long mSelectedUserId;

    private View mFragmentView;
    private RecyclerView mRecyclerView;

    public UserDetailsRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_records, container, false);

        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);

        initAddUserReadingControl();

        initReadingListControl();

        return mFragmentView;
    }

    private void initAddUserReadingControl() {
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.button_user_add_record);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<UserReading> readings = new UserService().get(mSelectedUserId).getReadings();

                handleIfNoReadingsForPregnancy(getContext(), readings.size());
            }
        });
    }

    private void handleIfNoReadingsForPregnancy(final Context context, int readingCount) {

        if(readingCount > 0) {
            Intent intent = new Intent(getContext(), AddUserRecordActivity.class);
            intent.putExtra(UserDetailActivity.ARG_USER_ID, mSelectedUserId);
            startActivityForResult(intent, NEW_USER_RECORD_ADDED_REQUEST);
            return;
        };

        final UserReading userReading = new UserReading();
        userReading.UserId = mSelectedUserId;
        userReading.Sequence = 0;
        userReading.TakenOn = new Date();

        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_user_first_reading, null);

        final NumberPicker weightPicker = ((NumberPicker) dialogView.findViewById(R.id.add_first_reading_weight_picker));
        double startWeight = 30;
        double endWeight = 150;
        double weightIncrementFactor = 0.05;

        final List<String> weightItems = new ArrayList<>();
        for (double i = startWeight; i<endWeight; i+=weightIncrementFactor)
            weightItems.add(String.format("%1$.2f", i));

        String[] weightValues = weightItems.toArray(new String[weightItems.size()]);
        weightPicker.setMinValue(0);
        weightPicker.setMaxValue(weightValues.length - 1);
        weightPicker.setDisplayedValues(weightValues);
        weightPicker.setWrapSelectorWheel(false);
        weightPicker.setValue(weightValues.length / 2);

        final NumberPicker heightPicker = ((NumberPicker) dialogView.findViewById(R.id.add_first_reading_height_picker));
        int startHeight = 140;
        int endHeight = 210;
        int heightIncrementFactor = 1;
        final List<String> heightItems = new ArrayList<>();
        for (int i = startHeight; i<endHeight; i+=heightIncrementFactor)
            heightItems.add(String.format("%3d", i));

        String[] heightValues = heightItems.toArray(new String[heightItems.size()]);
        heightPicker.setMinValue(0);
        heightPicker.setMaxValue(heightValues.length - 1);
        heightPicker.setDisplayedValues(heightValues);
        heightPicker.setWrapSelectorWheel(false);
        heightPicker.setValue(heightValues.length / 2);

        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                userReading.Weight = Double.valueOf(weightItems.get(weightPicker.getValue()));
                                userReading.Height = Integer.valueOf(heightItems.get(heightPicker.getValue()));

                                new UserService().addReading(userReading);

                                setupRecyclerView(mRecyclerView);
                                ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                Toast.makeText(context,
                                        "Pre-preganancy reading is must for accuracy of calculations.",
                                        Toast.LENGTH_SHORT)
                                    .show();
                            }
                        })
                .create()
                .show();
    }

    private void initReadingListControl() {
        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.user_record_list));
        setupRecyclerView(mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        User user = new UserService().get(mSelectedUserId);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(user.getReadings()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_USER_RECORD_ADDED_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // update the list for new record
                setupRecyclerView(mRecyclerView);
                ((OnNewReadingAdded) getActivity()).onNewReadingAdded();
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<UserReading> mUserReadings;

        public SimpleItemRecyclerViewAdapter(List<UserReading> userReadings) {
            mUserReadings = userReadings;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_record_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final UserReading reading = mUserReadings.get(position);

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
            return mUserReadings.size();
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
