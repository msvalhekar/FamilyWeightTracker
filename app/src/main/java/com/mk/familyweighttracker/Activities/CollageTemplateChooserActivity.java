package com.mk.familyweighttracker.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mk.familyweighttracker.DbModels.CollageTemplateModel;
import com.mk.familyweighttracker.Enums.CollageTemplateType;
import com.mk.familyweighttracker.Framework.Analytic;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Framework.TrackerBaseActivity;
import com.mk.familyweighttracker.R;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class CollageTemplateChooserActivity extends TrackerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_template_chooser_layout);

        Analytic.sendScreenView(Constants.Activities.CollageTemplateChooserActivity);

        initToolbarControl();

//        List<UserReading> userReadings = getUser().getReadings(true);
//        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
//        for (int i = 0; i < userReadings.size(); i++) {
//            bitmaps.add(userReadings.get(i).getImageAsBitmap(true, ImageUtility.SixHundred, ImageUtility.EightHundred));
//        }
//
//        Bitmap resultBitmap = Bitmap.createBitmap(ImageUtility.SixHundred * userReadings.size(), ImageUtility.EightHundred + 10, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(resultBitmap);
//        Paint paint = new Paint();
//        for (int i = 0; i < bitmaps.size(); i++) {
//            canvas.drawBitmap(bitmaps.get(i), ImageUtility.SixHundred * i, 5, paint);
//        }
//        ImageUtility.saveImage("temp.jpg", resultBitmap);

        boolean isPregnant = getIntent().getBooleanExtra(Constants.ExtraArg.USER_TYPE, true);
        GridView gridView = ((GridView) findViewById(R.id.collage_template_chooser_grid));
        gridView.setAdapter(new CollageTemplateChooserAdapter(isPregnant));
    }

    private void initToolbarControl() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_collage_template_chooser);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private class CollageTemplateChooserAdapter extends BaseAdapter {
        List<CollageTemplateModel> templateList;
        public CollageTemplateChooserAdapter(boolean isPregnant) {
            templateList = CollageTemplateModel.getAllFor(isPregnant);

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            templateList.get(0).ImageContent = stream.toByteArray();

//            Bitmap bmpg = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
//            ByteArrayOutputStream streamg = new ByteArrayOutputStream();
//            bmpg.compress(Bitmap.CompressFormat.PNG, 100, streamg);
//            templateList.get(1).ImageContent = streamg.toByteArray();
        }

        @Override
        public int getCount() {
            return templateList.size();
        }

        @Override
        public Object getItem(int position) {
            return templateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collage_template_cell_item, parent, false);
            }

            CollageTemplateModel template = ((CollageTemplateModel) getItem(position));
            ((ImageView) convertView.findViewById(R.id.item_image)).setImageBitmap(template.getImageBitmap());
            ((TextView) convertView.findViewById(R.id.item_name)).setText(template.Name);
            return convertView;
        }
    }
}
