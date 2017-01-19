package com.mk.familyweighttracker.Services;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mvalhekar on 16-01-2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public void onMessageReceived(RemoteMessage remoteMessage) {
        FirebaseNotificationService.handleNotification(remoteMessage);
    }
}
