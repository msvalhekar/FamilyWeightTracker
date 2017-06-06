package com.mk.familyweighttracker.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mk.familyweighttracker.Enums.ImageShapeType;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.TrackerApplication;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mvalhekar on 26-03-2016.
 */
public class UserReading {
    public static final double DEFAULT_PREGNANCY_BASE_WEIGHT = 60.0;
    public static final double DEFAULT_PREGNANCY_BASE_HEIGHT = 160.0;
    public static final double DEFAULT_PREGNANCY_BASE_HEADCIRCUM = 53.0;
    public static final double DEFAULT_INFANT_BASE_WEIGHT = 2.5;
    public static final double DEFAULT_INFANT_BASE_HEIGHT = 50.0;
    public static final double DEFAULT_INFANT_BASE_HEADCIRCUM = 35;

    public long Id;
    public long UserId;
    public long Sequence;
    public double Weight;
    public double Height;
    public double HeadCircumference;
    public Date TakenOn;
    public String Note;

    public String getImagePath() {
        return ImageUtility.getReadingImagePath(UserId, Sequence);
    }

    public UserReading () {
    }

    public UserReading(long id, long userId, long sequence, double weight, double height, double headCircum, String note, Date takenOn) {
        Id = id;
        UserId = userId;
        Sequence = sequence;
        Weight = weight;
        Height = height;
        HeadCircumference = headCircum;
        Note = note;
        TakenOn = takenOn;
    }

    public boolean isPrePregnancyReading() {
        return Sequence == 0;
    }

    public String getDisplayTakenOn() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormatter.format(this.TakenOn);
    }

    public boolean isDeliveryReading() {
        return Sequence == User.MAXIMUM_PREGNANCY_READINGS_COUNT -1;
    }

    public Bitmap getImageAsBitmap(ImageShapeType shapeType, int newWidth, int newHeight){
        Bitmap bitmap = null;

        if(new File(getImagePath()).exists())
            bitmap = ImageUtility.decodeSampledBitmapFromFile(getImagePath(), newWidth, newHeight);

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(
                    TrackerApplication.getApp().getResources(),
                    getUser().isPregnant() ? R.drawable.weekly : getUser().isMale ? R.drawable.boy : R.drawable.girl);
        }
        if(shapeType == ImageShapeType.Oval || shapeType == ImageShapeType.Circle)
            return ImageUtility.transformBitmapToShape(shapeType, bitmap);
        return bitmap;
    }

    private User mUser;
    private User getUser() {
        if(mUser == null)
            mUser = new UserService().get(UserId);
        return mUser;
    }

    public static UserReading createReading(User user, long sequence) {
        UserReading userReading = new UserReading();
        userReading.UserId = user.getId();
        userReading.TakenOn = new Date();
        userReading.Sequence = sequence;
        userReading.Weight = user.getDefaultBaseWeight();
        userReading.Height = user.getDefaultBaseHeight();
        userReading.HeadCircumference = user.getDefaultBaseHeadCircum();
        UserReading previousReading = user.getLatestReading();
        if(previousReading != null) {
            userReading.Weight = previousReading.Weight;
            userReading.Height = previousReading.Height;
            userReading.HeadCircumference = previousReading.HeadCircumference;
        }
        return userReading;
    }
}
