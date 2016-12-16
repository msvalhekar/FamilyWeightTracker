package com.mk.familyweighttracker.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.mk.familyweighttracker.Activities.PregnantUserDetailActivity;
import com.mk.familyweighttracker.Framework.Constants;
import com.mk.familyweighttracker.Models.MonthGrowthRange;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.Models.WeekWeightGainRange;
import com.mk.familyweighttracker.Repositories.InfantRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PregnantUserBaseFragment extends Fragment {

    public PregnantUserBaseFragment() {
        // Required empty public constructor
    }

    protected long getUserId() {
        return ((PregnantUserDetailActivity) getActivity()).getUserId();
    }

    protected User getUser() {
        return ((PregnantUserDetailActivity) getActivity()).getUser();
    }

    protected void onUserDataChange() {
        ((PregnantUserDetailActivity) getActivity()).onUserDataChange();
    }

    protected List<WeekWeightGainRange> getWeightGainRangeTable() {
        return ((PregnantUserDetailActivity) getActivity()).getWeightGainRangeTable();
    }

    public WeekWeightGainRange getWeightGainRangeFor(long weekNumber) {
        return ((PregnantUserDetailActivity) getActivity()).getPregnancyWeightGainRangeFor(weekNumber);
    }

    protected List<MonthGrowthRange> getMonthGrowthRangeTable() {
        return ((PregnantUserDetailActivity) getActivity()).getMonthGrowthRangeTable();
    }

    public MonthGrowthRange getMonthGrowthRangeFor(long monthNumber) {
        return ((PregnantUserDetailActivity) getActivity()).getInfantMonthGrowthRangeFor(monthNumber);
    }

    public boolean showShareChartMenu() {
        return false;
    }
    public boolean showHelpMenu() {
        return false;
    }

    public void onShareChartMenu() { }
    public void onHelpMenu() { }

    protected void saveBitmapAs(Map<String, Bitmap> map) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (String filePath : map.keySet()) {
            saveBitmapAs(map.get(filePath), filePath);

            //// TODO: 14-12-2016 http://stackoverflow.com/questions/25846496/intent-share-action-send-multiple-facebook-not-working
            uris.add(Uri.fromFile(new File(filePath)));
        }

        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, getUser().getShareChartSubject());
        intent.setType("image/jpeg");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(Intent.createChooser(intent, getUser().getShareChartSubject()));
    }

    private void saveBitmapAs(Bitmap bitmap, String filePath) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        try {
            File newFile = new File(filePath);
            if(!newFile.exists())
                newFile.createNewFile();

            FileOutputStream fo = new FileOutputStream(newFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
