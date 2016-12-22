package com.mk.familyweighttracker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.mk.familyweighttracker.Activities.WhatIsNewActivity;
import com.mk.familyweighttracker.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by mvalhekar on 16-12-2016.
 */

public class WhatIsNewListAdapter extends BaseExpandableListAdapter {
    Activity context;
    List<WhatIsNewActivity.IWhatIsNew> list;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    public WhatIsNewListAdapter(Activity context, List<WhatIsNewActivity.IWhatIsNew> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getFeatureMap().size();
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getFeatureMap().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup parent) {

        WhatIsNewActivity.IWhatIsNew whatIsNew = list.get(groupPosition);

        if(convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.whats_new_group_item, null);
        }

        ((TextView) convertView.findViewById(R.id.group_item_version))
                .setText(String.format("%s - %s",
                        whatIsNew.getVersion(),
                        simpleDateFormat.format(whatIsNew.getReleaseDate())));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup parent) {
        WhatIsNewActivity.IWhatIsNew whatIsNew = list.get(groupPosition);
        WhatIsNewActivity.WhatIsNewMessage whatIsNewMessage = whatIsNew.getFeatureMap().get(childPosition);
        String title = whatIsNewMessage.title;
        String description = whatIsNewMessage.description;

        if(convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.whats_new_group_child_item, null);
        }

        ((TextView) convertView.findViewById(R.id.child_item_title)).setText(title);
        ((TextView) convertView.findViewById(R.id.child_item_description)).setText(description);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
