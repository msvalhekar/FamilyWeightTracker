package com.mk.familyweighttracker.Activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.mk.familyweighttracker.DbModels.CollageTemplateItemModel;
import com.mk.familyweighttracker.DbModels.CollageTemplateModel;
import com.mk.familyweighttracker.Enums.ImageShapeType;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.ImageUtility;
import com.mk.familyweighttracker.Framework.StorageUtility;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.UserReading;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

import java.util.ArrayList;
import java.util.List;

public class CollagePreviewActivity extends TrackerBaseActivity {
    long _userId;
    int _collageTemplateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_preview_layout);

        Analytic.sendScreenView(Constants.Activities.CollagePreviewActivity);

        initToolbarControl();

        _userId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, -1);
        _collageTemplateId = getIntent().getIntExtra(Constants.ExtraArg.COLLAGE_TEMPLATE_ID, -1);

        setImageView();
    }

    private void setImageView() {
        if(_userId == -1 || _collageTemplateId == -1) return;

        createImage();

        ImageView previewImage = ((ImageView) findViewById(R.id.collage_preview_image));
        previewImage.setImageBitmap( ImageUtility.decodeSampledBitmapFromFile(
            StorageUtility.getTempImagePath(),
            ImageUtility.SixHundred,
            ImageUtility.EightHundred));
    }

    private void createImage() {
        CollageTemplateModel template = CollageTemplateModel.get(_collageTemplateId);
        Bitmap resultBitmap = Bitmap.createBitmap(template.Width, template.Height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();

        List<UserReading> userReadings = new UserService().get(_userId).getReadings(true);
        List<CollageTemplateItemModel> items = template.getItems();
        for (int i = 0; i < items.size(); i++) {
            CollageTemplateItemModel item = items.get(i);
            boolean readingFound = false;
            for (int r = 0; r < userReadings.size(); r++) {
                if(item.Sequence == userReadings.get(r).Sequence) {
                    Bitmap bitmap = userReadings.get(r).getImageAsBitmap(item.Shape, ImageUtility.SixHundred, ImageUtility.EightHundred);
                    canvas.drawBitmap(bitmap, items.get(i).StartX, items.get(i).StartY, paint);
                    readingFound = true;
                    break;
                }
            }
            if(!readingFound) {
                // add text - week N reading not provided
                paint.setColor(Color.GRAY);
                //if(item.Shape == ImageShapeType.Oval)
                    canvas.drawOval(new RectF(items.get(i).StartX+10, items.get(i).StartY+10, items.get(i).StartX+ImageUtility.SixHundred-10, items.get(i).StartY+ImageUtility.EightHundred-10), paint);
                //canvas.drawCircle();
                paint.setColor(Color.WHITE);
                paint.setTextSize(50);
                canvas.drawText(
                        String.format("Week %d", item.Sequence),
                        items.get(i).StartX + 210, items.get(i).StartY + 375, paint
                );
                canvas.drawText(
                        "Not available",
                        items.get(i).StartX + 170, items.get(i).StartY + 450, paint
                );
            }
        }

        ImageUtility.saveImage(StorageUtility.getTempImagePath(), resultBitmap);
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_collage_preview);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
