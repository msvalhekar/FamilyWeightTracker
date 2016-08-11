package com.mk.familyweighttracker.Framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class Constants {

    public static final String LogDirectory = "sysLog";

    public static class Activities {
        public static final String HomeActivity = "HomeActivity";
        public static final String UsersListActivity = "UsersListActivity";
        public static final String TrackerHelpActivity = "TrackerHelpActivity";
        public static final String AddReadingActivity = "AddReadingActivity";
        public static final String AddPregnantUserActivity = "AddPregnantUserActivity";
        public static final String AddFirstReadingActivity = "AddFirstReadingActivity";
    }

    public static class ExtraArg {
        public static final String USER_ID = "user_id";
        public static final String IS_DATA_CHANGED = "IsDataChanged";
        public static final String EDIT_READING_ID = "EditReadingId";
    }

    public static class RequestCode {
        public static final int ADD_USER = 1;
        public static final int EDIT_USER = 2;
        public static final int USER_DATA_CHANGED = 10;
        public static final int ADD_READING = 11;
        public static final int EDIT_READING = 12;
        public static final int IMAGE_LOAD = 21;
        public static final int IMAGE_CROP = 22;
    }

    public static class LogTag {
        public static final String App = "FWT";
        public static final String User = App + "User";
        public static final String BootReceiver = App + "BootReceiver";
    }

    public static class AnalyticsCategories {
        public static final String Activity = "Activity";
        public static final String BackgroundAction = "BackgroundAction";
        public static final String Action = "Action";
    }

    public static class AnalyticsActions {
        public static final String FirstReadingAdded = "FirstReadingAdded";
        public static final String ReadingAdded = "ReadingAdded %d";
        public static final String ReadingDeleted = "ReadingDeleted %d";
        public static final String UserDetailsLoaded = "UserDetailsLoaded %s";
        public static final String UserDeleted = "UserDeleted %s";
        public static final String ShowUserReadingHelp = "ShowUserReadingHelp";
    }

    public static class AnalyticsEvents {
        public static final String HomeActivityCreated = "HomeActivityCreated";
        public static final String AddFirstReading = "AddFirstReading";
        public static final String AddReading = "AddReading";
        public static final String DeleteReading = "DeleteReading";
        public static final String UserDetailsACtivity = "UserDetailsACtivity";
        public static final String UserDelete = "UserDelete";
        public static final String UserReadingHelp = "UserReadingHelp";
    }
}
