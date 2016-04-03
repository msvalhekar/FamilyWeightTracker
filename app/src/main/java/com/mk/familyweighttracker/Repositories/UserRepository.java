package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.DbModels.UserModel;
import com.mk.familyweighttracker.DbModels.UserReadingModel;
import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class UserRepository {

    public User getUser(long userId) {
        return UserModel.get(userId).mapToUser();
    }

    public User getUser(String name) {
        return UserModel.get(name.toLowerCase().trim()).mapToUser();
    }

    public Boolean isAlreadyAdded(String name) {
        return UserModel.exists(name.toLowerCase().trim());
    }

    public long addUser(User newUser) {
        UserModel user = UserModel.add(newUser);
        return user.getId();
    }

    public List<User> getAll() {
        List<UserModel> userModelList = UserModel.getAll();

        List<User> users = new ArrayList<>();
        for (UserModel userModel: userModelList) {
            users.add(userModel.mapToUser());
        }
        return users;
    }

    public void remove(long userId) {
        UserModel.delete(userId);
    }

    public void addReading(UserReading reading) {
        UserReadingModel.add(reading);
    }

    public void update(long userId, WeightUnit weightUnit, HeightUnit heightUnit) {
        UserModel.update(userId, weightUnit, heightUnit);
    }
}
