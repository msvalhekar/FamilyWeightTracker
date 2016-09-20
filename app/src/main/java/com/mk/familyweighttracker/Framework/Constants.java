package com.mk.familyweighttracker.Framework;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class Constants {

    public static final String DB_NAME = "MkWeighTracker.db";
    public static final String APP_BACKUP_KEY = "MkWeighTracker";
    public static final String SHARED_PREF_KEY = "com.mk.familyweighttracker.MkWeighTracker";
    public static final String PLAY_STORE_APP_SEARCH_URL = "http://play.google.com/store/apps/details?id=%s";

    public static final int SPLASH_SCREEN_TIMEOUT_SECONDS = 2500;

    public static class AppDirectory {
        public static final String LogDirectory = "sysLog";
        public static final String ImagesDirectory = "imgs";
    }

    public static class SharedPreference {
        public static final String SelectedBackgroundAudio = "SelectedBackgroundAudio";
    }

    public static class Activities {
        public static final String HomeActivity = "HomeActivity";
        public static final String UsersListActivity = "UsersListActivity";
        public static final String TrackerHelpActivity = "TrackerHelpActivity";
        public static final String UserMediaActivity = "UserMediaActivity";
        public static final String UserSlideshowActivity = "UserSlideshowActivity";
        public static final String AddReadingActivity = "AddReadingActivity";
        public static final String AddPregnantUserActivity = "AddPregnantUserActivity";
        public static final String AddFirstReadingActivity = "AddFirstReadingActivity";
    }

    public static class ExtraArg {
        public static final String USER_ID = "user_id";
        public static final String IS_DATA_CHANGED = "IsDataChanged";
        public static final String EDIT_READING_ID = "EditReadingId";
    }

    public static class RequestCode {
        public static final int ADD_USER = 1;
        public static final int EDIT_USER = 2;
        public static final int USER_DATA_CHANGED = 10;
        public static final int ADD_READING = 11;
        public static final int EDIT_READING = 12;
        public static final int USER_IMAGE_LOAD = 21;
        public static final int USER_IMAGE_CROP = 22;
        public static final int READING_IMAGE_LOAD = 31;
        public static final int READING_IMAGE_CROP = 32;
        public static final int MEDIA_BROWSE_AUDIO = 41;
    }

    public static class LogTag {
        public static final String App = "FWT";
        public static final String User = App + "User";
        public static final String BootReceiver = App + "BootReceiver";
    }

    public static class AnalyticsCategories {
        public static final String Activity = "Activity";
        public static final String Fragment = "Fragment";
        public static final String BackgroundAction = "BackgroundAction";
        public static final String Action = "Action";
    }

    public static class AnalyticsActions {
        public static final String UserDetailsProfile = "%s -> DetailsProfile";
        public static final String UserDetailsChart = "%s -> DetailsChart";
        public static final String UserDetailsMedia = "%s -> DetailsMedia";
        public static final String UserDetailsRecords = "%s -> DetailsRecords";
        public static final String FirstReadingAdded = "%s -> FirstReadingAdded";
        public static final String FirstReadingEdited = "%s -> FirstReadingEdited";
        public static final String ReadingAdded = "%s -> ReadingAdded %d";
        public static final String ReadingEdited = "%s -> ReadingEdited %d";
        public static final String ReadingDeleted = "%s -> ReadingDeleted %d";
        public static final String UserDetailsLoaded = "%s -> UserDetailsLoaded";
        public static final String UserDeleted = "%s -> UserDeleted";
        public static final String ShowUserReadingHelp = "%s -> ShowUserReadingHelp";
        public static final String UserAdded = "UserAdded : %s";
        public static final String UserEdited = "UserEdited : %s";
    }

    public static class AnalyticsEvents {
        public static final String HomeActivityCreated = "HomeActivityCreated";
        public static final String AddFirstReading = "AddFirstReading";
        public static final String AddReading = "AddReading";
        public static final String DeleteReading = "DeleteReading";
        public static final String UserDetailsActivity = "UserDetailsActivity";
        public static final String UserDetailsProfile = "UserDetailsProfile";
        public static final String UserDetailsChart = "UserDetailsChart";
        public static final String UserDetailsRecords = "UserDetailsRecords";
        public static final String UserDetailsMedia = "UserDetailsMedia";
        public static final String UserAdded = "UserAdded";
        public static final String UserDelete = "UserDelete";
        public static final String UserReadingHelp = "UserReadingHelp";
    }
}
