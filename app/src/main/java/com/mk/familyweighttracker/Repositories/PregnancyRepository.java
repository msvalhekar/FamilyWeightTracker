package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class PregnancyRepository {
    static List<BmiWeightRange> categories;
    static List<WeekWeightGain> records;
    static List<WeekWeightGain> twinRecords;

    public PregnancyRepository() {
        loadWeekWeightGainRecords();
        loadWeekWeightGainRecordsForTwins();
        loadBmiCategories();
    }

    // http://www.calculator.net/pregnancy-weight-gain-calculator.html
    private void loadWeekWeightGainRecords() {
        if (records != null) return;

        records = new ArrayList();

        records.add(new WeekWeightGain(0, 0, 0, 0, 0, 0, 0, 0, 0));
        records.add(new WeekWeightGain(1, 0, 0, 0, 0, 0, 0, 0, 0));
        records.add(new WeekWeightGain(2, 0.04, 0.2, 0.04, 0.2, 0.04, 0.2, 0.04, 0.2));
        records.add(new WeekWeightGain(3, 0.08, 0.3, 0.08, 0.3, 0.08, 0.3, 0.08, 0.3));
        records.add(new WeekWeightGain(4, 0.1, 0.5, 0.1, 0.5, 0.1, 0.5, 0.1, 0.5));
        records.add(new WeekWeightGain(5, 0.2, 0.7, 0.2, 0.7, 0.2, 0.7, 0.2, 0.7));
        records.add(new WeekWeightGain(6, 0.2, 0.8, 0.2, 0.8, 0.2, 0.8, 0.2, 0.8));
        records.add(new WeekWeightGain(7, 0.2, 1, 0.2, 1, 0.2, 1, 0.2, 1));
        records.add(new WeekWeightGain(8, 0.3, 1.2, 0.3, 1.2, 0.3, 1.2, 0.3, 1.2));
        records.add(new WeekWeightGain(9, 0.3, 1.3, 0.3, 1.3, 0.3, 1.3, 0.3, 1.3));
        records.add(new WeekWeightGain(10, 0.4, 1.5, 0.4, 1.5, 0.4, 1.5, 0.4, 1.5));
        records.add(new WeekWeightGain(11, 0.4, 1.7, 0.4, 1.7, 0.4, 1.7, 0.4, 1.7));
        records.add(new WeekWeightGain(12, 0.5, 1.8, 0.5, 1.8, 0.5, 1.8, 0.5, 1.8));
        records.add(new WeekWeightGain(13, 0.5, 2, 0.5, 2, 0.5, 2, 0.5, 2));
        records.add(new WeekWeightGain(14, 1, 2.6, 0.9, 2.5, 0.7, 2.3, 0.7, 2.3));
        records.add(new WeekWeightGain(15, 1.4, 3.2, 1.3, 3, 1, 2.7, 0.8, 2.5));
        records.add(new WeekWeightGain(16, 1.9, 3.8, 1.7, 3.5, 1.2, 3, 1, 2.8));
        records.add(new WeekWeightGain(17, 2.3, 4.4, 2.1, 4.1, 1.4, 3.4, 1.2, 3));
        records.add(new WeekWeightGain(18, 2.8, 5, 2.5, 4.6, 1.7, 3.7, 1.3, 3.3));
        records.add(new WeekWeightGain(19, 3.2, 5.6, 2.9, 5.1, 1.9, 4.1, 1.5, 3.6));
        records.add(new WeekWeightGain(20, 3.7, 6.2, 3.3, 5.6, 2.1, 4.4, 1.7, 3.8));
        records.add(new WeekWeightGain(21, 4.1, 6.8, 3.7, 6.1, 2.4, 4.8, 1.8, 4.1));
        records.add(new WeekWeightGain(22, 4.6, 7.4, 4.1, 6.6, 2.6, 5.1, 2, 4.4));
        records.add(new WeekWeightGain(23, 5, 8, 4.5, 7.1, 2.8, 5.5, 2.2, 4.6));
        records.add(new WeekWeightGain(24, 5.5, 8.6, 4.9, 7.7, 3.1, 5.8, 2.3, 4.9));
        records.add(new WeekWeightGain(25, 6.4, 9.2, 5.3, 8.2, 3.3, 6.1, 2.5, 5.1));
        records.add(new WeekWeightGain(26, 6.4, 9.8, 5.7, 8.7, 3.5, 6.5, 2.7, 5.4));
        records.add(new WeekWeightGain(27, 6.8, 10.4, 6.1, 9.2, 3.8, 6.8, 2.8, 5.7));
        records.add(new WeekWeightGain(28, 7.3, 11, 6.5, 9.7, 4, 7.2, 3, 5.9));
        records.add(new WeekWeightGain(29, 7.7, 11.6, 6.9, 10.2, 4.2, 7.5, 3.2, 6.2));
        records.add(new WeekWeightGain(30, 8.2, 12.2, 7.3, 10.7, 4.5, 7.9, 3.3, 6.4));
        records.add(new WeekWeightGain(31, 8.6, 12.8, 7.7, 11.2, 4.7, 8.2, 3.5, 6.7));
        records.add(new WeekWeightGain(32, 9.1, 13.4, 8.1, 11.8, 4.9, 8.6, 3.7, 7));
        records.add(new WeekWeightGain(33, 9.5, 14, 8.5, 12.3, 5.2, 8.9, 3.8, 7.2));
        records.add(new WeekWeightGain(34, 10, 14.6, 8.9, 12.8, 5.4, 9.3, 4, 7.5));
        records.add(new WeekWeightGain(35, 10.4, 15.2, 9.3, 13.3, 5.6, 9.6, 4.2, 7.8));
        records.add(new WeekWeightGain(36, 10.9, 15.8, 9.7, 13.8, 5.9, 10, 4.3, 8));
        records.add(new WeekWeightGain(37, 11.3, 16.3, 10.1, 14.3, 6.1, 10.3, 4.5, 8.3));
        records.add(new WeekWeightGain(38, 11.8, 16.9, 10.5, 14.8, 6.3, 10.6, 4.7, 8.5));
        records.add(new WeekWeightGain(39, 12.2, 17.5, 10.9, 15.4, 6.6, 11, 4.8, 8.8));
        records.add(new WeekWeightGain(40, 12.7, 18.1, 11.3, 15.9, 6.8, 11.3, 5, 9.1));
    }

    private void loadWeekWeightGainRecordsForTwins() {
        if (twinRecords != null) return;

        twinRecords = new ArrayList();

        twinRecords.add(new WeekWeightGain(0, 0, 0, 0, 0, 0, 0, 0, 0));
        twinRecords.add(new WeekWeightGain(1, 0, 0, 0, 0, 0, 0, 0, 0));
        twinRecords.add(new WeekWeightGain(2, 0.04, 0.2, 0.04, 0.2, 0.04, 0.2, 0.04, 0.2));
        twinRecords.add(new WeekWeightGain(3, 0.08, 0.3, 0.08, 0.3, 0.08, 0.3, 0.08, 0.3));
        twinRecords.add(new WeekWeightGain(4, 0.1, 0.5, 0.1, 0.5, 0.1, 0.5, 0.1, 0.5));
        twinRecords.add(new WeekWeightGain(5, 0.2, 0.7, 0.2, 0.7, 0.2, 0.7, 0.2, 0.7));
        twinRecords.add(new WeekWeightGain(6, 0.2, 0.8, 0.2, 0.8, 0.2, 0.8, 0.2, 0.8));
        twinRecords.add(new WeekWeightGain(7, 0.2, 1, 0.2, 1, 0.2, 1, 0.2, 1));
        twinRecords.add(new WeekWeightGain(8, 0.3, 1.2, 0.3, 1.2, 0.3, 1.2, 0.3, 1.2));
        twinRecords.add(new WeekWeightGain(9, 0.3, 1.3, 0.3, 1.3, 0.3, 1.3, 0.3, 1.3));
        twinRecords.add(new WeekWeightGain(10, 0.4, 1.5, 0.4, 1.5, 0.4, 1.5, 0.4, 1.5));
        twinRecords.add(new WeekWeightGain(11, 0.4, 1.7, 0.4, 1.7, 0.4, 1.7, 0.4, 1.7));
        twinRecords.add(new WeekWeightGain(12, 0.5, 1.8, 0.5, 1.8, 0.5, 1.8, 0.5, 1.8));
        twinRecords.add(new WeekWeightGain(13, 0.5, 2, 0.5, 2, 0.5, 2, 0.5, 2));
        twinRecords.add(new WeekWeightGain(14, 1.1, 2.8, 1.1, 2.8, 1, 2.8, 0.9, 2.6));
        twinRecords.add(new WeekWeightGain(15, 1.7, 3.7, 1.7, 3.7, 1.5, 3.5, 1.3, 3.3));
        twinRecords.add(new WeekWeightGain(16, 2.3, 4.5, 2.3, 4.5, 2, 4.3, 1.7, 3.9));
        twinRecords.add(new WeekWeightGain(17, 2.9, 5.3, 2.9, 5.3, 2.5, 5.1, 2.1, 4.5));
        twinRecords.add(new WeekWeightGain(18, 3.5, 6.2, 3.5, 6.2, 3, 5.8, 2.5, 5.2));
        twinRecords.add(new WeekWeightGain(19, 4.1, 7, 4.1, 7, 3.5, 6.6, 2.9, 5.8));
        twinRecords.add(new WeekWeightGain(20, 4.7, 7.8, 4.7, 7.8, 4, 7.4, 3.3, 6.4));
        twinRecords.add(new WeekWeightGain(21, 5.3, 8.7, 5.3, 8.7, 4.5, 8.1, 3.7, 7));
        twinRecords.add(new WeekWeightGain(22, 5.9, 9.5, 5.9, 9.5, 5, 8.9, 4.1, 7.7));
        twinRecords.add(new WeekWeightGain(23, 6.5, 10.3, 6.5, 10.3, 5.5, 9.7, 4.5, 8.3));
        twinRecords.add(new WeekWeightGain(24, 7.1, 11.2, 7.1, 11.2, 6, 10.4, 4.9, 8.9));
        twinRecords.add(new WeekWeightGain(25, 7.7, 12, 7.7, 12, 6.5, 11.2, 5.3, 9.6));
        twinRecords.add(new WeekWeightGain(26, 8.3, 12.8, 8.3, 12.8, 7, 12, 5.7, 10.2));
        twinRecords.add(new WeekWeightGain(27, 8.9, 13.7, 8.9, 13.7, 7.5, 12.7, 6.1, 10.8));
        twinRecords.add(new WeekWeightGain(28, 9.5, 14.5, 9.5, 14.5, 8, 13.5, 6.5, 11.5));
        twinRecords.add(new WeekWeightGain(29, 10.1, 15.3, 10.1, 15.3, 8.5, 14.3, 6.9, 12.1));
        twinRecords.add(new WeekWeightGain(30, 10.8, 16.2, 10.8, 16.2, 9, 15, 7.3, 12.7));
        twinRecords.add(new WeekWeightGain(31, 11.4, 17, 11.4, 17, 9.5, 15.8, 7.7, 13.4));
        twinRecords.add(new WeekWeightGain(32, 12, 17.8, 12, 17.8, 10, 16.6, 8.1, 14));
        twinRecords.add(new WeekWeightGain(33, 12.6, 18.7, 12.6, 18.7, 10.5, 17.3, 8.5, 14.6));
        twinRecords.add(new WeekWeightGain(34, 13.2, 19.5, 13.2, 19.5, 11, 18.1, 8.9, 15.3));
        twinRecords.add(new WeekWeightGain(35, 13.8, 20.3, 13.8, 20.3, 11.5, 18.8, 9.3, 15.9));
        twinRecords.add(new WeekWeightGain(36, 14.4, 21.2, 14.4, 21.2, 12.1, 19.6, 9.7, 16.5));
        twinRecords.add(new WeekWeightGain(37, 15, 22, 15, 22, 12.6, 20.4, 10.1, 17.2));
        twinRecords.add(new WeekWeightGain(38, 15.6, 22.8, 15.6, 22.8, 13.1, 21.1, 10.5, 17.8));
        twinRecords.add(new WeekWeightGain(39, 16.2, 23.7, 16.2, 23.7, 13.6, 21.9, 10.9, 18.4));
        twinRecords.add(new WeekWeightGain(40, 16.8, 24.5, 16.8, 24.5, 14.1, 22.7, 11.3, 19.1));
    }

    private void loadBmiCategories() {
        if(categories != null) return;

        categories = new ArrayList();

        categories.add(new BmiWeightRange(BodyWeightCategory.UnderWeight, Double.MIN_VALUE, 18.49));
        categories.add(new BmiWeightRange(BodyWeightCategory.Normal, 18.5, 24.99));
        categories.add(new BmiWeightRange(BodyWeightCategory.OverWeight, 25, 29.99));
        categories.add(new BmiWeightRange(BodyWeightCategory.Obese, 30, Double.MAX_VALUE));
    }

    public List<WeekWeightGainRange> createWeightGainTableFor(BodyWeightCategory category, WeightUnit weightUnit, boolean forTwins) {
        List<WeekWeightGain> weekWeightGainList = forTwins ? twinRecords : records;

        List<WeekWeightGainRange> result = new ArrayList<>();
        for (WeekWeightGain weekWeightGain : weekWeightGainList) {
            result.add(weekWeightGain.getWeekWeightGainRangeFor(category, weightUnit));
        }
        return result;
    }

    public BodyWeightCategory getWeightCategory(double bmi) {
        for (BmiWeightRange range: categories)
        {
            if(range.isWithinRange(bmi))
                return range.Category;
        }
        return BodyWeightCategory.Invalid;
    }

    private class BmiWeightRange {
        public final BodyWeightCategory Category;
        public final double MaximumBmi;
        public final double MinimumBmi;

        private BmiWeightRange(BodyWeightCategory category, double minimumBmi, double maximumBmi) {
            Category = category;
            MaximumBmi = maximumBmi;
            MinimumBmi = minimumBmi;
        }

        public boolean isWithinRange(double bmi)
        {
            return MinimumBmi <= bmi && bmi <= MaximumBmi;
        }
    }

    private class WeekWeightGain {
        public final int WeekNumber;

        public final double UnderWeightMaximum;
        public final double UnderWeightMinimum;
        public final double NormalMaximum;
        public final double NormalMinimum;
        public final double OverWeightMaximum;
        public final double OverWeightMinimum;
        public final double ObeseMaximum;
        public final double ObeseMinimum;

        private WeekWeightGain(int weekNumber,
                               double underWeightMinimum, double underWeightMaximum,
                               double normalMinimum, double normalMaximum,
                               double overWeightMinimum, double overWeightMaximum,
                               double obeseMinimum, double obeseMaximum)
        {
            WeekNumber = weekNumber;
            UnderWeightMaximum = underWeightMaximum;
            UnderWeightMinimum = underWeightMinimum;
            NormalMaximum = normalMaximum;
            NormalMinimum = normalMinimum;
            OverWeightMaximum = overWeightMaximum;
            OverWeightMinimum = overWeightMinimum;
            ObeseMaximum = obeseMaximum;
            ObeseMinimum = obeseMinimum;
        }

        public WeekWeightGainRange getWeekWeightGainRangeFor(BodyWeightCategory category, WeightUnit weightUnit) {
            double multiplyBy = (weightUnit == WeightUnit.lb) ? Utility.POUNDS_PER_KILOGRAM : 1;
            switch (category){
                case UnderWeight:
                    return new WeekWeightGainRange(WeekNumber, UnderWeightMinimum * multiplyBy, UnderWeightMaximum * multiplyBy);
                case Normal:
                    return new WeekWeightGainRange(WeekNumber, NormalMinimum * multiplyBy, NormalMaximum * multiplyBy);
                case OverWeight:
                    return new WeekWeightGainRange(WeekNumber, OverWeightMinimum * multiplyBy, OverWeightMaximum * multiplyBy);
                case Obese:
                    return new WeekWeightGainRange(WeekNumber, ObeseMinimum * multiplyBy, ObeseMaximum * multiplyBy);
            }
            return null;
        }
    }
}
