package com.mk.familyweighttracker.Models;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class UserHeader {
    private long mId;
    private String mName;
    private byte[] mImageBytes;

    public UserHeader(long id, String name, byte[] imageBytes) {
        mId = id;
        mName = name;
        mImageBytes = imageBytes;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public byte[] getImageBytes() {
        return mImageBytes;
    }
}
