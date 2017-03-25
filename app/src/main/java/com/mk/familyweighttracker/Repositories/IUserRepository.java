package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;

import java.util.List;

/**
 * Created by mvalhekar on 26-02-2017.
 */
public interface IUserRepository {
    User getUser(long userId);
    User getUser(String userName);
    Boolean isAlreadyAdded(String name);
    long addUser(User newUser);
    List<User> getAll();
    void remove(long userId);
    void updateUnits(long userId, WeightUnit weightUnit, HeightUnit heightUnit, HeightUnit headCircumUnit);

    UserReading getUserReading(long readingId);
    UserReading getUserReadingBySequence(long userId, long sequence);
    void saveReading(UserReading reading);
    void deleteReading(long readingId);
}
