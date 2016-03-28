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
    private long mId;
    private String mName;
    private String mImagePath;
    private List<UserReading> mReadings;

    public User(long id, String name, String imagePath)
    {
        mId = id;
        mName = name;
        mImagePath = imagePath;
        mReadings = new ArrayList<>();
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public double getWeight() {
        if(mReadings.size() > 0)
            return mReadings.get(0).Weight;
        return 0;
    }

    public double getHeight() {
        if(mReadings.size() > 0)
            return mReadings.get(0).Height;
        return 0;
    }

    private double getHeightInMeter() {
        return getHeight() / 100.0;
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
