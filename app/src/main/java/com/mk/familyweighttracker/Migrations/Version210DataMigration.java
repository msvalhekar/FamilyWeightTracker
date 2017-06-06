package com.mk.familyweighttracker.Migrations;

import com.mk.familyweighttracker.DbModels.CollageTemplateItemModel;
import com.mk.familyweighttracker.DbModels.CollageTemplateModel;
import com.mk.familyweighttracker.Enums.CollageTemplateType;
import com.mk.familyweighttracker.Enums.ImageShapeType;

import java.util.Date;

public class Version210DataMigration extends VersionDataMigration {
    @Override
    int getVersionCode() {
        return 210;
    }

    @Override
    int migrate() {
        createTemplates();
        return 0;
    }

    private void createTemplates() {
        create4plus2plus4BasicTemplate();
    }

    private static void create4plus2plus4BasicTemplate() {
        CollageTemplateModel ct1 = new CollageTemplateModel();
        ct1.Name = "4 + 2 + 4 - with heading";
        ct1.TemplateType = CollageTemplateType.Pregnancy;
        ct1.Width = 2450;
        ct1.Height = 2440;
        ct1.CreatedOn = new Date();
        ct1.save();

        new CollageTemplateItemModel(ct1, 10, 10, 0, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 620, 10, 5, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 1230, 10, 10, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 1840, 10, 15, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 310, 820, 20, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 1525, 820, 25, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 10, 1630, 30, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 620, 1630, 35, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 1230, 1630, 40, ImageShapeType.Oval).save();
        new CollageTemplateItemModel(ct1, 1840, 1630, 41, ImageShapeType.Oval).save();
        //CollageTemplateItemModel.mapFrom(ct1, "{\"StartX\": 1840,\"StartY\": 1630,\"Sequence\":10}").save();
    }
}
