package com.mk.familyweighttracker.Migrations;

/**
 * Created by mvalhekar on 24-11-2016.
 */
public abstract class VersionDataMigration {
    abstract int getVersionCode();
    abstract int migrate();
}
