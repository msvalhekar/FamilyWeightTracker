package com.mk.familyweighttracker.DbModels;

import android.net.Uri;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Models.UserReading;

import org.json.JSONException;
import org.json.JSONObject;

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
    public double Height;

    @Column(name = "HeadCircum")
    public double HeadCircum;

    @Column(name = "TakenOn")
    public Date TakenOn;

    @Column(name = "Note")
    public String Note;

    @Column(name = "CreatedOn")
    public Date CreatedOn;

    public static UserReadingModel get(long id) {
        return new Select()
                .from(UserReadingModel.class)
                .where("Id = ?", id)
                .executeSingle();
    }

    public static UserReadingModel getBySequence(long userId, long sequence) {
        return new Select()
                .from(UserReadingModel.class)
                .where("Sequence = ? AND User = ?", sequence, userId)
                .executeSingle();
    }

    public static void save(final UserReading reading) {
        UserReadingModel readingModel = new UserReadingModel();

        UserReadingModel model = UserReadingModel.get(reading.Id);
        if(model != null)
            readingModel = model;

        readingModel.User = UserModel.get(reading.UserId);
        readingModel.Sequence = reading.Sequence;
        readingModel.Weight = reading.Weight;
        readingModel.Height = reading.Height;
        readingModel.HeadCircum = reading.HeadCircumference;
        readingModel.TakenOn = reading.TakenOn;
        readingModel.Note = reading.Note;
        readingModel.CreatedOn = new Date();

        readingModel.save();
    }

    public static void delete(long readingId) {
        new Delete()
                .from(UserReadingModel.class)
                .where("Id = ? ", readingId)
                .execute();
    }

    public static void deleteAllFor(long userId) {
        new Delete()
                .from(UserReadingModel.class)
                .where("User = ? ", userId)
                .execute();
    }

    public UserReading mapToUserReading() {
        return new UserReading(this.getId(), this.User.getId(), this.Sequence, this.Weight, this.Height, this.HeadCircum, this.Note, this.TakenOn);
    }

    public JSONObject toJSON() throws JSONException {
        return new JSONObject()
                .put("createdOn", CreatedOn.getTime())
                .put("takenOn", TakenOn.getTime())
                .put("usrNm", User.name)
                .put("seq", Sequence)
                .put("wt", Weight)
                .put("ht", Height)
                .put("hc", HeadCircum)
                .put("note", Note)
                .put("imgSz", ImageUtility.getBitmapSize(ImageUtility.getReadingImagePath(User.getId(), Sequence)));
    }

    public static void saveFrom(JSONObject readingJSON, UserModel userModel) throws JSONException {
        UserReadingModel reading = new UserReadingModel();
        reading.CreatedOn = new Date(readingJSON.getLong("createdOn"));
        reading.TakenOn = new Date(readingJSON.getLong("takenOn"));
        reading.User = UserModel.get(readingJSON.getLong("usrNm"));
        reading.Sequence = readingJSON.getLong("seq");
        reading.Weight = readingJSON.getDouble("wt");
        reading.Height = readingJSON.getDouble("ht");
        reading.HeadCircum = readingJSON.getDouble("hc");
        reading.Note = readingJSON.getString("note");
        reading.User = userModel;

        reading.save();
    }
}
