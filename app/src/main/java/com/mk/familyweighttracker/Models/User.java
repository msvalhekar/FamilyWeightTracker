package com.mk.familyweighttracker.Models;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class User {
    private int mId;
    private String mName;
    private double mWeight;
    private double mHeight;
    private String mImagePath;
    private List<UserReading> mReadings;

    public User(int id, String name, double weight, double height, String imagePath)
    {
        mId = id;
        mName = name;
        mWeight = weight;
        mHeight = height;
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
        return mWeight;
    }

    public double getHeight() {
        return mHeight;
    }

    public void addReading(double weight, double height, Date takenOn)
    {
        UserReading reading = new UserReading(mId, mReadings.size(), weight, height, takenOn);
        mReadings.add(reading);
    }

    public List<UserReading> getReadings() {
        return mReadings;
    }
}
