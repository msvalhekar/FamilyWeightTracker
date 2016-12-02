package com.mk.familyweighttracker.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddPregnancyReadingActivity;
import com.mk.familyweighttracker.Activities.AddPregnantUserActivity;
import com.mk.familyweighttracker.Adapter.InfantUserReadingAdapter;
import com.mk.familyweighttracker.Adapter.PregnantUserReadingAdapter;
import com.mk.familyweighttracker.Enums.PregnancyReadingType;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserReadingsFragment extends PregnantUserBaseFragment {

    private View mFragmentView;
    private RecyclerView mRecyclerView;

    public PregnantUserReadingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_records, container, false);

        initAddUserReadingControl();

        initReadingListControl();

        Analytic.setData(Constants.AnalyticsCategories.Fragment,
                Constants.AnalyticsEvents.UserDetailsRecords,
                String.format(Constants.AnalyticsActions.UserDetailsRecords, getUser().name),
                null);

        mRecyclerView = ((RecyclerView) mFragmentView.findViewById(R.id.user_record_list));

        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        bindReadingList();
    }

    private void initAddUserReadingControl() {
        FloatingActionButton fab = (FloatingActionButton) mFragmentView.findViewById(R.id.button_user_add_record);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (getUser().isPregnant()) {
                    if (promptIfDeliveryDateNotSet(view.getContext())) {
                        return;
                    }

                    if (getUser().maxReadingsReached()) {
                        Toast.makeText(view.getContext(),
                                R.string.MaximumNWeeksDataSupportedMessage,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    setReadingTypes(view);
                } else {
                    if (getUser().maxReadingsReached()) {
                        Toast.makeText(view.getContext(),
                                R.string.MaximumNMonthsDataSupportedMessage,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    gotoAddReading(null);
                }
            }
        });
    }

    private boolean showDialogIfConflictingReading(Context context) {
        long estimatedSequence = getUser().getEstimatedSequence();
        final UserReading reading = new UserService().getReadingBySequence(getUserId(), estimatedSequence);
        if (reading == null)
            return false;

        String messageFormat = getResources().getString(R.string.add_conflicting_week_message);

        new AlertDialog.Builder(context)
                .setTitle(R.string.add_conflicting_week_title)
                .setMessage(String.format(messageFormat, reading.Sequence))
                .setPositiveButton(context.getString(R.string.add_conflicting_week_positive_action_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoEditReading(reading.Id);
                    }
                })
                .setNegativeButton(context.getString(R.string.add_conflicting_week_negative_action_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoAddReading(PregnancyReadingType.Pregnancy);
                    }
                })
                .create()
                .show();
        return true;
    }

    private void setReadingTypes(View view) {
        ArrayAdapter<PregnancyReadingType> adapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_list_item_single_choice,
                Arrays.asList(PregnancyReadingType.values()));

        ListView readingTypesListView = new ListView(view.getContext());
        readingTypesListView.setAdapter(adapter);

        final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(getResources().getString(R.string.add_reading_type_title))
                .setView(readingTypesListView)
                .create();

        readingTypesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PregnancyReadingType type = (PregnancyReadingType)((ListView) parent).getAdapter().getItem(position);
                switch (type) {
                    case PrePregnancy:
                        if(getUser().getPrepregnancyReading() != null) {
                            Toast.makeText(view.getContext(), R.string.add_reading_prepregnancy_exists_message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case Pregnancy:
                        if(getUser().getPrepregnancyReading() == null) {
                            Toast.makeText(view.getContext(), R.string.add_reading_prepregnancy_must_exist_message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (showDialogIfConflictingReading(view.getContext())) {
                            dialog.dismiss();
                            return;
                        }
                    case Delivery:
                        if(getUser().getDeliveryReading() != null) {
                            Toast.makeText(view.getContext(), R.string.add_reading_delivery_exists_message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }
                gotoAddReading(type);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void gotoEditReading(long readingId) {
        Intent intent = getReadingActivityIntent();
        intent.putExtra(Constants.ExtraArg.USER_ID, getUserId());
        intent.putExtra(Constants.ExtraArg.EDIT_READING_ID, readingId);
        startActivityForResult(intent, Constants.RequestCode.EDIT_READING);
    }

    private void gotoAddReading(PregnancyReadingType type) {
        Intent intent = getReadingActivityIntent();
        intent.putExtra(Constants.ExtraArg.USER_ID, getUserId());
        intent.putExtra(Constants.ExtraArg.ADD_READING_TYPE, type == null ? "" : type.toString());
        startActivityForResult(intent, Constants.RequestCode.ADD_READING);
    }

    private Intent getReadingActivityIntent() {
        return new Intent(getContext(), AddPregnancyReadingActivity.class);
//        return new Intent(getContext(), getUser().isPregnant()
//                ? AddPregnancyReadingActivity.class
//                : AddInfantReadingActivity.class);
    }

    private boolean promptIfDeliveryDateNotSet(final Context context) {
        if (getUser().deliveryDueDate != null)
            return false;

        new AlertDialog.Builder(context)
                .setTitle(R.string.delivery_due_date_required_title)
                .setMessage(R.string.delivery_due_date_required_message)
                .setPositiveButton(context.getString(R.string.button_due_date_action_lable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, AddPregnantUserActivity.class);
                        intent.putExtra(Constants.ExtraArg.USER_ID, getUserId());
                        startActivityForResult(intent, Constants.RequestCode.EDIT_USER);
                    }
                })
                .create()
                .show();
        return true;
    }

    private void bindReadingList() {
        showHideEmptyListControl();

        if(getUser().isPregnant()) {
            PregnantUserReadingAdapter adapter = new PregnantUserReadingAdapter(this, getUser());
            adapter.setOnItemClickListener(new PregnantUserReadingAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(long readingId) {
                    gotoEditReading(readingId);
                }
            });

            mRecyclerView.setAdapter(adapter);
        } else {
            InfantUserReadingAdapter adapter = new InfantUserReadingAdapter(this, getUser());
            adapter.setOnItemClickListener(new InfantUserReadingAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(long readingId) {
                    gotoEditReading(readingId);
                }
            });

            mRecyclerView.setAdapter(adapter);
        }
        //usersAdapter.notifyDataSetChanged();
    }

    private void showHideEmptyListControl() {
        mFragmentView.findViewById(R.id.empty_view).setVisibility(View.GONE);

        if(getUser().getReadingsCount() == 0) {
            mFragmentView.findViewById(R.id.user_records_list_record_content_help).setVisibility(View.GONE);

            mFragmentView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_title)).setText(R.string.user_readings_not_found_message);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_description)).setText(R.string.user_readings_add_reading_message);
        }
    }

    private void initReadingListControl() {

        mFragmentView.findViewById(R.id.card_view)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getContext())
                                .setMessage(Html.fromHtml(getLegendMessage()))
                                .setPositiveButton(getString(R.string.got_it_label), null)
                                .create()
                                .show();

                        Analytic.setData(Constants.AnalyticsCategories.Activity,
                                Constants.AnalyticsEvents.UserReadingHelp,
                                String.format(Constants.AnalyticsActions.ShowUserReadingHelp, getUser().name),
                                null);

                    }
                });
    }

    private String getLegendMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("<big>");
        builder.append("<b>" + "Period" + "</b>");
        builder.append("<br />" + "Week number, for which the reading was taken.");
        builder.append("<br /><b>" + "Date" + "</b>");
        builder.append("<br />" + "The date when the reading was recorded.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Actual Wt" + "</b>");
        builder.append("<br />" + "The actual weight recorded for this week.");
        builder.append("<br /><b>" + "(wt gain)" + "</b>");
        builder.append("<br />" + "The difference between actual weight recorded for this week and previous week.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Exp Min" + "</b>");
        builder.append("<br />" + "The minimum weight expected for this week, as per Pregnancy Weight Gain chart.");
        builder.append("<br /><b>" + "(v/s actual)" + "</b>");
        builder.append("<br />" + "The difference between actual weight recorded and minimum expected weight for this week.");
        builder.append("<br /><font color=\"#ff0000\">RED</font>: indicates that the actual weight is lower than minimum expected, may need to gain more weight.");
        builder.append("<br /><font color=\"#0000ff\">BLUE</font>: indicates that the actual weight is more than minimum expected, good.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Exp Max" + "</b>");
        builder.append("<br />" + "The maximum weight expected for this week, as per Pregnancy Weight Gain chart.");
        builder.append("<br /><b>" + "(v/s actual)" + "</b>");
        builder.append("<br />" + "The difference between actual weight recorded and maximum expected weight for this week.");
        builder.append("<br /><font color=\"#0000ff\">BLUE</font>: indicates that the actual weight is lower than minimum expected, good.");
        builder.append("<br /><font color=\"#ff0000\">RED</font>: indicates that the actual weight is more than maximum expected, may need to loose weight.");
        builder.append("</big>");
        return builder.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case Constants.RequestCode.ADD_READING:
            case Constants.RequestCode.EDIT_READING:
                onUserDataChange();
                break;
        }
    }
}
