package com.mk.familyweighttracker.Framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.Activities.SplashActivity;
import com.mk.familyweighttracker.Models.PushNotification;
import com.mk.familyweighttracker.Models.User;
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
        if(user == null || !user.enableReminder)
            return;

        if(user.getDeliveryReading() != null) {
            user.enableReminder = false;
            new UserService().add(user);
            user.resetReminder();
            return;
        }

        PushNotification pushNotification = new PushNotification();

        pushNotification.title = user.getReminderNotificationTitle();
        pushNotification.message = user.getReminderNotificationMessage();
        pushNotification.context = _context;
        pushNotification.requestCode = (int)user.getId();
        pushNotification.toClass = SplashActivity.class;
        NotificationCenter.showNotification(pushNotification);
    }
}
