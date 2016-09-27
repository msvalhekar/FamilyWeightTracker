package com.mk.familyweighttracker.Framework;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class Utility {
    public static final double POUNDS_PER_KILOGRAM = 2.20462;
    public static final double INCHES_PER_METER = 39.3701;
    public static final double CENTIMETERS_PER_METER = 100;
    public static final double CENTIMETERS_PER_INCH = 2.54;
    public static final long WEEK_INTERVAL_MILLIS = 7 * 24 * 60 * 60 * 1000;

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

    public static String calculateAge(Date dateOfBirth) {
        if(dateOfBirth == null) return "-";

        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);

        Calendar now = Calendar.getInstance();

        if(dob.after(now)) {
            int ageYear = (dob.get(Calendar.YEAR) - now.get(Calendar.YEAR));
            int ageMonth = (dob.get(Calendar.MONTH) - now.get(Calendar.MONTH));
            if(ageYear > 0 || ageMonth > 9)
                return "Invalid DoB";
            return "Baby in womb";
        }

        //calculate age .
        int ageYear = (now.get(Calendar.YEAR) - dob.get(Calendar.YEAR));
        int ageMonth = (now.get(Calendar.MONTH) - dob.get(Calendar.MONTH));
        int ageDays = (now.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH));

        if(ageYear == 0) {
            if(ageMonth == 0) {
                return String.format("%d days", ageDays);
            } else {
                if (ageDays < 0) {
                    GregorianCalendar gregCal = new GregorianCalendar(
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH)-1,
                            now.get(Calendar.DAY_OF_MONTH));
                    ageDays += gregCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    ageMonth --;
                }
                if(ageMonth == 0)
                    return String.format("%d days", ageDays);
                else
                    return String.format("%dm %dd", ageMonth, ageDays);
            }
        } else {
            if(ageMonth == 0)
                return String.format("%d yrs", ageYear);
            else if(ageMonth < 0){
                ageMonth += 12;
                ageYear--;
                if(ageYear == 0)
                    return String.format("%d months", ageMonth);
                return String.format("%dy %dm", ageYear, ageMonth);
            } else {
                return String.format("%dy %dm", ageYear, ageMonth);
            }
        }
    }

    public static Date getInitialDateOfReminder(int reminderDay, int reminderHour, int reminderMinute) {
        Calendar now = Calendar.getInstance();

        Calendar reminder = Calendar.getInstance();
        reminder.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), reminderHour, reminderMinute, 0);

        while(reminder.before(now) || reminderDay != reminder.get(Calendar.DAY_OF_WEEK) ) {
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
}
