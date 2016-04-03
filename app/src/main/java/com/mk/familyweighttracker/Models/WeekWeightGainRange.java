package com.mk.familyweighttracker.Models;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class WeekWeightGainRange {
    public final long WeekNumber;

    public final double MaximumWeight;

    public final double MinimumWeight;

    public WeekWeightGainRange(long weekNumber, double minimumWeight, double maximumWeight) {
        WeekNumber = weekNumber;
        MaximumWeight = maximumWeight;
        MinimumWeight = minimumWeight;
    }
}
