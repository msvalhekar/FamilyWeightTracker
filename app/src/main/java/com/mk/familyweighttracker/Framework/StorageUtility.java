package com.mk.familyweighttracker.Framework;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mvalhekar on 15-09-2016.
 */
public class StorageUtility {
    private static String getStoragePath() {
        return TrackerApplication.getApp().getExternalFilesDir(null).getPath();
    }

    public static File getFile(String relativePath) {
        return new File(getStoragePath(), relativePath);
    }

    public static String getLogDirectory() {
        return getPath(getStoragePath(), Constants.AppDirectory.LogDirectory);
    }

    public static String getImagesDirectory() {
        return getPath(getStoragePath(), Constants.AppDirectory.ImagesDirectory);
    }

    public static String getLogFilePath() {
        return getPath(getStoragePath(), "wTrackLog.txt");
    }

    public static String getZippedLogFilePath() {
        return getPath(getStoragePath(), "wTrackLogs.zip");
    }

    public static String getPath(String... paths) {
        if (paths.length == 0) return null;
        File file = new File(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            file = new File(file, paths[i]);
        }
        return file.getPath();
    }

    public static void createDirectories() {
        new File(getLogDirectory()).mkdir();
        new File(getImagesDirectory()).mkdir();
    }

    public static void deleteDirectories() {
        new File(getLogFilePath()).delete();
        new File(getImagesDirectory()).delete();
        new File(getZippedLogFilePath()).delete();
    }

    public static String getLogFilePattern() {
        return getPath(getLogDirectory(), "wTrackLog_%d.txt");
    }

    public static void deleteDirectory(File directory, boolean recursive) {
        final File[] files = directory.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.isDirectory() && recursive) {
                deleteDirectory(file, true);
            }
            file.delete();
        }
        directory.delete();
    }

    public static ArrayList<String> getFilesAtPath(String directoryPath) {
        ArrayList<String> allFiles = new ArrayList<>();

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            return allFiles;
        }

        File[] files = directory.listFiles();
        if (files == null)
            return allFiles;

        for (File f : files) {
            allFiles.add(f.getAbsolutePath());
        }
        return allFiles;
    }
}
