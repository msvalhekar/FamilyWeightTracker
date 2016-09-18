package com.mk.familyweighttracker.Framework;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class StringHelper {
    public static boolean isNullOrEmpty(String msg) {
        return msg == null || msg.equals("");
    }
}
