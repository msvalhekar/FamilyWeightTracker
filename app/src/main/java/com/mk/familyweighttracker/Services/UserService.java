package com.mk.familyweighttracker.Services;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Repositories.UserRepository;

import java.util.List;

/**
 * Created by mvalhekar on 25-03-2016.
 */
public class UserService {
    private UserRepository userRepository = new UserRepository();

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User get(long userId) {
        return userRepository.getUser(userId);
    }

    public Boolean isAlreadyAdded(String name) {
        return userRepository.isAlreadyAdded(name);
    }

    public long add(User newUser) {
        return userRepository.addUser(newUser);
    }

    public void remove(long userId) {
        userRepository.remove(userId);
    }

    public void addReading(UserReading reading) {
        userRepository.addReading(reading);
    }

    public void deleteReading(long readingId) {
        userRepository.deleteReading(readingId);
    }

    public void updateUnits(long userId, WeightUnit weightUnit, HeightUnit heightUnit) {
        userRepository.updateUnits(userId, weightUnit, heightUnit);
    }
}
