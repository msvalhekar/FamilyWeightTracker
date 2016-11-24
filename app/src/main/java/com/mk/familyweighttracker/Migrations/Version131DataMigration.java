package com.mk.familyweighttracker.Migrations;

import com.mk.familyweighttracker.DbModels.UserModel;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Services.UserService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mvalhekar on 24-11-2016.
 */
public class Version131DataMigration extends VersionDataMigration {
    @Override
    int getVersionCode() {
        return 131;
    }

    @Override
    int migrate() {
        saveUserImageAsFile();
        return 0;
    }

    private void saveUserImageAsFile() {
        // before this version: imageBytes were used to store user image
        // in next version:     file is created with user.jpg name
        // what to do:          if imageBytes exists, then save it as file
        for (UserModel userModel : UserModel.getAll()) {
            User user = userModel.mapToUser();

            if (user == null || user.imageBytes == null)
                continue;

            String imagePath = String.format("%s/user.jpg", StorageUtility.getImagesDirectory());

            try {
                FileOutputStream outputStream = new FileOutputStream(imagePath);
                outputStream.write(user.imageBytes, 0, user.imageBytes.length);
                outputStream.flush();
                outputStream.close();

                user.imageBytes = null;
                new UserService().add(user);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
