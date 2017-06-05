package com.mk.familyweighttracker.Migrations;

import com.mk.familyweighttracker.DbModels.CollageTemplateItemModel;
import com.mk.familyweighttracker.DbModels.CollageTemplateModel;
import com.mk.familyweighttracker.Enums.CollageTemplateType;

import java.util.Date;

public class Version210DataMigration extends VersionDataMigration {
    @Override
    int getVersionCode() {
        return 210;
    }

    @Override
    int migrate() {
        create3by3BasicTemplate();
        return 0;
    }

    private static void create3by3BasicTemplate() {
        CollageTemplateModel ct1 = new CollageTemplateModel();
        ct1.Name = "4 + 2 + 4 - with heading";
        ct1.TemplateType = CollageTemplateType.Pregnancy;
        ct1.Width = 2450;
        ct1.Height = 2440;
        ct1.CreatedOn = new Date();
        ct1.save();

        new CollageTemplateItemModel(ct1, 10, 10, 0).save();
        new CollageTemplateItemModel(ct1, 620, 10, 5).save();
        new CollageTemplateItemModel(ct1, 1230, 10, 10).save();
        new CollageTemplateItemModel(ct1, 1840, 10, 15).save();
        new CollageTemplateItemModel(ct1, 310, 820, 20).save();
        new CollageTemplateItemModel(ct1, 1525, 820, 25).save();
        new CollageTemplateItemModel(ct1, 10, 1630, 30).save();
        new CollageTemplateItemModel(ct1, 620, 1630, 35).save();
        new CollageTemplateItemModel(ct1, 1230, 1630, 40).save();
        new CollageTemplateItemModel(ct1, 1840, 1630, 41).save();
        //CollageTemplateItemModel.mapFrom(ct1, "{\"StartX\": 1840,\"StartY\": 1630,\"Sequence\":10}").save();
    }
}
