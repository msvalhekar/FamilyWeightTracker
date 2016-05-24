package com.mk.familyweighttracker.Framework;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.AddReadingActivity;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.HomeActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

/**
 * Created by mvalhekar on 16-05-2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private Context _context;
    @Override
    public void onReceive(Context context, Intent intent) {
        _context = context;

        long userId = intent.getLongExtra(UserDetailActivity.ARG_USER_ID, 0);
        String userName = intent.getStringExtra(UserDetailActivity.ARG_USER_NAME);
        //Toast.makeText(context, userName, Toast.LENGTH_LONG).show();
        sendReminderNotification(userId, userName);
    }

    private void sendReminderNotification(long userId, String userName) {

        //User user = new UserService().get(userId);
        //if(user == null || !user.enableReminder) return;

        String titleMessage = userName + " - Record Weight";
        String textMessage = "";
        android.support.v7.app.NotificationCompat.Builder builder =
                (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(_context)
                .setContentTitle(titleMessage)
                .setSmallIcon(R.drawable.edit_icon)
                .setContentText(textMessage)
                .setAutoCancel(true);

        Intent intent = new Intent(_context, HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent((int)userId, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = ((NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE));
        manager.notify((int)userId, builder.build());
    }

}
