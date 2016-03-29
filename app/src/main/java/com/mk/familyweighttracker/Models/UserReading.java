package com.mk.familyweighttracker.Models;

import java.util.Date;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class UserReading {
    public final long UserId;
    public final long Sequence;
    public final double Weight;
    public final double Height;
    public final Date TakenOn;

    public UserReading(long userId, long sequence, double weight, double height, Date takenOn)
    {
        UserId = userId;
        Sequence = sequence;
        Weight = weight;
        Height = height;
        TakenOn = takenOn;
    }
}
