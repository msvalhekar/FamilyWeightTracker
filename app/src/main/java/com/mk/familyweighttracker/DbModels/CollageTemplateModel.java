package com.mk.familyweighttracker.DbModels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mk.familyweighttracker.Enums.CollageTemplateType;
import com.mk.familyweighttracker.R;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mvalhekar on 26-03-2016.
 */
@Table(name = "CollageTemplates")
public class CollageTemplateModel extends Model {

    @Column(name = "Name")
    public String Name;

    @Column(name = "ImageContent")
    public byte[] ImageContent;

    @Column(name = "TemplateType")
    public CollageTemplateType TemplateType;

    @Column(name = "CreatedOn")
    public Date CreatedOn;

    public Bitmap getImageBitmap() {
        return BitmapFactory.decodeByteArray(ImageContent, 0, ImageContent.length);
    }

    public boolean isPregnancyTemplate(){
        return TemplateType == CollageTemplateType.Pregnancy;
    }

    public static List<CollageTemplateModel> getAllFor(boolean isPregnant) {

//        CollageTemplateModel ct1 = new CollageTemplateModel();
//        ct1.Name = "first temp";
//        ct1.TemplateType = CollageTemplateType.Pregnancy;
//        ct1.CreatedOn = new Date();
//        ct1.save();
//
//        CollageTemplateModel ct2 = new CollageTemplateModel();
//        ct2.Name = "second temp";
//        ct2.TemplateType = CollageTemplateType.Infant;
//        ct2.CreatedOn = new Date();
//        ct2.save();

        return new Select().from(CollageTemplateModel.class)
                .where("TemplateType = ?", isPregnant ? CollageTemplateType.Pregnancy : CollageTemplateType.Infant)
                .orderBy("CreatedOn")
                .execute();
    }
}
