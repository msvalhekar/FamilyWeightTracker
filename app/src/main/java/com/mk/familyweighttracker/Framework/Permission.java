package com.mk.familyweighttracker.Framework;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mvalhekar on 25-11-2016.
 */
public enum Permission {
    Camera,
    Storage;

    @Override
    public String toString() {
        switch (this) {
            case Camera: return Manifest.permission.CAMERA;
            case Storage: return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        return super.toString();
    }

    public static List<Permission> getAllPermissions() {
        return Arrays.asList(Storage);
    }
}
