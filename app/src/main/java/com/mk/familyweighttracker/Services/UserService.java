package com.mk.familyweighttracker.Services;

import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Repositories.UserRepository;

import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class UserService {
    private UserRepository userRepository;
    public UserService()
    {
        userRepository = new UserRepository();
    }

    public List<MinimalUser> getAllUsers()
    {
        return userRepository.getAllUsers();
    }

    public User getUser(int userId) {
        return userRepository.getUser(userId);
    }
}
