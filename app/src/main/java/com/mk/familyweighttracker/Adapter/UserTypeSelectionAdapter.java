package com.mk.familyweighttracker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mk.familyweighttracker.Enums.UserType;
import com.mk.familyweighttracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 17-12-2016.
 */

public class UserTypeSelectionAdapter extends BaseAdapter {
    Context context;
    List<UserType> userTypeList;

    public UserTypeSelectionAdapter(Context context) {
        this.context = context;

        userTypeList = new ArrayList<>();
        for (UserType userType : UserType.values()) {
            userTypeList.add(userType);
        }
    }

    @Override
    public int getCount() {
        return userTypeList.size();
    }

    @Override
    public Object getItem(int i) {
        return userTypeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if(convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.user_type_selection_item, null);
        }

        ((TextView) convertView.findViewById(R.id.item_title))
                .setText(userTypeList.get(i).title);
        ((TextView) convertView.findViewById(R.id.item_description))
                .setText(userTypeList.get(i).description);

        return convertView;
    }
}
