package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;

import java.util.ArrayList;
import java.util.Date;
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

            mUsers.add(createDummyUser(0, "Mk", 80, 173, "mk.img"));
            mUsers.add(createDummyUser(1, "Rash", 54, 153, "rash.img"));
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

    public int addUser(User newUser) {
        newUser = new User(mUsers.size(), newUser.getName(), newUser.getWeight(), newUser.getHeight(), newUser.getImagePath());
        mUsers.add(newUser);
        return newUser.getId();
    }

    private User createDummyUser(int id, String name, double weight, double height, String imagePath)
    {
        User user = new User(id, name, weight, height, imagePath);
        user.addReading(weight *1.02,height,new Date());
        user.addReading(weight *1.04,height,new Date());
        user.addReading(weight *1.08,height,new Date());
        user.addReading(weight *1.01,height,new Date());
        if(id == 1)
        {
            user.addReading(weight *1.02, height, new Date());
            user.addReading(weight *1.03, height, new Date());
            user.addReading(weight *1.07, height, new Date());
            user.addReading(weight *1.04, height, new Date());
            user.addReading(weight *1.01, height, new Date());
            user.addReading(weight *0.92, height, new Date());
            user.addReading(weight *0.93, height, new Date());
            user.addReading(weight *0.87, height, new Date());
            user.addReading(weight *0.84, height, new Date());
            user.addReading(weight *0.81, height, new Date());
        }
        return user;
    }
}
