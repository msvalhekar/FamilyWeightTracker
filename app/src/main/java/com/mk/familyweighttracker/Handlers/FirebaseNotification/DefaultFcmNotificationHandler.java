package com.mk.familyweighttracker.Handlers.FirebaseNotification;

import org.json.JSONObject;

public class DefaultFcmNotificationHandler implements IFirebaseNotificationHandler {

    @Override
    public boolean canHandle(String notificationType) {
        return false;
    }

    @Override
    public void handleRequest(JSONObject jsonData) { }
}
