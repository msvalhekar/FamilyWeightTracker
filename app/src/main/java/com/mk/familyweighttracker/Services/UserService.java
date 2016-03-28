package com.mk.familyweighttracker.Services;

import com.mk.familyweighttracker.Models.MinimalUser;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Repositories.UserRepository;

import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class UserService {
    private UserRepository userRepository = new UserRepository();

    public List<MinimalUser> getAll() { return userRepository.getAll(); }

    public User get(long userId) { return userRepository.getUser(userId); }

    public Boolean isAlreadyAdded(String name) {
        return userRepository.isAlreadyAdded(name);
    }

    public long add(User newUser) { return userRepository.addUser(newUser); }

    public void remove(long userId) { userRepository.remove(userId); }
}
