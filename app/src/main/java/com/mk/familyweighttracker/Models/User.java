package com.mk.familyweighttracker.Models;

import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Services.PregnancyService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class User {
    private int mId;
    private String mName;
    private double mWeightInKg;
    private double mHeightInCm;
    private String mImagePath;
    private List<UserReading> mReadings;

    public User(int id, String name, double weight, double height, String imagePath)
    {
        mId = id;
        mName = name;
        mWeightInKg = weight;
        mHeightInCm = height;
        mImagePath = imagePath;
        mReadings = new ArrayList<>();
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public double getWeight() {
        return mWeightInKg;
    }

    public double getHeight() {
        return mHeightInCm;
    }
    public double getHeightInMeter() {
        return mHeightInCm / 100.0;
    }

    public void addReading(double weight, double height, Date takenOn)
    {
        UserReading reading = new UserReading(mId, mReadings.size(), weight, height, takenOn);
        mReadings.add(reading);
    }

    public List<UserReading> getReadings() {
        return mReadings;
    }

    public double getBmi() {
        return new PregnancyService().calculateBmi(getHeightInMeter(), getWeight());
    }

    public BodyWeightCategory getWeightCategory() {
        return new PregnancyService().getWeightCategory(getBmi());
    }
}
