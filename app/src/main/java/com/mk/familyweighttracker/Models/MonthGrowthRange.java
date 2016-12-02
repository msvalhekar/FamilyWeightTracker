package com.mk.familyweighttracker.Models;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Utility;

/**
 * Created by mvalhekar on 29-11-2016.
 */
public class MonthGrowthRange {
    public final int MonthNumber;
    public final double WeightMaximum;
    public final double WeightMinimum;
    public final double HeightMaximum;
    public final double HeightMinimum;
    public final double HeadCircumMaximum;
    public final double HeadCircumMinimum;

    public MonthGrowthRange(int monthNumber,
                             double heightMinimum, double heightMaximum,
                             double weightMinimum, double weightMaximum,
                             double headCircumMinimum, double headCircumMaximum)
    {
        MonthNumber = monthNumber;
        WeightMaximum = weightMaximum;
        WeightMinimum = weightMinimum;
        HeightMaximum = heightMaximum;
        HeightMinimum = heightMinimum;
        HeadCircumMaximum = headCircumMaximum;
        HeadCircumMinimum = headCircumMinimum;
    }

    public MonthGrowthRange getMonthGrowthRangeFor(WeightUnit weightUnit, HeightUnit heightUnit, HeightUnit headCirUnit) {
        double weightMultiplyBy = (weightUnit == WeightUnit.lb) ? Utility.POUNDS_PER_KILOGRAM : 1;
        double heightMultiplyBy = (heightUnit == HeightUnit.inch) ? Utility.INCHES_PER_CENTIMETER : 1;
        double hcMultiplyBy = (headCirUnit == HeightUnit.inch) ? Utility.INCHES_PER_CENTIMETER : 1;

        return new MonthGrowthRange(MonthNumber,
                this.HeightMinimum * heightMultiplyBy, HeightMaximum * heightMultiplyBy,
                this.WeightMinimum * weightMultiplyBy, WeightMaximum * weightMultiplyBy,
                this.HeadCircumMinimum * hcMultiplyBy, HeadCircumMaximum * hcMultiplyBy);
    }
}
