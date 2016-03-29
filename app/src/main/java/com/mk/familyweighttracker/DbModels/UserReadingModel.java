package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;

import java.util.Date;
import java.util.List;

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
    public double Height;

    @Column(name = "TakenOn")
    public Date TakenOn;

    @Column(name = "CreatedOn")
    public Date CreatedOn;

    public static void add(final UserReading reading) {
        UserReadingModel readingModel = new UserReadingModel();
        readingModel.User = UserModel.get(reading.UserId);
        readingModel.Weight = reading.Weight;
        readingModel.Height = reading.Height;
        readingModel.TakenOn = reading.TakenOn;
        readingModel.CreatedOn = new Date();

        readingModel.save();
    }
}
