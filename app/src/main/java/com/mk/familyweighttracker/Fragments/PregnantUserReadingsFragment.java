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
import com.mk.familyweighttracker.Enums.UserType;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Arrays;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserReadingsFragment extends PregnantUserBaseFragment {

    private View mFragmentView;
    private RecyclerView mRecyclerView;

    public boolean showHelpMenu() {
        return true;
    }

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
                getUser().getDetailsRecordsEvent(),
                String.format(Constants.AnalyticsActions.UserDetailsRecords, getUser().name, getUser().getTypeShortName()),
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
                            Toast.makeText(view.getContext(), R.string.add_reading_prepregnancy_must_exist_for_pregnancy_reading_message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (showDialogIfConflictingReading(view.getContext())) {
                            dialog.dismiss();
                            return;
                        }
                    case Delivery:
                        if(getUser().getPrepregnancyReading() == null) {
                            Toast.makeText(view.getContext(), R.string.add_reading_prepregnancy_must_exist_for_delivery_reading_message, Toast.LENGTH_SHORT).show();
                            return;
                        }
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
        Intent intent = new Intent(getContext(), AddPregnancyReadingActivity.class);
        intent.putExtra(Constants.ExtraArg.USER_ID, getUserId());
        intent.putExtra(Constants.ExtraArg.EDIT_READING_ID, readingId);
        startActivityForResult(intent, Constants.RequestCode.EDIT_READING);
    }

    private void gotoAddReading(PregnancyReadingType type) {
        Intent intent = new Intent(getContext(), AddPregnancyReadingActivity.class);
        intent.putExtra(Constants.ExtraArg.USER_ID, getUserId());
        intent.putExtra(Constants.ExtraArg.ADD_READING_TYPE, type == null ? "" : type.toString());
        startActivityForResult(intent, Constants.RequestCode.ADD_READING);
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

        mFragmentView.findViewById(R.id.user_records_list_record_content_help)
                .setVisibility(getUser().isPregnant() ? View.VISIBLE : View.GONE);
        mFragmentView.findViewById(R.id.infant_records_list_record_content_help)
                .setVisibility(!getUser().isPregnant() ? View.VISIBLE : View.GONE);

        if(getUser().getReadingsCount() == 0) {
            mFragmentView.findViewById(R.id.user_records_list_record_content_help).setVisibility(View.GONE);
            mFragmentView.findViewById(R.id.infant_records_list_record_content_help).setVisibility(View.GONE);

            mFragmentView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_title)).setText(R.string.user_readings_not_found_message);
            ((TextView) mFragmentView.findViewById(R.id.empty_mesage_description)).setText(R.string.user_readings_add_reading_message);
        }
    }

    private void initReadingListControl() {

        mFragmentView.findViewById(R.id.user_card_view_help)
                .setOnClickListener(onClickListener);
        mFragmentView.findViewById(R.id.infant_card_view_help)
                .setOnClickListener(onClickListener);
    }

    public void onHelpMenu() {
        onClickListener.onClick(null);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(getContext())
                    .setMessage(Html.fromHtml(getUser().isPregnant() ? getPregnancyHelpMessage() : getInfantHelpMessage()))
                    .setPositiveButton(getString(R.string.got_it_label), null)
                    .create()
                    .show();

            Analytic.setData(Constants.AnalyticsCategories.Activity,
                    getUser().getDetailsReadingsHelpEvent(),
                    String.format(Constants.AnalyticsActions.ShowUserReadingHelp, getUser().name, getUser().getTypeShortName()),
                    null);

        }
    };

    private String getPregnancyHelpMessage() {
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
        builder.append("<br />" + "The minimum weight expected for this week, as per Pregnancy Weight Gain chart (per WHO Standards).");
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

    private String getInfantHelpMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("<big>");
        builder.append("<b>" + "Month" + "</b>");
        builder.append("<br />" + "Month number, for which the reading was taken.");
        builder.append("<br /><b>" + "Date" + "</b>");
        builder.append("<br />" + "The date when the reading was recorded.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Measure" + "</b>");
        builder.append("<br />" + "The infant's physical growth is tracked by Weight, Height and Head Circumference.");
        builder.append("<br />");
        builder.append("<br /><b>" + "Actual reading" + "</b>");
        builder.append("<br />" + "The actual weight/height/head circumference recorded for this month.");
        builder.append("<br /><b>" + "(+/-diff)" + "</b>");
        builder.append("<br />" + "The difference between actual value recorded for this month and previous month.");
        builder.append("<br />");
        builder.append("<br /><b>" + "3rd Percentile" + "</b>");
        builder.append("<br />" + "The minimum percentile expected for this month, as per Infant growth chart (per WHO Standards).");
        builder.append("<br />" + "Being on the 3rd percentile for weight would mean that 3% of children at this age are lighter than your child and 97% are heavier.");
        builder.append("<br />" + "As for height, 3% are shorter and 97% are taller.");
        builder.append("<br />" + "As for head circumference, 3% have smaller head and 97% have bigger.");
        builder.append("<br />");
        builder.append("<br /><b>" + "97th Percentile" + "</b>");
        builder.append("<br />" + "The maximum percentile expected for this month, as per Infant growth chart (per WHO Standards).");
        builder.append("<br />" + "Being on the 97th percentile for weight would mean that 97% of children at this age are lighter than your child and 3% are heavier.");
        builder.append("<br />" + "As for height, 97% are shorter and 3% are taller.");
        builder.append("<br />" + "As for head circumference, 97% have smaller head and 3% have bigger.");
        builder.append("<br />");
        builder.append("<br />" + "Consult your paediatrician if definite pattern is not observed.");
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
                onUserDataChange();
                createInfantUserIfRequired();
                break;
            case Constants.RequestCode.EDIT_READING:
                onUserDataChange();
                break;
        }
    }

    private void createInfantUserIfRequired() {
        if(!getUser().isPregnant())
            return;

        final UserReading latestReading = getUser().getLatestReading();
        if(latestReading != null && latestReading.isDeliveryReading()) {

            Toast.makeText(getContext(), R.string.welcome_newborn_message, Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.create_newborn_dialog_title))
                    .setMessage(getString(R.string.create_newborn_dialog_message))
                    .setPositiveButton(R.string.yes_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            createInfantUser("Baby", latestReading.TakenOn);
                            if(getUser().haveTwins) {
                                createInfantUser("Baby2", latestReading.TakenOn);
                            }

                            Toast.makeText(getContext(), R.string.created_newborn_message, Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.no_label, null)
                    .create()
                    .show();
        }
    }

    private void createInfantUser(String userName, Date dob) {
        User baby = User.createUser(UserType.Infant);
        baby.name = userName;
        baby.dateOfBirth = dob;
        new UserService().add(baby);
    }
}
