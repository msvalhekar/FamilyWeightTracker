package com.mk.familyweighttracker.Framework;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public interface OnNewReadingAdded {
    boolean isOriginator();
    void onNewReadingAdded();
}
