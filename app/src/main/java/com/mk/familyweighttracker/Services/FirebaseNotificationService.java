package com.mk.familyweighttracker.Services;

import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.SystemInformation;
import com.mk.familyweighttracker.Handlers.FirebaseNotification.FirebaseNotificationHandlerFactory;
import com.mk.familyweighttracker.Handlers.FirebaseNotification.IFirebaseNotificationHandler;
import com.mk.familyweighttracker.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by mvalhekar on 16-01-2017.
 */
public class FirebaseNotificationService {
    static SimpleDateFormat ymdDateFormat = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat dobDateFormat = new SimpleDateFormat("MMdd");

    public static void deleteCurrentToken() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                FirebaseInstanceId.getInstance().getToken();
            }
        }.execute();
    }

    public static void registerTopics() {

        if(FirebaseInstanceId.getInstance().getToken() == null)
            return;

        FirebaseMessaging.getInstance().subscribeToTopic(SystemInformation.getAppVersionName());

        List<User> users = new UserService().getAll();
        for (User user : users) {
            if(user.isPregnant()) {
                String dueDateTopic = String.format(Constants.FirebaseNotificationTopic.DeliveryDueDateFormat, ymdDateFormat.format(user.deliveryDueDate));
                FirebaseMessaging.getInstance().subscribeToTopic(dueDateTopic);

                if(user.dateOfBirth != null) {
                    String dobTopic = String.format(Constants.FirebaseNotificationTopic.BirthDateFormat, dobDateFormat.format(user.dateOfBirth));
                    FirebaseMessaging.getInstance().subscribeToTopic(dobTopic);
                }
            } else { // infant
                String dobTopic = String.format(Constants.FirebaseNotificationTopic.InfantBirthDateFormat, ymdDateFormat.format(user.dateOfBirth));
                FirebaseMessaging.getInstance().subscribeToTopic(dobTopic);
            }
        }
    }

    public static void handleNotification(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() == 0)
            return;

        try {
            Map<String, String> data = remoteMessage.getData();

            IFirebaseNotificationHandler.NotificationData notificationData = new IFirebaseNotificationHandler.NotificationData(data);

            IFirebaseNotificationHandler handler = FirebaseNotificationHandlerFactory.getHandlerFor(notificationData.type);
            handler.handleRequest(notificationData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
