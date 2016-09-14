package com.mk.familyweighttracker.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.activeandroid.annotation.Column;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.R;

import java.io.IOException;
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

    public boolean isFirstReading() {
        return Sequence == 0;
    }

    public Bitmap getImageAsBitmap(boolean circular){
        Bitmap bitmap = BitmapFactory.decodeFile(getImagePath());
        if (bitmap != null)
            return circular ? ImageUtility.getCircularBitmap(bitmap) : bitmap;

        int resourceId = -1;
        int monthNumber = (int)(Sequence / 4.45);
        switch (monthNumber) {
            case 0: resourceId = R.mipmap.ic_month1; break;
            case 1: resourceId = R.mipmap.ic_month2; break;
            case 2: resourceId = R.mipmap.ic_month3; break;
            case 3: resourceId = R.mipmap.ic_month4; break;
            case 4: resourceId = R.mipmap.ic_month5; break;
            case 5: resourceId = R.mipmap.ic_month6; break;
            case 6: resourceId = R.mipmap.ic_month7; break;
            case 7: resourceId = R.mipmap.ic_month8; break;
            case 8: resourceId = R.mipmap.ic_month9; break;
        }
        bitmap = BitmapFactory.decodeResource(TrackerApplication.getApp().getResources(), resourceId);
        return circular ? ImageUtility.getCircularBitmap(bitmap) : bitmap;
    }
}
