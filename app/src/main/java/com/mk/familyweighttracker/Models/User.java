package com.mk.familyweighttracker.Models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.UserType;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Framework.WeeklyReminderAlarmReceiver;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.PregnancyService;
import com.mk.familyweighttracker.Services.UserService;

import java.io.File;
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

    public static int MAXIMUM_PREGNANCY_READINGS_COUNT = 42;
    public static int MAXIMUM_INFANT_READINGS_COUNT = 36;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    public static String ImageNameFormat = "u%d.jpg";
    public static String ChartNameFormat = "u%dc.jpg";
    public static String WeightChartNameFormat = "u%dwc.jpg";
    public static String HeightChartNameFormat = "u%dhc.jpg";
    public static String HdCircumChartNameFormat = "u%dhcc.jpg";

    private long mId;

    public String name;
    public byte[] imageBytes;
    public boolean isMale;
    public Date dateOfBirth;
    public Date deliveryDueDate;
    public TrackingPeriod trackingPeriod;
    public WeightUnit weightUnit;
    public HeightUnit heightUnit;
    public HeightUnit headCircumUnit;
    public boolean haveTwins;
    public boolean enableReminder;
    public int reminderDay;
    public int reminderHour;
    public int reminderMinute;
    public UserType type;

    private List<UserReading> mReadings;

    public User() {
        mId = generateId();
        mReadings = new ArrayList<>();
    }

    public User(long id) {
        mId = id;
        mReadings = new ArrayList<>();
    }

    private long generateId() {
        UserService service = new UserService();
        List<Long> userIds = new ArrayList<>();
        for (User user : service.getAll()) {
            userIds.add(user.getId());
        }
        Collections.sort(userIds);
        int count = userIds.size();
        if(count > 0)
            return userIds.get(count -1)+1;
        return 1;
    }

    public long getId() {
        return mId;
    }

    public int getReadingsCount() {
        if(mReadings != null)
            return mReadings.size();
        return 0;
    }

    public double getDefaultBaseWeight() {
        return isPregnant() ? UserReading.DEFAULT_PREGNANCY_BASE_WEIGHT : UserReading.DEFAULT_INFANT_BASE_WEIGHT;
    }

    public double getDefaultBaseHeight() {
        return isPregnant() ? UserReading.DEFAULT_PREGNANCY_BASE_HEIGHT : UserReading.DEFAULT_INFANT_BASE_HEIGHT;
    }

    public double getDefaultBaseHeadCircum() {
        return isPregnant() ? UserReading.DEFAULT_PREGNANCY_BASE_HEADCIRCUM : UserReading.DEFAULT_INFANT_BASE_HEADCIRCUM;
    }

    public boolean maxReadingsReached() {
        if(isPregnant())
            return getReadingsCount() >= MAXIMUM_PREGNANCY_READINGS_COUNT || getDeliveryReading() != null;

        return getReadingsCount() >= MAXIMUM_INFANT_READINGS_COUNT;
    }

    public boolean isPregnant() {
        return type == UserType.Pregnancy;
    }

    public String getDateOfBirthStr() {
        if(dateOfBirth == null) return "Not set";
        return dateFormat.format(dateOfBirth);
    }

    public String getDeliveryDueDateStr() {
        if(deliveryDueDate == null) return "Not set";
        return dateFormat.format(deliveryDueDate);
    }

    public String getDeliveryRemainingStr() {
        if(deliveryDueDate == null) return "Not set";
        long remainingDays = Utility.calculateRemainingDays(deliveryDueDate);
        if(remainingDays < 30)
            return String.format("%s day", remainingDays) + (remainingDays > 1 ? "s" : "");

        long remDays = remainingDays % 7;
        long remWeeks = remainingDays / 7;
        String remDaysStr = "";
        if(remDays > 0) remDaysStr = String.format(", %d day(s)", remDays);
        return String.valueOf(String.format("%s weeks%s", remWeeks, remDaysStr));
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

    public UserReading getReadingBySequence(long sequence) {
        List<UserReading> readings = getReadings(true);
        for (int i = 0; i < readings.size(); i++) {
            if(readings.get(i).Sequence == sequence) return readings.get(i);
        }
        return null;
    }

    public UserReading getPrepregnancyReading() {
        if(mReadings != null && mReadings.size() > 0) {
            List<UserReading> readings = getReadings(true);
            if(readings.get(0).isPrePregnancyReading())
                return readings.get(0);
        }
        return null;
    }

    public UserReading getDeliveryReading() {
        if(mReadings != null && mReadings.size() > 0) {
            List<UserReading> readings = getReadings(false);
            if(readings.get(0).isDeliveryReading())
                return readings.get(0);
        }
        return null;
    }

    public UserReading getLatestReading() {
        if(mReadings != null && mReadings.size() > 0) {
            return getReadings(false).get(0);
        }
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

    public int getAgeMonth() {
        long days = Utility.calculateDateDiff(dateOfBirth);
        return (int)(days / Utility.DAYS_PER_MONTH);
    }

    public double getBmi() {
        return new PregnancyService().calculateBmi(getHeightInMeter(), getWeightInKg());
    }

    public BodyWeightCategory getWeightCategory() {
        return new PregnancyService().getWeightCategory(getBmi());
    }

    public String getChartPath() {
        String imageName = String.format(ChartNameFormat, getId());
        return String.format("%s/%s", StorageUtility.getImagesDirectory(), imageName);
    }

    public String getWeightChartPath() {
        String imageName = String.format(WeightChartNameFormat, getId());
        return String.format("%s/%s", StorageUtility.getImagesDirectory(), imageName);
    }

    public String getHeightChartPath() {
        String imageName = String.format(HeightChartNameFormat, getId());
        return String.format("%s/%s", StorageUtility.getImagesDirectory(), imageName);
    }

    public String getHeadCircumChartPath() {
        String imageName = String.format(HdCircumChartNameFormat, getId());
        return String.format("%s/%s", StorageUtility.getImagesDirectory(), imageName);
    }

    public String getImagePath() {
        String imageName = String.format(ImageNameFormat, getId());
        return String.format("%s/%s", StorageUtility.getImagesDirectory(), imageName);
    }

    public Bitmap getImageAsBitmap(boolean circular){
        Bitmap bitmap = null;

        if(new File(getImagePath()).exists())
            bitmap = BitmapFactory.decodeFile(getImagePath());

        if (bitmap != null)
            return circular ? ImageUtility.getCircularBitmap(bitmap) : bitmap;

        bitmap = BitmapFactory.decodeResource(TrackerApplication.getApp().getResources(),
                isPregnant()
                        ? R.drawable.splash
                        : isMale ? R.drawable.boy : R.drawable.girl);
        return circular ? ImageUtility.getCircularBitmap(bitmap) : bitmap;
    }

    public void resetReminder() {
        Context context = TrackerApplication.getApp();
        if(enableReminder == false) {
            removeReminder();
            return;
        }

        Date nextAlarmDate = Utility.getInitialDateOfReminder(isPregnant(), reminderDay + 1, reminderHour, reminderMinute);

        Log.i(Constants.LogTag.App, String.format("%s: Set reminder for '%s' to '%s'", Constants.LogTag.User, name, nextAlarmDate.toString()));

        Calendar nextAlarmDateTime = Calendar.getInstance();
        nextAlarmDateTime.setTime(nextAlarmDate);

        PendingIntent pendingIntent = getPendingIntent(context);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                nextAlarmDateTime.getTimeInMillis(),
                isPregnant() ? Utility.WEEK_INTERVAL_MILLIS : Utility.MONTH_INTERVAL_MILLIS,
                pendingIntent);
    }

    public void removeReminder() {
        Context context = TrackerApplication.getApp();

        Log.i(Constants.LogTag.App, String.format("%s: Remove reminder of '%s'", Constants.LogTag.User, name));

        PendingIntent pendingIntent = getPendingIntent(context);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent alarmReceiverIntent = new Intent(context, WeeklyReminderAlarmReceiver.class);
        alarmReceiverIntent.setData(Uri.parse("pwt://" + getId()));

        alarmReceiverIntent.putExtra(Constants.ExtraArg.USER_ID, getId());
        return PendingIntent.getBroadcast(context, (int)getId(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public long getEstimatedSequence() {
        if (isPregnant()) {
            if (deliveryDueDate == null)
                return -1;

            if (getPrepregnancyReading() == null)
                return 0;

            long days = Utility.calculateDateDiff(deliveryDueDate);
            long remainingWeeks = days / 7;
            long estSeq = 40 - remainingWeeks;
            return estSeq < 0 ? 0 : estSeq;
        } else {
            Date now = new Date();
            if (dateOfBirth == null || now.before(dateOfBirth))
                return -1;

            if (getPrepregnancyReading() == null)
                return 0;

            long days = Utility.calculateDateDiff(dateOfBirth, now);
            long estSeq = days / 30;
            return estSeq;
        }
    }

    public long getNextAvailableSequence() {
        long estSeq = getEstimatedSequence();
        Boolean found = false;
        while (!found) {
            UserReading reading = getReadingBySequence(estSeq);
            if(reading == null)
                return estSeq;
            estSeq ++;
        }
        return -1;
    }

    public long getMaximumReadingCount() {
        return isPregnant() ? User.MAXIMUM_PREGNANCY_READINGS_COUNT : User.MAXIMUM_INFANT_READINGS_COUNT;
    }

    public String getMissingPeriodLabel() {
        return Utility.getResourceString(isPregnant() ? R.string.WeeksMessage : R.string.MonthsMessage);
    }

    public String getSequenceLabel() {
        return Utility.getResourceString(isPregnant()
                ? R.string.reading_period_week_label
                : R.string.reading_period_month_label);
    }

    public String getEditSequenceErrorMessage() {
        return Utility.getResourceString(isPregnant()
                ? R.string.add_reading_cannot_edit_week_error
                : R.string.add_reading_cannot_edit_month_error);
    }

    public String getSequenceChangeLabel() {
        return Utility.getResourceString(isPregnant() ? R.string.WeekNumberMessage : R.string.MonthNumberMessage);
    }

    public String getReadingSavedMessage(long sequence) {
        return String.format(
                Utility.getResourceString(isPregnant() ? R.string.WeekNReadingSavedMessage : R.string.MonthNReadingSavedMessage),
                sequence);
    }

    public String getReadingRemovedMessage(long sequence) {
        return String.format(
                Utility.getResourceString(isPregnant() ? R.string.WeekNReadingRemovedMessage : R.string.MonthNReadingRemovedMessage),
                sequence);
    }

    public String getUserDetailsEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantDetailsActivity : Constants.AnalyticsEvents.InfantDetailsActivity;
    }

    public String getDetailsProfileEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantDetailsProfile : Constants.AnalyticsEvents.InfantDetailsProfile;
    }

    public String getDetailsRecordsEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantDetailsRecords : Constants.AnalyticsEvents.InfantDetailsRecords;
    }

    public String getDetailsReadingsHelpEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantReadingHelp : Constants.AnalyticsEvents.InfantReadingHelp;
    }

    public String getDetailsChartEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantDetailsChart : Constants.AnalyticsEvents.InfantDetailsChart;
    }

    public String getDetailsMediaEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantDetailsMedia : Constants.AnalyticsEvents.InfantDetailsMedia;
    }

    public String getAddUserEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantAdded : Constants.AnalyticsEvents.InfantAdded;
    }

    public String getDeleteUserEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantDelete : Constants.AnalyticsEvents.InfantDelete;
    }

    public String getAddReadingEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantAddReading : Constants.AnalyticsEvents.InfantAddReading;
    }

    public String getDeleteReadingEvent() {
        return isPregnant() ? Constants.AnalyticsEvents.PregnantDeleteReading : Constants.AnalyticsEvents.InfantDeleteReading;
    }

    public TrackingPeriod getTrackingPeriodByType() {
        return isPregnant() ? TrackingPeriod.Week : TrackingPeriod.Month;
    }

    public String getTypeShortName() {
        switch (type) {
            case Pregnancy: return "P";
            case Infant: return "I";
        }
        return "P";
    }

    public String getAddUserActivity() {
        return isPregnant() ? Constants.Activities.PregnantAddUserActivity : Constants.Activities.InfantAddUserActivity;
    }

    public String getUserSlideshowActivity() {
        return isPregnant() ? Constants.Activities.PregnantSlideshowActivity : Constants.Activities.InfantSlideshowActivity;
    }

    public String getAddReadingActivity() {
        return isPregnant() ? Constants.Activities.PregnantAddReadingActivity : Constants.Activities.InfantAddReadingActivity;
    }

    public List<String> getPeriodDays() {
        return isPregnant() ? Utility.getWeekDays() : Utility.getMonthDays();
    }

    public String getReminderPeriodLabel() {
        return Utility.getResourceString(isPregnant()
                ? R.string.add_user_weekly_reminder_text
                : R.string.add_user_monthly_reminder_text);
    }

    public String getDayOfPeriodLabel() {
        return Utility.getResourceString(isPregnant()
                ? R.string.add_user_reminder_dow_text
                : R.string.add_user_reminder_dom_text);
    }

    public String getReminderNotificationTitle() {
        return String.format(
                Utility.getResourceString(isPregnant() ? R.string.notification_pregnancy_title : R.string.notification_infant_title),
                name);
    }

    public String getReminderNotificationMessage() {
        return String.format(
                Utility.getResourceString(isPregnant() ? R.string.notification_pregnancy_message : R.string.notification_infant_message),
                getEstimatedSequence());
    }

    public String getShareChartSubject() {
        return String.format("%s - %s chart(s)",
                name,
                Utility.getResourceString(isPregnant() ? R.string.preg_weight_gain_message : R.string.physical_growth_message));
    }

    public static User createUser(UserType userType) {
        User user = new User();
        user.isMale = false;
        user.haveTwins = false;
        user.enableReminder = true;
        user.reminderDay = 1;
        user.reminderHour = 8;
        user.reminderMinute = 0;
        user.weightUnit = WeightUnit.kg;
        user.heightUnit = HeightUnit.cm;
        user.headCircumUnit = HeightUnit.cm;

        user.type = userType;
        user.trackingPeriod = user.getTrackingPeriodByType();
        return user;
    }

}
