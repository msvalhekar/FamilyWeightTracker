package com.mk.familyweighttracker.Models;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.WeightUnit;

import java.util.Date;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class NewUserViewModel {
    public long Id;
    public byte[] ImageBytes;
    public String Name;
    public boolean IsMale;
    public TrackingPeriod TrackingPeriod;
    public boolean EnableReminder;
    public int ReminderDay;
    public int ReminderHour;
    public int ReminderMinute;
    public Date DateOfBirth;

    public User mapToUser() {
        User user = new User(0);
        user.name = Name;
        user.imageBytes = ImageBytes;
        user.dateOfBirth = DateOfBirth;
        user.isMale = IsMale;
        user.trackingPeriod = TrackingPeriod;
        user.enableReminder = EnableReminder;
        user.reminderDay = ReminderDay;
        user.reminderHour = ReminderHour;
        user.reminderMinute = ReminderMinute;
        user.weightUnit = WeightUnit.kg;
        user.heightUnit = HeightUnit.cm;
        return user;
    }
}