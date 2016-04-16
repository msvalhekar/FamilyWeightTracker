package com.mk.familyweighttracker.Services;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.Repositories.PregnancyRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class PregnancyService {

    private PregnancyRepository repository = new PregnancyRepository();

    public PregnancyService() {
    }

    public List<WeekWeightGainRange> getWeightGainTableFor(double baseWeight, BodyWeightCategory category, WeightUnit weightUnit) {
        List<WeekWeightGainRange> records = repository.getWeightGainTableFor(category, weightUnit);

        List<WeekWeightGainRange> recordsToReturn = new ArrayList<>();
        for (WeekWeightGainRange record: records) {
            recordsToReturn.add(new WeekWeightGainRange(
                    record.WeekNumber,
                    record.MinimumWeight + baseWeight,
                    record.MaximumWeight + baseWeight));
        }
        return recordsToReturn;
    }

    // BMI = (wt in kg) / squareOf(height in meters)
    public double calculateBmi(double heightInMeter, double weightInKg) {
        if(heightInMeter < 0) return 0;

        return weightInKg / Math.pow(heightInMeter, 2);
    }

    public BodyWeightCategory getWeightCategory(double bmi) {
        return repository.getWeightCategory(bmi);
    }
}
