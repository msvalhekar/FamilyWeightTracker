package com.mk.familyweighttracker.DbModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mvalhekar on 26-03-2016.
 */
@Table(name = "CollageTemplateItems")
public class CollageTemplateItemModel extends Model {

    @Column(name = "CollageTemplate")
    public CollageTemplateModel CollageTemplate;

    @Column(name = "StartX")
    public int StartX;

    @Column(name = "StartY")
    public int StartY;

    @Column(name = "Sequence")
    public int Sequence;

    public CollageTemplateItemModel() { }

    public CollageTemplateItemModel(CollageTemplateModel template, int startX, int startY, int sequence) {
        this.CollageTemplate = template;
        this.StartX = startX;
        this.StartY = startY;
        this.Sequence = sequence;
    }

    public static CollageTemplateItemModel mapFrom(CollageTemplateModel template, String jsonString) {
        CollageTemplateItemModel itemModel = new CollageTemplateItemModel();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            itemModel.StartX = jsonObject.getInt("StartX");
            itemModel.StartY = jsonObject.getInt("StartY");
            itemModel.Sequence = jsonObject.getInt("Sequence");
            itemModel.CollageTemplate = template;
            return itemModel;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
