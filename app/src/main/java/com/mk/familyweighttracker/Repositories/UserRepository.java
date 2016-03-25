package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class UserRepository {
    private List<User> mUsers = new ArrayList();

    public UserRepository()
    {
        mUsers.add(new User(0, "Mk", "mk.img"));
        mUsers.add(new User(1, "Rash", "rash.img"));
        //mUsers.add(new User(2, "Soham", "soham.img"));
        //mUsers.add(new User(3, "Ovee", "ovee.img"));
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
}
