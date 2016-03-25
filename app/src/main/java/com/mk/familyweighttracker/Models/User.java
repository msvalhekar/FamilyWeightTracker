package com.mk.familyweighttracker.Models;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class User {
    private int mId;
    private String mName;
    private double mWeight;
    private double mHeight;
    private String mImagePath;

    public User(int id, String name, double weight, double height, String imagePath)
    {
        mId = id;
        mName = name;
        mWeight = weight;
        mHeight = height;
        mImagePath = imagePath;
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
}
