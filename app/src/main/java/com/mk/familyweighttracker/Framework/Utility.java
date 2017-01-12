package com.mk.familyweighttracker.Framework;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.R;

import org.joda.time.Instant;
import org.joda.time.Period;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class Utility {
    public static final double POUNDS_PER_KILOGRAM = 2.20462;
    public static final double INCHES_PER_METER = 39.3701;
    public static final double CENTIMETERS_PER_METER = 100;
    public static final double INCHES_PER_CENTIMETER = 0.393701;
    public static final double CENTIMETERS_PER_INCH = 2.54;
    public static final long WEEK_INTERVAL_MILLIS = 7 * 24 * 60 * 60 * 1000;
    public static final long MONTH_INTERVAL_MILLIS = 30 * 24 * 60 * 60 * 1000;
    public static final long DAYS_PER_MONTH = 30;

    public static List<String> getWeekDays() {
        return Arrays.asList(
                getResourceString(R.string.WEEK_DAY_SUNDAY),
                getResourceString(R.string.WEEK_DAY_MONDAY),
                getResourceString(R.string.WEEK_DAY_TUESDAY),
                getResourceString(R.string.WEEK_DAY_WEDNESDAY),
                getResourceString(R.string.WEEK_DAY_THURSDAY),
                getResourceString(R.string.WEEK_DAY_FRIDAY),
                getResourceString(R.string.WEEK_DAY_SATURDAY));
    }

    public static List<String> getMonthDays() {
        return Arrays.asList(
                getResourceString(R.string.MONTH_DAY_1),
                getResourceString(R.string.MONTH_DAY_2),
                getResourceString(R.string.MONTH_DAY_3),
                getResourceString(R.string.MONTH_DAY_4),
                getResourceString(R.string.MONTH_DAY_5),
                getResourceString(R.string.MONTH_DAY_6),
                getResourceString(R.string.MONTH_DAY_7),
                getResourceString(R.string.MONTH_DAY_8),
                getResourceString(R.string.MONTH_DAY_9),
                getResourceString(R.string.MONTH_DAY_10),
                getResourceString(R.string.MONTH_DAY_11),
                getResourceString(R.string.MONTH_DAY_12),
                getResourceString(R.string.MONTH_DAY_13),
                getResourceString(R.string.MONTH_DAY_14),
                getResourceString(R.string.MONTH_DAY_15),
                getResourceString(R.string.MONTH_DAY_16),
                getResourceString(R.string.MONTH_DAY_17),
                getResourceString(R.string.MONTH_DAY_18),
                getResourceString(R.string.MONTH_DAY_19),
                getResourceString(R.string.MONTH_DAY_20),
                getResourceString(R.string.MONTH_DAY_21),
                getResourceString(R.string.MONTH_DAY_22),
                getResourceString(R.string.MONTH_DAY_23),
                getResourceString(R.string.MONTH_DAY_24),
                getResourceString(R.string.MONTH_DAY_25),
                getResourceString(R.string.MONTH_DAY_26),
                getResourceString(R.string.MONTH_DAY_27),
                getResourceString(R.string.MONTH_DAY_28),
                getResourceString(R.string.MONTH_DAY_29),
                getResourceString(R.string.MONTH_DAY_30));
    }

    public static double convertWeightTo(double weight, WeightUnit toUnit) {
        if(toUnit == WeightUnit.kg)
            return weight / POUNDS_PER_KILOGRAM;
        return weight * POUNDS_PER_KILOGRAM;
    }

    public static double convertHeightTo(double height, HeightUnit toUnit) {
        if(toUnit == HeightUnit.cm)
            return height * CENTIMETERS_PER_INCH;
        return height / CENTIMETERS_PER_INCH;
    }

    public static String getAge(Date dateOfBirth, boolean includeWeek) {
        Instant nowInstant = Instant.now();
        Instant dobInstant = new Instant(dateOfBirth);

        Period period = new Period(dobInstant, nowInstant);

        String sAge = "";
        if(period.getYears() > 0)
            sAge = period.getYears() + "y";
        if(period.getMonths() > 0)
            sAge += " " + period.getMonths() + "m";
        if(includeWeek && period.getWeeks() > 0)
            sAge += " " + period.getWeeks() + "w";
        if(period.getDays() > 0)
            sAge += " " + (period.getDays() + (includeWeek ? 0 : period.getWeeks()*7)) + "d";
        if(StringHelper.isNullOrEmpty(sAge))
            sAge = "Today";
        return sAge;
    }

    public static Date getInitialDateOfReminder(boolean isWeekly, int reminderDay, int reminderHour, int reminderMinute) {
        Calendar now = Calendar.getInstance();

        Calendar reminder = Calendar.getInstance();
        reminder.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), reminderHour, reminderMinute, 0);

        while(reminder.before(now) || reminderDay != reminder.get(isWeekly ? Calendar.DAY_OF_WEEK : Calendar.DAY_OF_MONTH) ) {
            reminder.set(reminder.get(Calendar.YEAR),
                    reminder.get(Calendar.MONTH),
                    reminder.get(Calendar.DAY_OF_MONTH)+1,
                    reminderHour, reminderMinute, 0);
        }

        return reminder.getTime();
    }

    public static String getResourceString(int resourceId) {
        return TrackerApplication.getApp().getResources().getString(resourceId);
    }

    public static long calculateRemainingDays(Date deliveryDueDate) {
        if(deliveryDueDate == null)
            return -1;

        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Date todayDate = today.getTime();

        if(deliveryDueDate.before(todayDate))
            return 0;

        long diffInMs = deliveryDueDate.getTime() - todayDate.getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMs);
    }

    public static long calculateDateDiff(Date toDate) {
        return calculateDateDiff(new Date(), toDate);
    }

    public static long calculateDateDiff(Date fromDate, Date toDate) {
        if(toDate == null)
            return -1;

        if(toDate.before(fromDate)) {
            long diffInMs = fromDate.getTime() - toDate.getTime();
            return TimeUnit.MILLISECONDS.toDays(diffInMs) * -1;
        }

        long diffInMs = toDate.getTime() - fromDate.getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMs);
    }
}
