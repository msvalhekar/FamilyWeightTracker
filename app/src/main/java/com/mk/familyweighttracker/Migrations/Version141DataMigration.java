package com.mk.familyweighttracker.Migrations;

import com.mk.familyweighttracker.DbModels.UserModel;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Models.User;

import java.io.File;

/**
 * Created by mvalhekar on 24-11-2016.
 */
public class Version141DataMigration extends VersionDataMigration {
    @Override
    int getVersionCode() {
        return 141;
    }

    @Override
    int migrate() {
        renameUserImage();
        return 0;
    }

    private void renameUserImage() {
        // before this version: image file is created with user.jpg name
        // in next version:     image file is renamed to u{userId}.jpg name
        // what to do:          rename image file from user.jpg to u(userId}.jpg
        for (UserModel userModel : UserModel.getAll()) {
            User user = userModel.mapToUser();

            String imageOldPath = String.format("%s/user.jpg", StorageUtility.getImagesDirectory());
            File file = new File(imageOldPath);
            if(file.exists())
                file.renameTo(new File(user.getImagePath()));
        }
    }
}
