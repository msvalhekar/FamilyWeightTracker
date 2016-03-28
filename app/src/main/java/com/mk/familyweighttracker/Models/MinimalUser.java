package com.mk.familyweighttracker.Models;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class MinimalUser {
    private long mId;
    private String mName;
    private String mImagePath;

    public MinimalUser(long id, String name, String imagePath) {
        mId = id;
        mName = name;
        mImagePath = imagePath;
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
}
