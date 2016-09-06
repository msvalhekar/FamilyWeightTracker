package com.mk.familyweighttracker.Models;

import com.activeandroid.annotation.Column;
import com.mk.familyweighttracker.R;

import java.util.Date;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class UserReading {
    public static double DEFAULT_BASE_WEIGHT = 60.0;
    public static double DEFAULT_BASE_HEIGHT = 160.0;

    public long Id;
    public long UserId;
    public byte[] ImageBytes;
    public long Sequence;
    public double Weight;
    public double Height;
    public Date TakenOn;
    public String Note;


    public UserReading () {
    }

    public UserReading(long id, long userId, byte[] imageBytes, long sequence, double weight, double height, String note, Date takenOn) {
        Id = id;
        UserId = userId;
        ImageBytes = imageBytes;
        Sequence = sequence;
        Weight = weight;
        Height = height;
        Note = note;
        TakenOn = takenOn;
    }

    public boolean isFirstReading() {
        return Sequence == 0;
    }

    public int getDefaultImage(){
        int monthNumber = (int)(Sequence / 4.45);
        switch (monthNumber) {
            case 0: return R.mipmap.ic_month1;
            case 1: return R.mipmap.ic_month2;
            case 2: return R.mipmap.ic_month3;
            case 3: return R.mipmap.ic_month4;
            case 4: return R.mipmap.ic_month5;
            case 5: return R.mipmap.ic_month6;
            case 6: return R.mipmap.ic_month7;
            case 7: return R.mipmap.ic_month8;
            case 8: return R.mipmap.ic_month9;
        }
        return -1;
    }
}
