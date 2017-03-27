package com.mk.familyweighttracker.Framework;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class Constants {

    public static final String DB_NAME = "MkWeighTracker.db";
    public static final String APP_BACKUP_KEY = "MkWeighTracker";
    public static final String SHARED_PREF_KEY = "com.mk.familyweighttracker.MkWeighTracker";

    public static final String PLAY_STORE_MARKET_SEARCH_URL = "market://details?id=%s";
    public static final String PLAY_STORE_APP_SEARCH_URL = "http://play.google.com/store/apps/details?id=%s";

    public static class FirebaseNotificationTopic {
        public static final String DeliveryDueDateFormat = "DELIVERY_DUE_%s";
        public static final String BirthDateFormat = "DOB_%s";
        public static final String InfantBirthDateFormat = "INFANT_DOB_%s";
    }

    public static class FirebaseNotificationHandler {
        public static final String DeliveryDue = "DELIVERY_DUE";
        public static final String BirthDate = "DOB";
        public static final String InfantBirthDate = "INFANT_DOB";
    }

    public static class Settings {
        public static final int USER_NOTE_LENGTH = 250;
        public static final int SPLASH_SCREEN_TIMEOUT_SECONDS = 1800;
        public static final int CHECK_MARKET_APP_UPDATE_AFTER_DAYS = 30;
    }

    public static class AppDirectory {
        public static final String LogDirectory = "sysLog";
        public static final String ImagesDirectory = "imgs";
    }

    public static class SharedPreference {
        public static final String SelectedBackgroundAudio = "SelectedBackgroundAudio";
        public static final String AppMarketLastUpdateCheckedOn = "AppMarketLastUpdateCheckedOn";
        public static final String LastRunMigration = "LastRunMigration";
        public static final String SelectedLanguage = "SelectedLanguage";
        public static final String WhatsNewShownFor = "WhatsNewShownFor";
    }

    public static class Activities {
        public static final String HomeActivity = "HomeActivity";
        public static final String UsersListActivity = "UsersListActivity";
        public static final String TrackerHelpActivity = "TrackerHelpActivity";
        public static final String AppFeedbackActivity = "AppFeedbackActivity";

        public static final String PregnantSlideshowActivity = "PregnantSlideshowActivity";
        public static final String PregnantAddReadingActivity = "PregnantAddReadingActivity";
        public static final String PregnantAddUserActivity = "PregnantAddUserActivity";
        public static final String CollageTemplateChooserActivity = "CollageTemplateChooserActivity";

        public static final String InfantSlideshowActivity = "InfantSlideshowActivity";
        public static final String InfantAddReadingActivity = "InfantAddReadingActivity";
        public static final String InfantAddUserActivity = "InfantAddUserActivity";
    }

    public static class ExtraArg {
        public static final String USER_ID = "user_id";
        public static final String USER_TYPE = "UserType";
        public static final String ADD_READING_TYPE = "AddReadingType";
        public static final String EDIT_READING_ID = "EditReadingId";
        public static final String PROMPT_FOR_UPGRADE = "PromptForUpgrade";
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
        public static final String UserDetailsProfile = "%s (%s) -> DetailsProfile";
        public static final String UserDetailsChart = "%s (%s) -> DetailsChart";
        public static final String UserDetailsMedia = "%s (%s) -> DetailsMedia";
        public static final String UserDetailsRecords = "%s (%s) -> DetailsRecords";
        public static final String ReadingAdded = "%s (%s) -> ReadingAdded %d";
        public static final String ReadingEdited = "%s (%s) -> ReadingEdited %d";
        public static final String ReadingDeleted = "%s (%s) -> ReadingDeleted %d";
        public static final String UserDetailsLoaded = "%s (%s) -> UserDetailsLoaded";
        public static final String UserDeleted = "%s (%s) -> UserDeleted";
        public static final String ShowUserReadingHelp = "%s (%s) -> ShowUserReadingHelp";
        public static final String UserAdded = "UserAdded (%s) : %s";
        public static final String UserEdited = "UserEdited (%s) : %s";
    }

    public static class AnalyticsEvents {
        public static final String PregnantAddReading = "PregnantAddReading";
        public static final String PregnantDeleteReading = "PregnantDeleteReading";
        public static final String PregnantDetailsActivity = "PregnantDetailsActivity";
        public static final String PregnantDetailsProfile = "PregnantDetailsProfile";
        public static final String PregnantDetailsChart = "PregnantDetailsChart";
        public static final String PregnantDetailsRecords = "PregnantDetailsRecords";
        public static final String PregnantDetailsMedia = "PregnantDetailsMedia";
        public static final String PregnantAdded = "PregnantAdded";
        public static final String PregnantDelete = "PregnantDelete";
        public static final String PregnantReadingHelp = "PregnantReadingHelp";

        public static final String InfantAddReading = "InfantAddReading";
        public static final String InfantDeleteReading = "InfantDeleteReading";
        public static final String InfantDetailsActivity = "InfantDetailsActivity";
        public static final String InfantDetailsProfile = "InfantDetailsProfile";
        public static final String InfantDetailsChart = "InfantDetailsChart";
        public static final String InfantDetailsRecords = "InfantDetailsRecords";
        public static final String InfantDetailsMedia = "InfantDetailsMedia";
        public static final String InfantDelete = "InfantDelete";
        public static final String InfantAdded = "InfantAdded";
        public static final String InfantReadingHelp = "InfantReadingHelp";
    }
}
