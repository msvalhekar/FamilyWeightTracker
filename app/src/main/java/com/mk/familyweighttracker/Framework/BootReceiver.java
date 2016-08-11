package com.mk.familyweighttracker.Framework;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.HomeActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Date;
import java.util.List;

/**
 * Created by mvalhekar on 16-05-2016.
 */
public class BootReceiver extends BroadcastReceiver {

    private Context _context;
    @Override
    public void onReceive(Context context, Intent intent) {
        _context = context;

        ActiveAndroid.initialize(context);

        try {
            setReminders();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void setReminders() {
        List<User> users = new UserService().getAll();
        for (User user:users) {
            user.resetReminder(_context);
        }
    }
}
