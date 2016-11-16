package com.mk.familyweighttracker.Framework;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.mk.familyweighttracker.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by mvalhekar on 16-05-2016.
 */
public class LogHelper {
    private static final int BUFFER = 2048;

    public static void sendLogEmail(final String customText) {
        LogHelper.createLogFile();

        final String logFilePath = StorageUtility.getZippedLogFilePath();
        File file = new File(logFilePath);

        if (!file.exists())
            return;

        final Activity currentActivity = TrackerApplication.getCurrentActivity();
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder emailTextBuilder = LogHelper.getSystemInformation();
                    emailTextBuilder.append(customText);

                    Intent emailIntent = new Intent(Intent.ACTION_SEND)
                            .setType("plain/text")
                            .putExtra(Intent.EXTRA_EMAIL, "mvalhekar@spiderlogic.com")
                            .putExtra(Intent.EXTRA_SUBJECT, "Device Log")
                            .putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + logFilePath))
                            .putExtra(Intent.EXTRA_TEXT, emailTextBuilder.toString());

                    currentActivity.startActivity(Intent.createChooser(emailIntent, currentActivity.getString(R.string.log_helper_email_chooser_title)));

                } catch (Throwable t) {
                    Toast.makeText(currentActivity, R.string.error_log_helper_no_email_client_message, Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            }
        });
    }

    public static void createLogFile() {
        final String DASH_LINE = "----------------------------------------\n\n";

        try {
            String logFilePath = StorageUtility.getLogFilePath();
            FileWriter fileWriter = new FileWriter(new File(logFilePath));

            fileWriter.write(getSystemInformation().toString());
            fileWriter.write(DASH_LINE);

            appendSystemLog(fileWriter);
            fileWriter.write(DASH_LINE);

            appendCustomLog(fileWriter);
            fileWriter.write(DASH_LINE);

            fileWriter.flush();
            fileWriter.close();

            zipTrackerLogFile(logFilePath);

        } catch (IOException e) {
            //Utility.logException(Constants.LogTag.SystemInformation, e);
        }
    }

    public static StringBuilder getSystemInformation() {
        LinkedHashMap<String, String> systemInformation = SystemInformation.getSystemInformation();
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> entry : systemInformation.entrySet()) {
            builder.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
        }

        return builder;
    }

    private static void appendSystemLog(FileWriter fileWriter) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"logcat", "-d", "-v", "time", "ActivityManager:W", "FWT:I", "*:S"});

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                //if (line.matches("(.*)System.err(.*)") || line.matches("(.*)SQLiteDatabase(.*)")) {
                fileWriter.write(String.format("%s\n", line));
                //}
            }
            bufferedReader.close();
        } catch (IOException e) {
            //Utility.logException(Constants.LogTag.SystemInformation, e);
        }
    }

    private static void appendCustomLog(FileWriter fileWriter) {
        File[] logFiles = new File(StorageUtility.getLogDirectory()).listFiles();
        String data;

        if(logFiles == null) return;

        try {
            for (File logFile : logFiles) {
                BufferedReader reader = new BufferedReader(new FileReader(logFile));

                while ((data = reader.readLine()) != null) {
                    fileWriter.write(String.format("%s\n", data));
                }
                reader.close();
            }
        } catch (IOException e) {
            //Utility.logException(Constants.LogTag.SystemInformation, e);
        }
    }

    private static void zipTrackerLogFile(String logFilePath) throws IOException {
        BufferedInputStream bufferedInputStream;

        FileOutputStream destinationStream = new FileOutputStream(StorageUtility.getZippedLogFilePath());
        ZipOutputStream zipDestinationStream = new ZipOutputStream(new BufferedOutputStream(destinationStream));
        byte data[] = new byte[BUFFER];

        bufferedInputStream = new BufferedInputStream(new FileInputStream(logFilePath), BUFFER);
        ZipEntry entry = new ZipEntry(logFilePath.substring(logFilePath.lastIndexOf("/") + 1));
        zipDestinationStream.putNextEntry(entry);

        int count;
        while ((count = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
            zipDestinationStream.write(data, 0, count);
        }

        bufferedInputStream.close();
        zipDestinationStream.close();
    }
}
