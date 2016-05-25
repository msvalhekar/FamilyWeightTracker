package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.User;

import java.util.Date;
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

    @Column(name = "DateOfBirth")
    private Date dateOfBirth;

    @Column(name = "IsMale")
    private boolean isMale;

    @Column(name = "TrackingPeriod")
    private TrackingPeriod trackingPeriod;

    @Column(name = "WeightUnit")
    private WeightUnit weightUnit;

    @Column(name = "HeightUnit")
    private HeightUnit heightUnit;

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
        user.dateOfBirth = this.dateOfBirth;
        user.isMale = this.isMale;
        user.trackingPeriod = this.trackingPeriod;
        user.weightUnit = this.weightUnit;
        user.heightUnit = this.heightUnit;
        user.enableReminder = this.enableReminder;
        user.reminderDay = this.reminderDay;
        user.reminderHour = this.reminderHour;
        user.reminderMinute = this.reminderMinute;

        for (UserReadingModel reading: getReadings()) {
            user.addReading(reading.getId(), reading.Sequence, reading.Weight, reading.Height, reading.TakenOn);
        }
        return user;
    }

    private static UserModel mapFromUser(User user) {
        UserModel userModel = new UserModel();

        UserModel userFound = get(user.getId());
        if(userFound != null)
            userModel = userFound;

        userModel.name = user.name;
        userModel.imageBytes = user.imageBytes;
        userModel.dateOfBirth = user.dateOfBirth;
        userModel.isMale = user.isMale;
        userModel.trackingPeriod = user.trackingPeriod;
        userModel.weightUnit = user.weightUnit;
        userModel.heightUnit = user.heightUnit;
        userModel.enableReminder = user.enableReminder;
        userModel.reminderDay = user.reminderDay;
        userModel.reminderHour = user.reminderHour;
        userModel.reminderMinute = user.reminderMinute;
        return userModel;
    }

    public static void updateUnits(long userId, WeightUnit weightUnit, HeightUnit heightUnit) {
        UserModel userModel = UserModel.get(userId);
        userModel.weightUnit = weightUnit;
        userModel.heightUnit = heightUnit;
        userModel.save();
    }
}
