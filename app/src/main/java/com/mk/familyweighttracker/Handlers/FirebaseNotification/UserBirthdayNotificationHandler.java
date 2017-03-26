package com.mk.familyweighttracker.Handlers.FirebaseNotification;

import android.app.AlertDialog;
import android.widget.Toast;

import com.mk.familyweighttracker.Activities.SplashActivity;
import com.mk.familyweighttracker.Activities.UsersListActivity;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.NotificationCenter;
import com.mk.familyweighttracker.Framework.StringHelper;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Models.PushNotification;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.Calendar;

/**
 * Created by mvalhekar on 19-01-2017.
 */

public class UserBirthdayNotificationHandler implements IFirebaseNotificationHandler {
    public boolean canHandle(String notificationType) {
        return notificationType.equals(Constants.FirebaseNotificationHandler.BirthDate);
    }

    public void handleRequest(NotificationData data) {

        Calendar requestTimeCalendar = Calendar.getInstance();
        requestTimeCalendar.setTime(data.datetime);
        int requestDateMonth = requestTimeCalendar.get(Calendar.MONTH);
        int requestDateDay = requestTimeCalendar.get(Calendar.DAY_OF_MONTH);

        for (User user : new UserService().getAll()) {
            if(user.isPregnant() && user.dateOfBirth != null) {

                requestTimeCalendar.setTime(user.dateOfBirth);
                int userDobMonth = requestTimeCalendar.get(Calendar.MONTH);
                int userDobDay = requestTimeCalendar.get(Calendar.DAY_OF_MONTH);

                if(requestDateDay == userDobDay && requestDateMonth == userDobMonth) {
                    try {
                        if(TrackerApplication.getCurrentActivity() == null) {
                            //// TODO: 26-03-2017 not working as expected, control does not come here
                            handleRequestWhenAppPurged(user, data);
                            return;
                        }

                        handleRequestWhenInForeground(user, data);
                    } catch (Exception e) {}
                    break;
                }
            }
        }
    }

    private void handleRequestWhenAppPurged(User user, NotificationData data) {
        //// TODO: 26-03-2017 not working as expected, control does not come here
        String wishTitle = String.format(TrackerApplication.getApp().getString(R.string.birthday_wish_title), user.name);
        String wishMessage = StringHelper.isNullOrEmpty(data.birthdayWishMessage)
                ? TrackerApplication.getApp().getString(R.string.birthday_wish_message)
                : data.birthdayWishMessage;

        PushNotification pushNotification = new PushNotification();
        pushNotification.title = wishTitle;
        pushNotification.message = wishMessage;
        pushNotification.context = TrackerApplication.getApp();
        pushNotification.requestCode = (int)user.getId();
        pushNotification.toClass = UsersListActivity.class;
        NotificationCenter.showNotification(pushNotification);
    }

    private void handleRequestWhenInForeground(User user, NotificationData data) {
        showBirthDayWishDialog(user, data);
    }

    private void showBirthDayWishDialog(User user, NotificationData data) {
        final String wishTitle = String.format(TrackerApplication.getApp().getString(R.string.birthday_wish_title), user.name);
        String wishMessage = StringHelper.isNullOrEmpty(data.birthdayWishMessage)
                ? TrackerApplication.getApp().getString(R.string.birthday_wish_message)
                : data.birthdayWishMessage;

        final String finalWishMessage = wishMessage;

        TrackerApplication.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(TrackerApplication.getCurrentActivity())
                        .setTitle(wishTitle)
                        .setMessage(finalWishMessage)
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .create()
                        .show();
            }
        });
    }
}
