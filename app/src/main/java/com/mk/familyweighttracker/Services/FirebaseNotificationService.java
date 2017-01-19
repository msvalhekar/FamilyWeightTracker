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
        if (remoteMessage.getData().size() > 0) {
            try {
                Map<String, String> data = remoteMessage.getData();
                JSONObject json = new JSONObject(data);
                String notificationType = json.getString("type");

                IFirebaseNotificationHandler handler = FirebaseNotificationHandlerFactory.getHandlerFor(notificationType);
                handler.handleRequest(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //// Check if message contains a notification payload.
        //if (remoteMessage.getNotification() != null) {
        //    //Log.d(Constants.LogTag.App, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        //}

        //// Also if you intend on generating your own notifications as a result of a received FCM
        //// message, here is where that should be initiated. See sendNotification method below.
    }
}
