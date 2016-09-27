package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.TrackingPeriod;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Column(name = "DeliveryDueDate")
    private Date deliveryDueDate;

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

    public static void deleteAll() {
        for (UserModel user : getAll()) {
            UserReadingModel.deleteAllFor(user.getId());
            UserModel.delete(UserModel.class, user.getId());
        }
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
        user.deliveryDueDate = this.deliveryDueDate;
        user.isMale = this.isMale;
        user.trackingPeriod = this.trackingPeriod;
        user.weightUnit = this.weightUnit;
        user.heightUnit = this.heightUnit;
        user.enableReminder = this.enableReminder;
        user.reminderDay = this.reminderDay;
        user.reminderHour = this.reminderHour;
        user.reminderMinute = this.reminderMinute;

        for (UserReadingModel reading: getReadings()) {
            user.addReading(reading.mapToUserReading());
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
        userModel.deliveryDueDate = user.deliveryDueDate;
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

    public JSONObject toJSON() throws JSONException {
        JSONArray readingsJSONArray = new JSONArray();
        for (UserReadingModel reading : getReadings()) {
            readingsJSONArray.put(reading.toJSON());
        }

        return new JSONObject()
                .put("name", name)
                .put("imageBytes", imageBytes)
                .put("dob", dateOfBirth == null ? 0 : dateOfBirth.getTime())
                .put("ddd", deliveryDueDate == null ? 0 : deliveryDueDate.getTime())
                .put("isMale", isMale)
                .put("trackPeriod", trackingPeriod)
                .put("wtUnit", weightUnit)
                .put("htUnit", heightUnit)
                .put("reminder", enableReminder)
                .put("remDay", reminderDay)
                .put("remHr", reminderHour)
                .put("remMin", reminderMinute)
                .put("readings", readingsJSONArray);
    }

    public static void saveFrom(JSONObject userJSON) throws JSONException {
        UserModel user = new UserModel();
        user.name = userJSON.getString("name");
        user.imageBytes = (byte[]) userJSON.get("imageBytes");
        user.dateOfBirth = userJSON.getLong("dob") == 0 ? null : new Date(userJSON.getLong("dob"));
        user.deliveryDueDate = userJSON.getLong("ddd") == 0 ? null : new Date(userJSON.getLong("ddd"));
        user.isMale = userJSON.getBoolean("isMale");
        user.trackingPeriod = TrackingPeriod.valueOf(userJSON.getString("trackPeriod"));
        user.weightUnit = WeightUnit.valueOf(userJSON.getString("wtUnit"));
        user.heightUnit = HeightUnit.valueOf(userJSON.getString("htUnit"));
        user.enableReminder = userJSON.getBoolean("reminder");
        user.reminderDay = userJSON.getInt("remDay");
        user.reminderHour = userJSON.getInt("remHr");
        user.reminderMinute = userJSON.getInt("remMin");
        user.save();

        JSONArray readingsJSONArray = userJSON.getJSONArray("readings");
        for (int i=0; i<readingsJSONArray.length(); i++) {
            JSONObject readingJSON = readingsJSONArray.getJSONObject(i);
            UserReadingModel.saveFrom(readingJSON, user);
        }
    }
}
