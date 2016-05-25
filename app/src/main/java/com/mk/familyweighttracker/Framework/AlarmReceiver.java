package com.mk.familyweighttracker.Framework;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.Activities.AddReadingActivity;
import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Activities.UsersListActivity;
import com.mk.familyweighttracker.HomeActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
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

        ActiveAndroid.initialize(context);

        try {
            sendReminderNotification(userId);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void sendReminderNotification(long userId) throws ClassNotFoundException {
        User user = new UserService().get(userId);
        if(user == null || !user.enableReminder) return;

        UserReading latestReading = user.getLatestReading();
        long nextSequence = latestReading != null ? latestReading.Sequence +1 : 0;
        String titleMessage = String.format("%s - Week %d Reading", user.name, nextSequence);
        String textMessage = "Touch to record pregnancy weight for current week.";
        android.support.v7.app.NotificationCompat.Builder builder =
                (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(_context)
                .setContentTitle(titleMessage)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentText(textMessage)
                .setAutoCancel(true);

//        Class className = Class.forName("com.mk.familyweighttracker.Activities.AddReadingActivity");
//        if(user.getLatestReading() == null)
//            className = Class.forName("com.mk.familyweighttracker.Activities.AddFirstReadingActivity");
//
//        Intent readingIntent = new Intent(_context, className);
//        readingIntent.putExtra(UserDetailActivity.ARG_USER_ID, user.getId());
//
//        Intent userDetailIntent = new Intent(_context, UserDetailActivity.class);
//        userDetailIntent.putExtra(UserDetailActivity.ARG_USER_ID, user.getId());
//        userDetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        Intent usersListIntent = new Intent(_context, UsersListActivity.class);
//        usersListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        Intent dashboardIntent = new Intent(_context, HomeActivity.class);
//        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivities(
//                _context,
//                (int) user.getId(),
//                new Intent[]{dashboardIntent, usersListIntent, userDetailIntent, readingIntent},
//                PendingIntent.FLAG_ONE_SHOT);
//        builder.setContentIntent(pendingIntent);

        Intent intent = new Intent(_context, HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent((int)user.getId(), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = ((NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE));
        manager.notify((int)user.getId(), builder.build());
    }
}
