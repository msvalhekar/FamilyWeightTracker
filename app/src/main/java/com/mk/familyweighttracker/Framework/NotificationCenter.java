package com.mk.familyweighttracker.Framework;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.mk.familyweighttracker.Activities.SplashActivity;
import com.mk.familyweighttracker.Models.PushNotification;
import com.mk.familyweighttracker.R;

/**
 * Created by mvalhekar on 25-10-2016.
 */
public class NotificationCenter {

    public static void showNotification(PushNotification pushNotification) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(pushNotification.context)
                        .setContentTitle(pushNotification.title)
                        .setSmallIcon(R.drawable.launcher)
                        .setContentText(pushNotification.message)
                        .setAutoCancel(true);

        Intent intent = new Intent(pushNotification.context, SplashActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(pushNotification.context);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(pushNotification.requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = ((NotificationManager) pushNotification.context.getSystemService(Context.NOTIFICATION_SERVICE));
        manager.notify(pushNotification.requestCode, builder.build());
    }
}
