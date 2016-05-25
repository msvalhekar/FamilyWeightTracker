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

    public static final String ARG_USER_ID = "user_id";
    public static final String ARG_USER_NAME = "user_name";
    public static final String ARG_IS_DATA_CHANGED = "IsDataChanged";
    public static final String ARG_EDIT_READING_ID = "EditReadingId";

    public static final int REQUEST_CODE_FOR_ADD_USER = 1;
    public static final int REQUEST_CODE_FOR_EDIT_USER = 2;
    public static final int REQUEST_CODE_FOR_USER_DATA_CHANGED = 10;
    public static final int REQUEST_CODE_FOR_ADD_READING = 11;
    public static final int REQUEST_CODE_FOR_EDIT_READING = 12;

    public static final int REQUEST_CODE_FOR_IMAGE_LOAD = 21;
    public static final int REQUEST_CODE_FOR_IMAGE_CROP = 22;
}
