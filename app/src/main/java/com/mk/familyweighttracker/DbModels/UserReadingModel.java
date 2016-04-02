package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.mk.familyweighttracker.Models.UserReading;

import java.util.Date;

/**
 * Created by mvalhekar on 26-03-2016.
 */
@Table(name = "UserReadings")
public class UserReadingModel extends Model {

    @Column(name = "User")
    public UserModel User;

    @Column(name = "Sequence")
    public long Sequence;

    @Column(name = "Weight")
    public double Weight;

    @Column(name = "Height")
    public int Height;

    @Column(name = "TakenOn")
    public Date TakenOn;

    @Column(name = "CreatedOn")
    public Date CreatedOn;

    public static void add(final UserReading reading) {
        UserReadingModel readingModel = new UserReadingModel();
        readingModel.User = UserModel.get(reading.UserId);
        readingModel.Sequence = reading.Sequence;
        readingModel.Weight = reading.Weight;
        readingModel.Height = reading.Height;
        readingModel.TakenOn = reading.TakenOn;
        readingModel.CreatedOn = new Date();

        readingModel.save();
    }

    public static void deleteAllFor(long userId) {
        new Delete()
                .from(UserReadingModel.class)
                .where("User = ? ", userId)
                .execute();
    }
}
