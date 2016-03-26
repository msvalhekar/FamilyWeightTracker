package com.mk.familyweighttracker.Services;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.Repositories.PregnancyRepository;

import java.util.List;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class PregnancyService {

    private PregnancyRepository repository;

    public PregnancyService()
    {
        repository = new PregnancyRepository();
    }

    public List<WeekWeightGainRange> getWeightGainTableFor(BodyWeightCategory category) {
        return repository.getWeightGainTableFor(category);
    }
}
