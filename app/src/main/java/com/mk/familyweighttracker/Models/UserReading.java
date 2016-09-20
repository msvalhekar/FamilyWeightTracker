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

        int resourceId = R.drawable.w30;
//        switch ((int)Sequence) {
//            case 0: resourceId = R.drawable.w00; break;
//            case 1: resourceId = R.drawable.w00; break;
//            case 2: resourceId = R.drawable.w00; break;
//            case 3: resourceId = R.drawable.w03; break;
//            case 4: resourceId = R.drawable.w04; break;
//            case 5: resourceId = R.drawable.w05; break;
//            case 6: resourceId = R.drawable.w03; break;
//            case 7: resourceId = R.drawable.w07; break;
//            case 8: resourceId = R.drawable.w08; break;
//            case 9: resourceId = R.drawable.w09; break;
//            case 10: resourceId = R.drawable.w10; break;
//            case 11: resourceId = R.drawable.w11; break;
//            case 12: resourceId = R.drawable.w12; break;
//            case 13: resourceId = R.drawable.w04; break;
//            case 14: resourceId = R.drawable.w09; break;
//            case 15: resourceId = R.drawable.w15; break;
//            case 16: resourceId = R.drawable.w08; break;
//            case 17: resourceId = R.drawable.w17; break;
//            case 18: resourceId = R.drawable.w18; break;
//            case 19: resourceId = R.drawable.w19; break;
//            case 20: resourceId = R.drawable.w20; break;
//            case 21: resourceId = R.drawable.w21; break;
//            case 22: resourceId = R.drawable.w11; break;
//            case 23: resourceId = R.drawable.w23; break;
//            case 24: resourceId = R.drawable.w24; break;
//            case 25: resourceId = R.drawable.w25; break;
//            case 26: resourceId = R.drawable.w26; break;
//            case 27: resourceId = R.drawable.w27; break;
//            case 28: resourceId = R.drawable.w19; break;
//            case 29: resourceId = R.drawable.w23; break;
//            case 30: resourceId = R.drawable.w30; break;
//            case 31: resourceId = R.drawable.w31; break;
//            case 32: resourceId = R.drawable.w32; break;
//            case 33: resourceId = R.drawable.w24; break;
//            case 34: resourceId = R.drawable.w20; break;
//            case 35: resourceId = R.drawable.w35; break;
//            case 36: resourceId = R.drawable.w36; break;
//            case 37: resourceId = R.drawable.w37; break;
//            case 38: resourceId = R.drawable.w38; break;
//            case 39: resourceId = R.drawable.w26; break;
//            case 40: resourceId = R.drawable.w40; break;
//            case 41: resourceId = R.drawable.w41; break;
//        }
        bitmap = BitmapFactory.decodeResource(TrackerApplication.getApp().getResources(), resourceId);
        return circular ? ImageUtility.getCircularBitmap(bitmap) : bitmap;
    }
}
