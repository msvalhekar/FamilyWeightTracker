package com.mk.familyweighttracker.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.R;

import java.util.Date;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class UserReading {
    public static double DEFAULT_BASE_WEIGHT = 60.0;
    public static double DEFAULT_BASE_HEIGHT = 160.0;
    public static String ImageNameFormat = "u%d_w%d.jpg";

    public long Id;
    public long UserId;
    public long Sequence;
    public double Weight;
    public double Height;
    public Date TakenOn;
    public String Note;

    public String getImageName() {
        return String.format(ImageNameFormat, UserId, Sequence);
    }

    public String getImagePath() {
        return String.format("%s/%s", StorageUtility.getImagesDirectory(), getImageName());
    }

    public UserReading () {
    }

    public UserReading(long id, long userId, long sequence, double weight, double height, String note, Date takenOn) {
        Id = id;
        UserId = userId;
        Sequence = sequence;
        Weight = weight;
        Height = height;
        Note = note;
        TakenOn = takenOn;
    }

    public boolean isPrePregnancyReading() {
        return Sequence == 0;
    }

    public boolean isDeliveryReading() {
        return Sequence == User.MAXIMUM_READINGS_COUNT -1;
    }

    public Bitmap getImageAsBitmap(boolean circular){
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeFile(getImagePath());
        } catch (Exception e) {
        }

        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(TrackerApplication.getApp().getResources(), R.drawable.weekly);

        return circular ? ImageUtility.getCircularBitmap(bitmap) : bitmap;
    }
}
