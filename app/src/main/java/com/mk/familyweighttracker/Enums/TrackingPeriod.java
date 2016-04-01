package com.mk.familyweighttracker.Enums;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public enum TrackingPeriod {
    Daily(1),
    Weekly(7),
    //BiWeekly(15),
    Monthly(30),
    Yearly(365);
    //None(0);

    private final int value;

    TrackingPeriod(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
