package com.mk.familyweighttracker.Services;

/**
 * Created by mvalhekar on 16-01-2017.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {
    public void onTokenRefresh() {
        //String token = FirebaseInstanceId.getInstance().getToken();

        FirebaseNotificationService.registerTopics();
    }
}
