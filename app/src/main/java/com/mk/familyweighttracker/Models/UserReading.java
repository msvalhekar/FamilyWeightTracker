package com.mk.familyweighttracker.Models;

import java.util.Date;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class UserReading {
    public long Id;
    public long UserId;
    public long Sequence;
    public double Weight;
    public int Height;
    public Date TakenOn;

    public UserReading () {
    }

    public UserReading(long userId, long sequence, double weight, int height, Date takenOn)
    {
        UserId = userId;
        Sequence = sequence;
        Weight = weight;
        Height = height;
        TakenOn = takenOn;
    }
}
