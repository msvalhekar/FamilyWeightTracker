package com.mk.familyweighttracker.Models;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class MinimalUser {
    private int mId;
    private String mName;
    private String mImagePath;

    public MinimalUser(User user)
    {
        mId = user.getId();
        mName = user.getName();
        mImagePath = user.getImagePath();
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
