package com.mk.familyweighttracker.Framework;

import android.os.AsyncTask;

import com.mk.familyweighttracker.Migrations.MigrationManager;

/**
 * Created by mvalhekar on 24-11-2016.
 */
public class DataMigratorTask {

    public void execute() {
        new MigrationManager().migrate();
    }
}
