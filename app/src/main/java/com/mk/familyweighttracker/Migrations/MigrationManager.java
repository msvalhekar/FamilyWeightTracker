package com.mk.familyweighttracker.Migrations;

import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.PreferenceHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mvalhekar on 24-11-2016.
 */
public class MigrationManager {
    // check if savedCurrentMigration preference exists
    //    if no, then create one with 0
    // iterate through all migrations
    //    check if current migration required i.e. currentMigration < savedCurrentMigration
    //    if yes, then run migration
    //            and update savedCurrentMigration number in preference to current migration

    public void migrate() {
        int lastRunMig = getLastRunMigrationCode();
        if(lastRunMig == 0)
            setLastRunMigrationCode(0);

        for (VersionDataMigration migration: getMigrations()) {
            if(getLastRunMigrationCode() < migration.getVersionCode()) {
                migration.migrate();
                setLastRunMigrationCode(migration.getVersionCode());
            }
        }
    }

    private int getLastRunMigrationCode(){
        return PreferenceHelper.getInt(Constants.SharedPreference.LastRunMigration, 0);
    }

    private void setLastRunMigrationCode(int lastMigCode){
        PreferenceHelper.putInt(Constants.SharedPreference.LastRunMigration, lastMigCode);
    }

    private List<VersionDataMigration> getMigrations() {
        return Arrays.asList(
                new Version131DataMigration(),
                new Version141DataMigration()
        );
    }
}
