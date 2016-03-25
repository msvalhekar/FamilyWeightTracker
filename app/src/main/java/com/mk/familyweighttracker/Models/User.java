package com.mk.familyweighttracker.Models;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class User {
    private int mId;
    private String mName;
    private String mImagePath;

    public User(int id, String name, String imagePath)
    {
        mId = id;
        mName = name;
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
}
