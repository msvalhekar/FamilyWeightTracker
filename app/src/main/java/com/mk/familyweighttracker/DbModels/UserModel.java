package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Enums.BodyWeightCategory;
import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Services.PregnancyService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
@Table(name = "Users")
public class UserModel extends Model {

    @Column(name = "Name", index = true)
    public String Name;

    @Column(name = "ImagePath")
    public String ImagePath;


    public User mapToUser() {
        User user = new User(getId(), Name, ImagePath);
        return user;
    }

    public MinimalUser mapToMinimalUser() {
        MinimalUser user = new MinimalUser(getId(), Name, ImagePath);
        return user;
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
        UserModel.delete(UserModel.class, userId);
    }

//    private List<UserReading> mReadings;

//    public void addReading(double weight, double height, Date takenOn)
//    {
//        UserReading reading = new UserReading(Id, mReadings.size(), weight, height, takenOn);
//        mReadings.add(reading);
//    }

//    public List<UserReading> getReadings() {
//        return mReadings;
//    }
}
