package com.mk.familyweighttracker.Services;

import android.app.backup.BackupManager;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.Framework.Utility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.Repositories.UserRepository;

import java.io.File;
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

    public User get(String userName) {
        return userRepository.getUser(userName);
    }

    public UserReading getReading(long readingId) {
        return userRepository.getUserReading(readingId);
    }

    public Boolean isAlreadyAdded(String name) {
        return userRepository.isAlreadyAdded(name);
    }

    public long add(User newUser) {
        long userId = userRepository.addUser(newUser);
        dataChanged();
        return userId;
    }

    public void remove(long userId) {
        User user = get(userId);
        for(UserReading reading : user.getReadings(true)) {
            new File(reading.getImagePath()).delete();
        }
        new File(user.getImagePath()).delete();

        userRepository.remove(userId);
        dataChanged();
    }

    public void addReading(UserReading reading) {
        userRepository.saveReading(reading);
        dataChanged();
    }

    public void deleteReading(long readingId) {
        UserReading reading = getReading(readingId);
        new File(reading.getImagePath()).delete();

        userRepository.deleteReading(readingId);
        dataChanged();
    }

    public void updateUnits(long userId, WeightUnit weightUnit, HeightUnit heightUnit) {
        User user = userRepository.getUser(userId);
        boolean convertWeight = user.weightUnit != weightUnit;
        boolean convertHeight = user.heightUnit != heightUnit;
        for (UserReading reading : user.getReadings(true)) {
            if(reading.Sequence == 0) continue;

            if(convertWeight) reading.Weight = Utility.convertWeightTo(reading.Weight, weightUnit);

            if(convertHeight) reading.Height = Utility.convertHeightTo(reading.Height, heightUnit);

            userRepository.saveReading(reading);
        }
        userRepository.updateUnits(userId, weightUnit, heightUnit);
        dataChanged();
    }

    private void dataChanged() {
        BackupManager.dataChanged(TrackerApplication.getApp().getPackageName());
    }
}
