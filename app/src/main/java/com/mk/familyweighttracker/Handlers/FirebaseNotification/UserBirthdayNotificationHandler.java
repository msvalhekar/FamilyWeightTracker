package com.mk.familyweighttracker.Handlers.FirebaseNotification;

import android.app.AlertDialog;

import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mvalhekar on 19-01-2017.
 */

public class UserBirthdayNotificationHandler implements IFirebaseNotificationHandler {
    public boolean canHandle(String notificationType) {
        return notificationType.equals(Constants.FirebaseNotificationHandler.BirthDate);
    }

    public void handleRequest(JSONObject jsonData) {
        Date requestTime = null;
        try {
            String timeData = jsonData.getString("time");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            requestTime = dateFormat.parse(timeData);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar requestTimeCalendar = Calendar.getInstance();
        requestTimeCalendar.setTime(requestTime);
        int requestDateMonth = requestTimeCalendar.get(Calendar.MONTH);
        int requestDateDay = requestTimeCalendar.get(Calendar.DAY_OF_MONTH);

        for (User user : new UserService().getAll()) {
            if(user.isPregnant() && user.dateOfBirth != null) {

                requestTimeCalendar.setTime(user.dateOfBirth);
                int userDobMonth = requestTimeCalendar.get(Calendar.MONTH);
                int userDobDay = requestTimeCalendar.get(Calendar.DAY_OF_MONTH);

                if(requestDateDay == userDobDay && requestDateMonth == userDobMonth) {
                    showDialog(user, jsonData);
                    break;
                }
            }
        }
    }

    private void showDialog(User user, JSONObject jsonData) {
        final String wishTitle = String.format(TrackerApplication.getApp().getString(R.string.birthday_wish_title), user.name);
        String wishMessage = TrackerApplication.getApp().getString(R.string.birthday_wish_message);

        try {
            wishMessage = jsonData.getString("birthday_wish_message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
