package com.mk.familyweighttracker.Services;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.MonthGrowthRange;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.Repositories.InfantRepository;
import com.mk.familyweighttracker.Repositories.PregnancyRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class PregnancyService {

    private PregnancyRepository pregnancyRepository = new PregnancyRepository();
    private InfantRepository infantRepository = new InfantRepository();

    public PregnancyService() {
    }

    public List<WeekWeightGainRange> getWeightGainTableFor(double baseWeight, BodyWeightCategory category, WeightUnit weightUnit, boolean forTwins) {
        List<WeekWeightGainRange> records = pregnancyRepository.createWeightGainTableFor(category, weightUnit, forTwins);

        List<WeekWeightGainRange> recordsToReturn = new ArrayList<>();
        for (WeekWeightGainRange record: records) {
            recordsToReturn.add(new WeekWeightGainRange(
                    record.WeekNumber,
                    record.MinimumWeight + baseWeight,
                    record.MaximumWeight + baseWeight));
        }
        return recordsToReturn;
    }

    public List<MonthGrowthRange> getMonthGrowthRangeTableFor(boolean forBoy, WeightUnit weightUnit, HeightUnit heightUnit, HeightUnit headCirUnit) {
        return infantRepository.createMonthGrowthRangeTableFor(forBoy, weightUnit, heightUnit, headCirUnit);
    }

    // BMI = (wt in kg) / squareOf(height in meters)
    public double calculateBmi(double heightInMeter, double weightInKg) {
        if(heightInMeter < 0) return 0;

        return weightInKg / Math.pow(heightInMeter, 2);
    }

    public BodyWeightCategory getWeightCategory(double bmi) {
        return pregnancyRepository.getWeightCategory(bmi);
    }
}
