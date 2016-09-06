package com.mk.familyweighttracker.Framework;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mvalhekar on 20-08-2016.
 */
//todo: https://developer.android.com/training/backup/autosyncapi.html
//todo: https://developer.android.com/guide/topics/data/backup.html

public class TrackerBackupAgent extends BackupAgent {
    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        // Get the oldState input stream
        Log.i(Constants.LogTag.App, "TrackerBackupAgent.onBackup");
        FileInputStream fileInputStream = new FileInputStream(oldState.getFileDescriptor());
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);

        try {
            File currentDb = TrackerApplication.getApp().getDatabasePath(Constants.DB_NAME);
            // Get the last modified timestamp from the state file and data file
            long dbFileLastModifiedTime = currentDb.lastModified();
            long lastBackedTime = 0;

            try {
                lastBackedTime = dataInputStream.readLong();
            } catch (EOFException e) {
                e.printStackTrace();
            }

            if (lastBackedTime != dbFileLastModifiedTime) {
                backup(data, newState, dbFileLastModifiedTime);
            }
        } catch (IOException e) {
            // Unable to read state file... be safe and do a backup
            Log.e(Constants.LogTag.App, e.getMessage());
        }
    }

    private void backup(BackupDataOutput data, ParcelFileDescriptor newState, long dbFileLastModifiedTime) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        BackupRestoreDb backupRestoreDb = new BackupRestoreDb(3, dbFileLastModifiedTime);

        try {
            JSONObject jsonObject = backupRestoreDb.getDatabaseJSON();
            Log.i(Constants.LogTag.App, jsonObject.toString());
            dataOutputStream.writeBytes(jsonObject.toString());

            byte[] buffer = outputStream.toByteArray();
            data.writeEntityHeader(Constants.APP_BACKUP_KEY, buffer.length);
            data.writeEntityData(buffer, buffer.length);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(Constants.LogTag.App, e.getMessage());
        } finally {
            dataOutputStream.flush();
            outputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        }

        writeRepresentation(newState, dbFileLastModifiedTime);
    }

    private void writeRepresentation(ParcelFileDescriptor newState, long dbFileLastModifiedTime) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(newState.getFileDescriptor());
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        dataOutputStream.writeLong(dbFileLastModifiedTime);

        dataOutputStream.flush();
        dataOutputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) {
        Log.i(Constants.LogTag.App, "TrackerBackupAgent.onRestore");

        try {
            while(data.readNextHeader()) {
                if(Constants.APP_BACKUP_KEY.equals(data.getKey())) {
                    int dataSize = data.getDataSize();
                    byte[] buffer = new byte[dataSize];
                    data.readEntityData(buffer, 0, dataSize);

                    try {
                        new BackupRestoreDb().saveToDbFrom(buffer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
