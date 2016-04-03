package com.mk.familyweighttracker.Enums;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public enum TrackingPeriod {
    Day(1),
    Week(7),
    //BiWeek(15),
    Month(30),
    Year(365);
    //None(0);

    private final int value;

    TrackingPeriod(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
