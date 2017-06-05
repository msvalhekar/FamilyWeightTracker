package com.mk.familyweighttracker.DbModels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.activeandroid.Cache;
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

    @Column(name = "TemplateType")
    public CollageTemplateType TemplateType;

    @Column(name = "ImageContent")
    public byte[] ImageContent;

    @Column(name = "Width")
    public int Width;

    @Column(name = "Height")
    public int Height;

    @Column(name = "CreatedOn")
    public Date CreatedOn;


    public static CollageTemplateModel get(int templateId) {
        return new Select()
                .from(CollageTemplateModel.class)
                .where("id=?", templateId)
                .executeSingle();
    }

    public List<CollageTemplateItemModel> getItems() {
        return new Select()
                .from(CollageTemplateItemModel.class)
                .where("CollageTemplateItems.CollageTemplate=?", getId())
                .orderBy("Sequence")
                .execute();
    }

    public Bitmap getImageBitmap() {
        return BitmapFactory.decodeByteArray(ImageContent, 0, ImageContent.length);
    }

    public boolean isPregnancyTemplate(){
        return TemplateType == CollageTemplateType.Pregnancy;
    }

    public String getDisplayName () {
        return String.format("%s - %s", isPregnancyTemplate() ? "Pregnancy" : "Infant", Name);
    }

    public static List<CollageTemplateModel> getAllFor(boolean isPregnant) {
        return new Select().from(CollageTemplateModel.class)
                .where("TemplateType = ?", isPregnant ? CollageTemplateType.Pregnancy : CollageTemplateType.Infant)
                .orderBy("CreatedOn")
                .execute();
    }
}
