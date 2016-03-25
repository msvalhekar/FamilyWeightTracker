package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class UserRepository {
    private static List<User> mUsers;

    public UserRepository()
    {
        if(mUsers == null) {
            mUsers = new ArrayList();

            mUsers.add(new User(0, "Mk", 80, 173, "mk.img"));
            mUsers.add(new User(1, "Rash", 54, 153, "rash.img"));
            //mUsers.add(new User(2, "Soham", "soham.img"));
            //mUsers.add(new User(3, "Ovee", "ovee.img"));
        }
    }

    public List<MinimalUser> getAllUsers() {
        List<MinimalUser> minimalUsers = new ArrayList();
        for (User user: mUsers)
            minimalUsers.add(new MinimalUser(user));

        return minimalUsers;
    }

    public User getUser(int userId) {
        for (User user: mUsers) {
            if(user.getId() == userId)
                return user;
        }
        return null;
    }

    public User getUser(String name) {
        String nameToSearch = name.toLowerCase().trim();
        for (User user: mUsers) {
            if(user.getName().toLowerCase().trim().equals(nameToSearch))
                return user;
        }
        return null;
    }

    public Boolean isAlreadyAdded(String name) {
        User matchedUser = getUser(name);
        return matchedUser != null;
    }

    public User addUser(User newUser) {
        newUser = new User(mUsers.size(), newUser.getName(), newUser.getWeight(), newUser.getHeight(), newUser.getImagePath());
        mUsers.add(newUser);
        return newUser;
    }
}
