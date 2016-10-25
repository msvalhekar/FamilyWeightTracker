package com.mk.familyweighttracker.Framework;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.Activities.SplashActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

/**
 * Created by mvalhekar on 16-05-2016.
 */
public class WeeklyReminderAlarmReceiver extends BroadcastReceiver {

    private Context _context;
    @Override
    public void onReceive(Context context, Intent intent) {
        _context = context;

        long userId = intent.getLongExtra(Constants.ExtraArg.USER_ID, 0);

        ActiveAndroid.initialize(context);

        try {
            sendReminderNotification(userId);
            vibrateDevice();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void vibrateDevice() {
        long[] pattern = new long[]{100, 100, 100, 200, 100, 300, 100, 200, 100, 100};
        ((Vibrator) _context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, -1);
    }

    private void sendReminderNotification(long userId) throws ClassNotFoundException {
        User user = new UserService().get(userId);
        if(user == null || !user.enableReminder) return;

        UserReading latestReading = user.getLatestReading();
        long nextSequence = latestReading != null ? latestReading.Sequence +1 : 0;
        String titleMessage = _context.getString(R.string.notification_title);
        String textMessage = String.format(_context.getString(R.string.notification_message), nextSequence);
        android.support.v7.app.NotificationCompat.Builder builder =
                (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(_context)
                .setContentTitle(titleMessage)
                .setSmallIcon(R.drawable.launcher)
                .setContentText(textMessage)
                .setAutoCancel(true);

        Intent intent = new Intent(_context, SplashActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent((int)user.getId(), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = ((NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE));
        manager.notify((int)user.getId(), builder.build());
    }
}
