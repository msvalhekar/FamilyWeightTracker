package com.mk.familyweighttracker.Handlers.FirebaseNotification;

import com.mk.familyweighttracker.Framework.Constants;

import org.json.JSONObject;

/**
 * Created by mvalhekar on 19-01-2017.
 */

public class DeliveryDueDateNotificationHandler implements IFirebaseNotificationHandler {
    public boolean canHandle(String notificationType) {
        return notificationType.equals(Constants.FirebaseNotificationHandler.DeliveryDue);
    }

    public void handleRequest(JSONObject jsonData) {

    }
}
