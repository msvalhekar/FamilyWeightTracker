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
public class Utility {
    public static final double POUNDS_PER_KILOGRAM = 2.20462;
    public static final double INCHES_PER_METER = 39.3701;
    public static final double CENTIMETERS_PER_METER = 100;
    public static final double CENTIMETERS_PER_INCH = 2.54;

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

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
