package com.mk.familyweighttracker.Models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.analytics.AnalyticsReceiver;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.AlarmReceiver;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.LogHelper;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Services.PregnancyService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class User {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

    private long mId;

    public String name;
    public byte[] imageBytes;
    public boolean isMale;
    public Date dateOfBirth;
    public TrackingPeriod trackingPeriod;
    public WeightUnit weightUnit;
    public HeightUnit heightUnit;
    public boolean enableReminder;
    public int reminderDay;
    public int reminderHour;
    public int reminderMinute;

    private List<UserReading> mReadings;

    public User(long id) {
        mId = id;
        mReadings = new ArrayList<>();
    }

    public long getId() {
        return mId;
    }

    public String getDateOfBirthStr() {
        return dateFormat.format(dateOfBirth);
    }

    public String getLastMenstrualPeriodStr() {
        return dateFormat.format(getPrepregnancyReading().TakenOn);
    }

    public String getStartingWeightStr() {
        return String.format("%.2f %s", getStartingWeight(), weightUnit.toString());
    }

    public String getStartingHeightStr() {
        return String.format("%.1f %s", getStartingHeight(), heightUnit.toString());
    }

    public String getBmiStr() {
        return String.format("%.2f", getBmi());
    }

    public double getStartingWeight() {
        UserReading prepregReading = getPrepregnancyReading();
        if(prepregReading != null)
            return prepregReading.Weight;
        return 0;
    }

    public double getStartingHeight() {
        UserReading prepregReading = getPrepregnancyReading();
        if(prepregReading != null)
            return prepregReading.Height;
        return 0;
    }

    private double getWeightInKg() {
        double divideBy = 1;
        if(weightUnit == WeightUnit.lb)
            divideBy = Utility.POUNDS_PER_KILOGRAM;
        return getStartingWeight() / divideBy;
    }

    private double getHeightInMeter() {
        double divideBy = 1;
        if( heightUnit == HeightUnit.cm)
            divideBy = Utility.CENTIMETERS_PER_METER;
        else if( heightUnit == HeightUnit.inch)
            divideBy = Utility.INCHES_PER_METER;
        return getStartingHeight() / divideBy;
    }

    public void addReading(UserReading userReading) {
        mReadings.add(userReading);
    }

    public List<UserReading> getReadings(final boolean ascending) {
        Collections.sort(mReadings, new Comparator<UserReading>() {
            @Override
            public int compare(UserReading lhs, UserReading rhs) {
                return (int) (lhs.Sequence - rhs.Sequence) * (ascending ? 1 : -1);
            }
        });
        return mReadings;
    }

    public UserReading getReadingById(long id) {
        List<UserReading> readings = getReadings(true);
        for (int i = 0; i < readings.size(); i++) {
            if(readings.get(i).Id == id) return readings.get(i);
        }
        return null;
    }

    public UserReading getPrepregnancyReading() {
        if(mReadings != null && mReadings.size() > 0)
            return getReadings(true).get(0);
        return null;
    }

    public UserReading getLatestReading() {
        if(mReadings != null && mReadings.size() > 0)
            return getReadings(false).get(0);
        return null;
    }

    public UserReading findReadingBefore(long sequence) {
        if(mReadings != null && mReadings.size() > 1) {
            List<UserReading> readings = getReadings(true);
            for(int i=0; i<readings.size(); i++) {
                if(readings.get(i).Sequence == sequence) {
                    if(i == 0) return null;
                    return readings.get(i-1);
                }
            }
        }
        return null;
    }

    public double getBmi() {
        return new PregnancyService().calculateBmi(getHeightInMeter(), getWeightInKg());
    }

    public BodyWeightCategory getWeightCategory() {
        return new PregnancyService().getWeightCategory(getBmi());
    }

    public void resetReminder(Context context) {
        if(enableReminder == false) {
            removeReminder(context);
            return;
        }

        Date nextAlarmDate = Utility.getInitialDateOfReminder(reminderDay + 1, reminderHour, reminderMinute);

        Log.i(Constants.LogTag.App, String.format("%s: Set reminder for '%s' to '%s'", Constants.LogTag.User, name, nextAlarmDate.toString()));

        Calendar nextAlarmDateTime = Calendar.getInstance();
        nextAlarmDateTime.setTime(nextAlarmDate);

        PendingIntent pendingIntent = getPendingIntent(context);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextAlarmDateTime.getTimeInMillis(), Utility.WEEK_INTERVAL_MILLIS, pendingIntent);
    }

    public void removeReminder(Context context) {
        Log.i(Constants.LogTag.App, String.format("%s: Remove reminder of '%s'", Constants.LogTag.User, name));

        PendingIntent pendingIntent = getPendingIntent(context);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent alarmReceiverIntent = new Intent(context, AlarmReceiver.class);
        alarmReceiverIntent.setData(Uri.parse("pwt://" + getId()));

        alarmReceiverIntent.putExtra(Constants.ExtraArg.USER_ID, getId());
        return PendingIntent.getBroadcast(context, (int)getId(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
