package com.mk.familyweighttracker.Framework;

import android.util.Log;

import com.mk.familyweighttracker.DbModels.UserModel;
import com.mk.familyweighttracker.Services.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mvalhekar on 30-08-2016.
 */

public class BackupRestoreDb {
    public int dbVersion;
    public long lastModified;

    public BackupRestoreDb() { }

    public BackupRestoreDb(int dbVersion, long lastModifiedTime) {
        this.dbVersion = dbVersion;
        this.lastModified = lastModifiedTime;
    }

    public JSONObject getDatabaseJSON() throws JSONException {
        JSONObject dbJSON = new JSONObject()
                .put("dbVer", dbVersion)
                .put("lastModified", lastModified);

        JSONArray usersJSONArray = new JSONArray();
        for (UserModel user : UserModel.getAll()) {
            usersJSONArray.put(user.toJSON());
        }

        dbJSON.put("users", usersJSONArray);
        return dbJSON;
    }
//
//    private void mapFrom(byte[] inputData) throws JSONException {
//        JSONObject dbJSON = new JSONObject(inputData.toString());
//
//        dbVersion = dbJSON.getInt("dbVer");
//        lastModified = dbJSON.getLong("lastModified");
//    }

    public void saveToDbFrom(byte[] inputData) throws JSONException {
        cleanExistingData();

        JSONObject dbJSON = new JSONObject(inputData.toString());

        JSONArray usersJSONArray = dbJSON.getJSONArray("users");
        for (int i=0; i<usersJSONArray.length(); i++) {
            JSONObject userJSON = usersJSONArray.getJSONObject(i);
            UserModel.saveFrom(userJSON);

            try {
                new UserService().get(userJSON.getString("name")).resetReminder();
            } catch (Exception e) {
                Log.e(Constants.LogTag.App, e.getMessage());
            }
        }
    }

    private void cleanExistingData() {
        UserModel.deleteAll();
    }
}


