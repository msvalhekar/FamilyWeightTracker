package com.mk.familyweighttracker.Handlers.FirebaseNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by mvalhekar on 19-01-2017.
 */

public interface IFirebaseNotificationHandler {
    boolean canHandle(String notificationType);
    void handleRequest(NotificationData notificationData);

    class NotificationData {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        public Date datetime;
        public String type;
        public String birthdayWishMessage;

        public NotificationData(Map<String, String> map) {
            try {
                datetime = dateFormat.parse(map.get("time"));
                type = map.get("type");
                birthdayWishMessage = map.get("birthday_wish_message");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

