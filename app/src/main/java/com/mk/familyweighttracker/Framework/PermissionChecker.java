package com.mk.familyweighttracker.Framework;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 25-11-2016.
 */
public class PermissionChecker {
    private static PermissionChecker instance = new PermissionChecker();
    private static Object syncLock = new Object();
    private final int DeniedPermissionRequest = 0;

    private PermissionChecker() { }

    public static PermissionChecker getInstance(){
        synchronized (syncLock) {
            return instance;
        }
    }

    public void requestPermissions(Activity activity){
        List<Permission> deniedPermissions = getDeniedPermissions(activity);
        if(deniedPermissions.size() == 0) return;

        List<String> stringPermissions = new ArrayList<>();
        for (Permission deniedPermission : deniedPermissions) {
            stringPermissions.add(deniedPermission.toString());
        }

        ActivityCompat.requestPermissions(activity, stringPermissions.toArray(new String[stringPermissions.size()]), DeniedPermissionRequest);
    }

    private List<Permission> getDeniedPermissions(Activity activity) {
        List<Permission> allPermissions = Permission.getAllPermissions();

        List<Permission> deniedPermissions = new ArrayList<>();

        for (Permission permission : allPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission.toString()) == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(permission);
            }
        }

        return deniedPermissions;
    }
}
