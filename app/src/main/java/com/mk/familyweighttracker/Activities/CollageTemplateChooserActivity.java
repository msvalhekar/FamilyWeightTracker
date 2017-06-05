package com.mk.familyweighttracker.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        final long userId = getIntent().getLongExtra(Constants.ExtraArg.USER_ID, -1);
        boolean isPregnant = getIntent().getBooleanExtra(Constants.ExtraArg.USER_TYPE, true);

        GridView gridView = ((GridView) findViewById(R.id.collage_template_chooser_grid));
        final CollageTemplateChooserAdapter adapter = new CollageTemplateChooserAdapter(isPregnant);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CollageTemplateModel item = (CollageTemplateModel) adapter.getItem(position);

                Toast.makeText(CollageTemplateChooserActivity.this, String.format("'%s' selected", item.Name), Toast.LENGTH_SHORT).show();
                int templateId = (int)((long) item.getId());

                Intent intent = new Intent(parent.getContext(), CollagePreviewActivity.class);
                intent.putExtra(Constants.ExtraArg.USER_ID, userId);
                intent.putExtra(Constants.ExtraArg.COLLAGE_TEMPLATE_ID, templateId);
                startActivity(intent);
            }
        });
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

            //todo: remove below four lines
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            templateList.get(0).ImageContent = stream.toByteArray();
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
            ((TextView) convertView.findViewById(R.id.item_name)).setText(template.getDisplayName());
            return convertView;
        }
    }
}
