package com.mk.familyweighttracker.Framework;

import junit.runner.Version;

/**
 * Created by mvalhekar on 25-10-2016.
 */
public class AppVersion {
    int major;
    int minor;
    int revision;

    public AppVersion(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public static AppVersion parseFrom(String sVersion) {
        if(StringHelper.isNullOrEmpty(sVersion))
            return InvalidVersion();

        int major = 0;
        int minor = 0;
        int revision = 0;
        String[] versionParts = sVersion.split("\\.");

        if(versionParts.length == 1)
            major = Integer.decode(versionParts[0]);
        if(versionParts.length == 2) {
            major = Integer.decode(versionParts[0]);
            minor = Integer.decode(versionParts[1]);
        }
        if(versionParts.length == 3) {
            major = Integer.decode(versionParts[0]);
            minor = Integer.decode(versionParts[1]);
            revision = Integer.decode(versionParts[2]);
        }
        return new AppVersion(major, minor, revision);
    }

    private static AppVersion InvalidVersion(){
        return new AppVersion(0, 0, 0);
    }

    public int compareTo(AppVersion compareTo) {
        if(compareTo == null)
            return -1;

        if(this.major > compareTo.major)
            return 1;
        if(this.major < compareTo.major)
            return -1;

        // major are same for both
        if(this.minor > compareTo.minor)
            return 1;
        if(this.minor < compareTo.minor)
            return -1;

        // major and minor are same for both
        if(this.revision > compareTo.revision)
            return 1;
        if(this.revision < compareTo.revision)
            return -1;

        // major, minor, revision are same for both
        return 0;
    }
}
