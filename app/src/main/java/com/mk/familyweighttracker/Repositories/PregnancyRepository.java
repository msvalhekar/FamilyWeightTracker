package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class PregnancyRepository {
    static List<BmiWeightRange> categories;
    static List<WeekWeightGain> records;
    static List<WeekWeightGainRange> weightGainTableForUnderWeight;
    static List<WeekWeightGainRange> weightGainTableForNormal;
    static List<WeekWeightGainRange> weightGainTableForOverWeight;
    static List<WeekWeightGainRange> weightGainTableForObese;
    static List<WeekWeightGainRange> weightGainTableForUnderWeightInPounds;
    static List<WeekWeightGainRange> weightGainTableForNormalInPounds;
    static List<WeekWeightGainRange> weightGainTableForOverWeightInPounds;
    static List<WeekWeightGainRange> weightGainTableForObeseInPounds;

    public PregnancyRepository()
    {
        loadWeekWeightGainRecords();
        loadBmiCategories();
    }

    private void loadWeekWeightGainRecords() {
        if(records != null) return;

        records = new ArrayList();

        records.add(new WeekWeightGain(0, 0, 0, 0, 0, 0, 0, 0, 0));
        records.add(new WeekWeightGain(1, 0, 0, 0, 0, 0, 0, 0, 0));
        records.add(new WeekWeightGain(2, 0, 0, 0, 0, 0, 0, 0, 0));
        records.add(new WeekWeightGain(3, 0.1, 0.3, 0.1, 0.3, 0.1, 0.3, 0, 0.2));
        records.add(new WeekWeightGain(4, 0.2, 0.5, 0.2, 0.5, 0.2, 0.5, 0.1, 0.4));
        records.add(new WeekWeightGain(5, 0.3, 0.8, 0.3, 0.8, 0.3, 0.8, 0.1, 0.5));
        records.add(new WeekWeightGain(6, 0.4, 1.1, 0.4, 1.1, 0.4, 1.1, 0.2, 0.7));
        records.add(new WeekWeightGain(7, 0.5, 1.4, 0.5, 1.4, 0.5, 1.4, 0.2, 0.9));
        records.add(new WeekWeightGain(8, 0.5, 1.6, 0.5, 1.6, 0.5, 1.6, 0.3, 1.1));
        records.add(new WeekWeightGain(9, 0.6, 1.9, 0.6, 1.9, 0.6, 1.9, 0.3, 1.3));
        records.add(new WeekWeightGain(10, 0.7, 2.2, 0.7, 2.2, 0.7, 2.2, 0.4, 1.5));
        records.add(new WeekWeightGain(11, 0.8, 2.4, 0.8, 2.4, 0.8, 2.4, 0.4, 1.6));
        records.add(new WeekWeightGain(12, 0.9, 2.7, 0.9, 2.7, 0.9, 2.7, 0.5, 1.8));
        records.add(new WeekWeightGain(13, 1, 3, 1, 3, 1, 3, 0.5, 2));
        records.add(new WeekWeightGain(14, 1.5, 3.5, 1.2, 3.3, 1.4, 3.5, 0.7, 2.3));
        records.add(new WeekWeightGain(15, 1.9, 4.1, 1.4, 3.6, 1.8, 3.9, 0.8, 2.5));
        records.add(new WeekWeightGain(16, 2.3, 4.7, 1.6, 3.9, 2.1, 4.4, 1, 2.8));
        records.add(new WeekWeightGain(17, 2.7, 5.2, 1.9, 4.2, 2.5, 4.9, 1.2, 3));
        records.add(new WeekWeightGain(18, 3.2, 5.8, 2.1, 4.5, 2.9, 5.4, 1.3, 3.3));
        records.add(new WeekWeightGain(19, 3.6, 6.4, 2.3, 4.9, 3.3, 5.9, 1.5, 3.6));
        records.add(new WeekWeightGain(20, 4, 6.9, 2.5, 5.2, 3.7, 6.4, 1.7, 3.8));
        records.add(new WeekWeightGain(21, 4.4, 7.5, 2.7, 5.4, 4.1, 6.8, 1.8, 4.1));
        records.add(new WeekWeightGain(22, 4.9, 8, 2.9, 5.8, 4.4, 7.3, 2, 4.4));
        records.add(new WeekWeightGain(23, 5.4, 8.6, 3.1, 6.1, 4.8, 7.8, 2.2, 4.6));
        records.add(new WeekWeightGain(24, 5.8, 9.2, 3.4, 6.4, 5.2, 8.3, 2.3, 4.9));
        records.add(new WeekWeightGain(25, 6.2, 9.7, 3.6, 6.7, 5.6, 8.7, 2.5, 5.1));
        records.add(new WeekWeightGain(26, 6.6, 10.3, 3.8, 7, 6, 9.2, 2.7, 5.4));
        records.add(new WeekWeightGain(27, 7.1, 10.8, 4, 7.3, 6.4, 9.7, 2.8, 5.7));
        records.add(new WeekWeightGain(28, 7.5, 11.4, 4.2, 7.6, 6.8, 10.2, 3, 5.9));
        records.add(new WeekWeightGain(29, 7.9, 12, 4.4, 7.9, 7.1, 10.6, 3.2, 6.2));
        records.add(new WeekWeightGain(30, 8.3, 12.5, 4.7, 8.3, 7.5, 11.1, 3.3, 6.4));
        records.add(new WeekWeightGain(31, 8.8, 13.1, 4.9, 8.6, 7.9, 11.6, 3.5, 6.7));
        records.add(new WeekWeightGain(32, 9.2, 13.7, 5.1, 8.8, 8.3, 12.1, 3.7, 7));
        records.add(new WeekWeightGain(33, 9.7, 14.2, 5.3, 9.2, 8.7, 12.5, 3.8, 7.2));
        records.add(new WeekWeightGain(34, 10.1, 14.8, 5.5, 9.5, 9, 13, 4, 7.5));
        records.add(new WeekWeightGain(35, 10.5, 15.3, 5.7, 9.8, 9.4, 13.5, 4.2, 7.8));
        records.add(new WeekWeightGain(36, 11, 15.9, 5.9, 10.1, 9.8, 14, 4.3, 8));
        records.add(new WeekWeightGain(37, 11.4, 16.5, 6.2, 10.4, 10.2, 14.4, 4.5, 8.3));
        records.add(new WeekWeightGain(38, 11.8, 17, 6.4, 10.7, 10.6, 14.9, 4.7, 8.5));
        records.add(new WeekWeightGain(39, 12.2, 17.6, 6.6, 11, 10.9, 15.4, 4.8, 8.8));
        records.add(new WeekWeightGain(40, 12.7, 18.1, 6.8, 11.3, 11.3, 15.9, 5, 9.1));
    }

    private void loadBmiCategories() {
        if(categories != null) return;

        categories = new ArrayList();

        categories.add(new BmiWeightRange(BodyWeightCategory.UnderWeight, Double.MIN_VALUE, 18.49));
        categories.add(new BmiWeightRange(BodyWeightCategory.Normal, 18.5, 24.99));
        categories.add(new BmiWeightRange(BodyWeightCategory.OverWeight, 25, 29.99));
        categories.add(new BmiWeightRange(BodyWeightCategory.Obese, 30, Double.MAX_VALUE));
    }

    private List<WeekWeightGainRange> createWeightGainTableFor(BodyWeightCategory category, WeightUnit weightUnit) {
        List<WeekWeightGainRange> result = new ArrayList<>();
        for (WeekWeightGain weekWeightGain : records) {
            result.add(weekWeightGain.getWeekWeightGainRangeFor(category, weightUnit));
        }
        return result;
    }

    public List<WeekWeightGainRange> getWeightGainTableFor(BodyWeightCategory category, WeightUnit weightUnit) {
        if(weightUnit == WeightUnit.kg) {
            if (category == BodyWeightCategory.UnderWeight) {
                if (weightGainTableForUnderWeight == null)
                    weightGainTableForUnderWeight = createWeightGainTableFor(BodyWeightCategory.UnderWeight, weightUnit);
                return weightGainTableForUnderWeight;
            }
            if (category == BodyWeightCategory.Normal) {
                if (weightGainTableForNormal == null)
                    weightGainTableForNormal = createWeightGainTableFor(BodyWeightCategory.Normal, weightUnit);
                return weightGainTableForNormal;
            }
            if (category == BodyWeightCategory.OverWeight) {
                if (weightGainTableForOverWeight == null)
                    weightGainTableForOverWeight = createWeightGainTableFor(BodyWeightCategory.OverWeight, weightUnit);
                return weightGainTableForOverWeight;
            }
            if (category == BodyWeightCategory.Obese) {
                if (weightGainTableForObese == null)
                    weightGainTableForObese = createWeightGainTableFor(BodyWeightCategory.Obese, weightUnit);
                return weightGainTableForObese;
            }
        } else {
            if (category == BodyWeightCategory.UnderWeight) {
                if (weightGainTableForUnderWeightInPounds == null)
                    weightGainTableForUnderWeightInPounds = createWeightGainTableFor(BodyWeightCategory.UnderWeight, weightUnit);
                return weightGainTableForUnderWeightInPounds;
            }
            if (category == BodyWeightCategory.Normal) {
                if (weightGainTableForNormalInPounds == null)
                    weightGainTableForNormalInPounds = createWeightGainTableFor(BodyWeightCategory.Normal, weightUnit);
                return weightGainTableForNormalInPounds;
            }
            if (category == BodyWeightCategory.OverWeight) {
                if (weightGainTableForOverWeightInPounds == null)
                    weightGainTableForOverWeightInPounds = createWeightGainTableFor(BodyWeightCategory.OverWeight, weightUnit);
                return weightGainTableForOverWeightInPounds;
            }
            if (category == BodyWeightCategory.Obese) {
                if (weightGainTableForObeseInPounds == null)
                    weightGainTableForObeseInPounds = createWeightGainTableFor(BodyWeightCategory.Obese, weightUnit);
                return weightGainTableForObeseInPounds;
            }
        }
        return null;
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
