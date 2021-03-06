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
public class UserRepository implements IUserRepository {

    @Override
    public User getUser(long userId) {
        UserModel userModel = UserModel.get(userId);
        if(userModel == null) return null;
        return userModel.mapToUser();
    }

    @Override
    public User getUser(String userName) {
        UserModel userModel = UserModel.get(userName);
        if(userModel == null) return null;
        return userModel.mapToUser();
    }

    @Override
    public UserReading getUserReading(long readingId) {
        UserReadingModel readingModel = UserReadingModel.get(readingId);
        if(readingModel == null) return null;
        return readingModel.mapToUserReading();
    }

    @Override
    public UserReading getUserReadingBySequence(long userId, long sequence) {
        UserReadingModel readingModel = UserReadingModel.getBySequence(userId, sequence);
        if(readingModel == null) return null;
        return readingModel.mapToUserReading();
    }

    @Override
    public Boolean isAlreadyAdded(String name) {
        return UserModel.exists(name.toLowerCase().trim());
    }

    @Override
    public long addUser(User newUser) {
        UserModel user = UserModel.add(newUser);
        return user.getId();
    }

    @Override
    public List<User> getAll() {
        List<UserModel> userModelList = UserModel.getAll();

        List<User> users = new ArrayList<>();
        for (UserModel userModel: userModelList) {
            users.add(userModel.mapToUser());
        }
        return users;
    }

    @Override
    public void remove(long userId) {
        UserModel.delete(userId);
    }

    @Override
    public void saveReading(UserReading reading) {
        UserReadingModel.save(reading);
    }

    @Override
    public void deleteReading(long readingId) {
        UserReadingModel.delete(readingId);
    }

    @Override
    public void updateUnits(long userId, WeightUnit weightUnit, HeightUnit heightUnit, HeightUnit headCircumUnit) {
        UserModel.updateUnits(userId, weightUnit, heightUnit, headCircumUnit);
    }
}
