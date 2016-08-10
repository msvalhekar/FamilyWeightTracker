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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class Constants {

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
        public static final String App = "FWT_";
        public static final String BootReceiver = App + "BootReceiver";
    }
}
