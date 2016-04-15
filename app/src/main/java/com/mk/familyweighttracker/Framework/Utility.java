package com.mk.familyweighttracker.Framework;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class Utility {

    public static String calculateAge(Date dateOfBirth) {
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);

        Calendar now = Calendar.getInstance();
        now.setTime(new java.util.Date());

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
}
