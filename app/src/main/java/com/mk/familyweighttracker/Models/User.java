package com.mk.familyweighttracker.Models;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Services.PregnancyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class User {
    public static final double POUNDS_PER_KILOGRAM = 2.20462;
    public static final double INCHES_PER_METER = 39.3701;
    public static final int CENTIMETERS_PER_METER = 100;

    private long mId;
    public String name;
    public byte[] imageBytes;
    public boolean isMale;
    public Date dateOfBirth;
    public TrackingPeriod trackingPeriod;
    public WeightUnit weightUnit;
    public HeightUnit heightUnit;
    public boolean enableReminder;
    public int reminderDay;
    public int reminderHour;
    public int reminderMinute;
    private List<UserReading> mReadings;

    public User(long id) {
        mId = id;
        mReadings = new ArrayList<>();
    }

    public long getId() {
        return mId;
    }

    public double getWeight() {
        if(mReadings.size() > 0)
            return getReadings(true).get(0).Weight;
        return 0;
    }

    public double getHeight() {
        if(mReadings.size() > 0)
            return getReadings(true).get(0).Height;
        return 0;
    }

    private double getWeightInKg() {
        double divideBy = 1;
        if(weightUnit == WeightUnit.lb)
            divideBy = POUNDS_PER_KILOGRAM;
        return getWeight() / divideBy;
    }

    private double getHeightInMeter() {
        double divideBy = 1;
        if( heightUnit == HeightUnit.cm)
            divideBy = CENTIMETERS_PER_METER;
        else if( heightUnit == HeightUnit.inch)
            divideBy = INCHES_PER_METER;
        return getHeight() / divideBy;
    }

    public void addReading(long sequence, double weight, int height, Date takenOn)
    {
        UserReading reading = new UserReading(mId, sequence, weight, height, takenOn);
        mReadings.add(reading);
    }

    public List<UserReading> getReadings(final boolean ascending) {
        Collections.sort(mReadings, new Comparator<UserReading>() {
            @Override
            public int compare(UserReading lhs, UserReading rhs) {
                return (int) (lhs.Sequence - rhs.Sequence) * (ascending ? 1 : -1);
            }
        });
        return mReadings;
    }

    public UserReading getLatestReading() {
        if(mReadings != null && mReadings.size() > 0)
            return getReadings(false).get(0);
        return null;
    }

    public UserReading findReadingBefore(long sequence) {
        if(mReadings != null && mReadings.size() > 1) {
            List<UserReading> readings = getReadings(true);
            for(int i=0; i<readings.size(); i++) {
                if(readings.get(i).Sequence == sequence) {
                    if(i == 0) return null;
                    return readings.get(i-1);
                }
            }
        }
        return null;
    }

    public double getBmi() {
        return new PregnancyService().calculateBmi(getHeightInMeter(), getWeightInKg());
    }

    public BodyWeightCategory getWeightCategory() {
        return new PregnancyService().getWeightCategory(getBmi());
    }
}
