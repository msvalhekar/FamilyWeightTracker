package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Models.UserHeader;
import com.mk.familyweighttracker.Models.User;

import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
@Table(name = "Users")
public class UserModel extends Model {

    @Column(name = "Name")
    private String name;

    @Column(name = "ImageBytes")
    private byte[] imageBytes;

    @Column(name = "IsMale")
    private boolean isMale;

    @Column(name = "TrackingPeriod")
    private TrackingPeriod trackingPeriod;

    @Column(name = "EnableReminder")
    private boolean enableReminder;

    @Column(name = "ReminderDay")
    private int reminderDay;

    @Column(name = "ReminderHour")
    private int reminderHour;

    @Column(name = "ReminderMinute")
    private int reminderMinute;

    public List<UserReadingModel> getReadings() {
        return getMany(UserReadingModel.class, "User");
    }

    public static List<UserModel> getAll() {
        return new Select()
                .from(UserModel.class)
                .execute();
    }

    public static UserModel get(long userId) {
        return new Select()
                .from(UserModel.class)
                .where("Id = ?", userId)
                .executeSingle();
    }

    public static UserModel get(String userName) {
        return new Select()
                .from(UserModel.class)
                .where("Name = ?", userName)
                .executeSingle();
    }

    public static Boolean exists(String name) {
        UserModel model = get(name);
        return model != null;
    }

    public static void delete(long userId) {
        UserReadingModel.deleteAllFor(userId);
        UserModel.delete(UserModel.class, userId);
    }

    public static UserModel add(User user) {
        UserModel userModel = mapFromUser(user);
        userModel.save();
        return userModel;
    }

    public User mapToUser() {
        User user = new User(getId());
        user.name = this.name;
        user.imageBytes = this.imageBytes;
        user.isMale = this.isMale;
        user.trackingPeriod = this.trackingPeriod;
        user.enableReminder = this.enableReminder;
        user.reminderDay = this.reminderDay;
        user.reminderHour = this.reminderHour;
        user.reminderMinute = this.reminderMinute;

        for (UserReadingModel reading: getReadings()) {
            user.addReading(reading.Sequence, reading.Weight, reading.Height, reading.TakenOn);
        }
        return user;
    }

    public UserHeader mapToUserHeader() {
        UserHeader user = new UserHeader(getId(), name, imageBytes);
        return user;
    }

    private static UserModel mapFromUser(User user) {
        UserModel userModel = new UserModel();
        userModel.name = user.name;
        userModel.imageBytes = user.imageBytes;
        userModel.isMale = user.isMale;
        userModel.trackingPeriod = user.trackingPeriod;
        userModel.enableReminder = user.enableReminder;
        userModel.reminderDay = user.reminderDay;
        userModel.reminderHour = user.reminderHour;
        userModel.reminderMinute = user.reminderMinute;
        return userModel;
    }
}
