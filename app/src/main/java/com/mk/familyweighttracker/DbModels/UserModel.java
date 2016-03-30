package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Models.UserHeader;
import com.mk.familyweighttracker.Models.User;

import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
@Table(name = "Users")
public class UserModel extends Model {

    @Column(name = "Name", index = true)
    public String Name;

    @Column(name = "ImageBytes")
    public byte[] ImageBytes;

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

    public static UserModel add(User newUser) {
        UserModel user = mapFromUser(newUser);
        user.save();
        return user;
    }

    public User mapToUser() {
        User user = new User(getId(), Name, ImageBytes);
        for (UserReadingModel reading: getReadings()) {
            user.addReading(reading.Sequence, reading.Weight, reading.Height, reading.TakenOn);
        }
        return user;
    }

    public UserHeader mapToUserHeader() {
        UserHeader user = new UserHeader(getId(), Name, ImageBytes);
        return user;
    }

    private static UserModel mapFromUser(User user) {
        UserModel userModel = new UserModel();
        userModel.Name = user.getName();
        userModel.ImageBytes = user.getImageBytes();
        return userModel;
    }
}
