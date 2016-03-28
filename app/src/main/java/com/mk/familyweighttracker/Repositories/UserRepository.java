package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.DbModels.UserModel;
import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;

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
        UserModel user = new UserModel();
        user.Name = newUser.getName();
        user.ImagePath = newUser.getImagePath();
        user.save();
        return user.getId();
    }

    public List<MinimalUser> getAll() {
        List<UserModel> userModelList = UserModel.getAll();

        List<MinimalUser> users = new ArrayList<>();
        for (UserModel userModel: userModelList) {
            users.add(userModel.mapToMinimalUser());
        }
        return users;
    }

    public void remove(long userId) {
        UserModel.delete(userId);
    }
}
