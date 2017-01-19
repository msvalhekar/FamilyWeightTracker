package com.mk.familyweighttracker.Handlers.FirebaseNotification;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

/**
 * Created by mvalhekar on 19-01-2017.
 */

public interface IFirebaseNotificationHandler {
    boolean canHandle(String notificationType);
    void handleRequest(JSONObject jsonData);
}

