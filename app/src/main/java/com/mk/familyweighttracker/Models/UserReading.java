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
    public double Height;
    public Date TakenOn;
    public String Note;

    public UserReading () {
    }

    public UserReading(long id, long userId, long sequence, double weight, double height, String note, Date takenOn)
    {
        Id = id;
        UserId = userId;
        Sequence = sequence;
        Weight = weight;
        Height = height;
        Note = note;
        TakenOn = takenOn;
    }
}
