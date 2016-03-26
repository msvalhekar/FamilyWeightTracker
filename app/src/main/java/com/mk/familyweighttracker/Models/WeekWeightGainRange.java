package com.mk.familyweighttracker.Models;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class WeekWeightGainRange {
    public final int WeekNumber;

    public final double MaximumWeight;

    public final double MinimumWeight;

    public WeekWeightGainRange(int weekNumber, double maximumWeight, double minimumWeight) {
        WeekNumber = weekNumber;
        MaximumWeight = maximumWeight;
        MinimumWeight = minimumWeight;
    }
}
