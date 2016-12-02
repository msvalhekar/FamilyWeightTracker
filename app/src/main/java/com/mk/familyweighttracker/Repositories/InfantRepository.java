package com.mk.familyweighttracker.Repositories;

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.Models.MonthGrowthRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 29-11-2016.
 */
public class InfantRepository {
    static List<MonthGrowthRange> boyRecords;
    static List<MonthGrowthRange> girlRecords;

    public InfantRepository() {
        loadBoysMonthGrowthRangeRecords();
        loadGirlsMonthGrowthRangeRecords();
    }

    // http://www.babycenter.in/a1052194/baby-boys-growth-chart-0-to-12-months
    // http://www.babycenter.in/a25010657/boys-growth-chart-12-to-24-months
    // http://www.babycenter.in/a25010659/boys-growth-chart-24-to-36-months
    // http://www.babycenter.in/a1052197/baby-girls-growth-chart-0-to-12-months
    // http://www.babycenter.in/a25010661/girls-growth-chart-12-to-24-months
    // http://www.babycenter.in/a25010663/girls-growth-chart-24-to-36-months
    private void loadBoysMonthGrowthRangeRecords() {
        if (boyRecords != null) return;

        boyRecords = new ArrayList();

        boyRecords.add(new MonthGrowthRange(0, 46.3, 53.4, 2.5, 4.3, 32.1, 36.9));
        boyRecords.add(new MonthGrowthRange(1, 51.1, 58.4, 3.4, 5.7, 35.1, 39.5));
        boyRecords.add(new MonthGrowthRange(2, 54.7, 62.2, 4.4, 7.0, 36.9, 41.3));
        boyRecords.add(new MonthGrowthRange(3, 57.6, 65.3, 5.1, 7.9, 38.3, 42.7));
        boyRecords.add(new MonthGrowthRange(4, 60.0, 67.8, 5.6, 8.6, 39.4, 43.9));
        boyRecords.add(new MonthGrowthRange(5, 61.9, 69.9, 6.1, 9.2, 40.3, 44.8));
        boyRecords.add(new MonthGrowthRange(6, 63.6, 71.6, 6.4, 9.7, 41.0, 45.6));
        boyRecords.add(new MonthGrowthRange(7, 65.1, 73.2, 6.7, 10.2, 41.7, 46.3));
        boyRecords.add(new MonthGrowthRange(8, 66.5, 74.7, 7.0, 10.5, 42.2, 46.9));
        boyRecords.add(new MonthGrowthRange(9, 67.7, 76.2, 7.2, 10.9, 42.6, 47.4));
        boyRecords.add(new MonthGrowthRange(10, 69.0, 77.6, 7.5, 11.2, 43.0, 47.8));
        boyRecords.add(new MonthGrowthRange(11, 70.2, 78.9, 7.4, 11.5, 43.4, 48.2));
        boyRecords.add(new MonthGrowthRange(12, 71.3, 80.2, 7.8, 11.8, 43.6, 48.5));
        boyRecords.add(new MonthGrowthRange(13, 72.4, 81.5, 8.0, 12.1, 43.9, 48.8));
        boyRecords.add(new MonthGrowthRange(14, 73.4, 82.7, 8.2, 12.4, 44.1, 49.0));
        boyRecords.add(new MonthGrowthRange(15, 74.4, 83.9, 8.4, 12.7, 44.3, 49.3));
        boyRecords.add(new MonthGrowthRange(16, 75.4, 85.1, 8.5, 12.9, 44.5, 49.5));
        boyRecords.add(new MonthGrowthRange(17, 76.3, 86.2, 8.7, 13.2, 44.7, 49.7));
        boyRecords.add(new MonthGrowthRange(18, 77.2, 87.3, 8.9, 13.5, 44.9, 49.9));
        boyRecords.add(new MonthGrowthRange(19, 78.1, 88.4, 9.0, 13.7, 45.0, 50.0));
        boyRecords.add(new MonthGrowthRange(20, 78.9, 89.5, 9.2, 14.0, 45.2, 50.2));
        boyRecords.add(new MonthGrowthRange(21, 79.7, 90.5, 9.3, 14.3, 45.3, 50.4));
        boyRecords.add(new MonthGrowthRange(22, 80.5, 91.6, 9.5, 14.5, 45.4, 50.5));
        boyRecords.add(new MonthGrowthRange(23, 81.3, 92.6, 9.7, 14.8, 45.6, 50.7));
        boyRecords.add(new MonthGrowthRange(24, 82.1, 93.6, 9.8, 15.1, 45.7, 50.8));
        boyRecords.add(new MonthGrowthRange(25, 82.1, 93.8, 10.0, 15.3, 45.8, 50.9));
        boyRecords.add(new MonthGrowthRange(26, 82.8, 94.8, 10.1, 15.6, 45.9, 51.1));
        boyRecords.add(new MonthGrowthRange(27, 83.5, 95.7, 10.2, 15.9, 46.0, 51.2));
        boyRecords.add(new MonthGrowthRange(28, 84.2, 96.6, 10.4, 16.1, 46.1, 51.3));
        boyRecords.add(new MonthGrowthRange(29, 84.9, 97.5, 10.5, 16.4, 46.2, 51.4));
        boyRecords.add(new MonthGrowthRange(30, 85.5, 98.3, 10.7, 16.6, 46.3, 51.6));
        boyRecords.add(new MonthGrowthRange(31, 86.2, 99.2, 10.8, 16.9, 46.4, 51.7));
        boyRecords.add(new MonthGrowthRange(32, 86.8, 100.0, 10.9, 17.1, 46.5, 51.8));
        boyRecords.add(new MonthGrowthRange(33, 87.4, 100.8, 11.1, 17.3, 46.6, 51.9));
        boyRecords.add(new MonthGrowthRange(34, 88.0, 101.5, 11.2, 17.6, 46.6, 52.0));
        boyRecords.add(new MonthGrowthRange(35, 88.5, 102.3, 11.3, 17.8, 46.7, 52.0));
        boyRecords.add(new MonthGrowthRange(36, 89.1, 103.1, 11.4, 18.0, 46.8, 52.1));
    }

    private void loadGirlsMonthGrowthRangeRecords() {
        if (girlRecords != null) return;

        girlRecords = new ArrayList();

        girlRecords.add(new MonthGrowthRange(0, 45.6, 52.7, 2.4, 4.2, 31.7, 36.1));
        girlRecords.add(new MonthGrowthRange(1, 50.0, 57.4, 3.2, 5.4, 34.3, 38.8));
        girlRecords.add(new MonthGrowthRange(2, 53.2, 60.9, 4.0, 6.5, 36.0, 40.5));
        girlRecords.add(new MonthGrowthRange(3, 55.8, 63.8, 4.6, 7.4, 37.2, 41.9));
        girlRecords.add(new MonthGrowthRange(4, 58.0, 66.2, 5.1, 8.1, 38.2, 43.0));
        girlRecords.add(new MonthGrowthRange(5, 59.9, 68.2, 5.5, 8.7, 39.0, 43.9));
        girlRecords.add(new MonthGrowthRange(6, 61.5, 70.0, 5.8, 9.2, 39.7, 44.6));
        girlRecords.add(new MonthGrowthRange(7, 62.9, 71.6, 6.1, 9.6, 40.4, 45.3));
        girlRecords.add(new MonthGrowthRange(8, 64.3, 73.2, 6.3, 10.0, 40.9, 45.9));
        girlRecords.add(new MonthGrowthRange(9, 65.6, 74.7, 6.6, 10.4, 41.3, 46.3));
        girlRecords.add(new MonthGrowthRange(10, 66.8, 76.1, 6.8, 10.7, 41.7, 46.8));
        girlRecords.add(new MonthGrowthRange(11, 68.0, 77.5, 7.0, 11.0, 42.0, 47.1));
        girlRecords.add(new MonthGrowthRange(12, 69.2, 78.9, 7.1, 11.3, 42.3, 47.5));
        girlRecords.add(new MonthGrowthRange(13, 70.3, 80.2, 7.3, 11.6, 42.6, 47.7));
        girlRecords.add(new MonthGrowthRange(14, 71.3, 81.4, 7.5, 11.9, 42.9, 48.0));
        girlRecords.add(new MonthGrowthRange(15, 72.4, 82.7, 7.7, 12.2, 43.1, 48.2));
        girlRecords.add(new MonthGrowthRange(16, 73.3, 83.9, 7.8, 12.5, 43.3, 48.5));
        girlRecords.add(new MonthGrowthRange(17, 74.3, 85.0, 8.0, 12.7, 43.5, 48.7));
        girlRecords.add(new MonthGrowthRange(18, 75.2, 86.2, 8.2, 13.0, 43.6, 48.8));
        girlRecords.add(new MonthGrowthRange(19, 76.2, 87.3, 8.3, 13.3, 43.8, 49.0));
        girlRecords.add(new MonthGrowthRange(20, 77.0, 88.4, 8.5, 13.5, 44.0, 49.2));
        girlRecords.add(new MonthGrowthRange(21, 77.9, 89.4, 8.7, 13.8, 44.1, 49.4));
        girlRecords.add(new MonthGrowthRange(22, 78.7, 90.5, 8.8, 14.1, 44.3, 49.5));
        girlRecords.add(new MonthGrowthRange(23, 79.6, 91.5, 9.0, 14.3, 44.4, 49.7));
        girlRecords.add(new MonthGrowthRange(24, 80.3, 92.5, 9.2, 14.6, 44.6, 49.8));
        girlRecords.add(new MonthGrowthRange(25, 80.4, 92.8, 9.3, 14.9, 44.7, 49.9));
        girlRecords.add(new MonthGrowthRange(26, 81.2, 93.7, 9.5, 15.2, 44.8, 50.1));
        girlRecords.add(new MonthGrowthRange(27, 81.9, 94.6, 9.6, 15.4, 44.9, 50.2));
        girlRecords.add(new MonthGrowthRange(28, 82.6, 95.6, 9.8, 15.7, 45.1, 50.3));
        girlRecords.add(new MonthGrowthRange(29, 83.4, 96.4, 10.0, 16.0, 45.2, 50.5));
        girlRecords.add(new MonthGrowthRange(30, 84.0, 97.3, 10.1, 16.2, 45.3, 50.6));
        girlRecords.add(new MonthGrowthRange(31, 84.7, 98.2, 10.3, 16.5, 45.4, 50.7));
        girlRecords.add(new MonthGrowthRange(32, 85.4, 99.0, 10.4, 16.8, 45.5, 50.8));
        girlRecords.add(new MonthGrowthRange(33, 86.0, 99.8, 10.5, 17.0, 45.6, 50.9));
        girlRecords.add(new MonthGrowthRange(34, 86.7, 100.6, 10.7, 17.3, 45.7, 51));
        girlRecords.add(new MonthGrowthRange(35, 87.3, 101.4, 10.8, 17.6, 45.8, 51.1));
        girlRecords.add(new MonthGrowthRange(36, 87.9, 102.2, 11.0, 17.8, 45.9, 51.2));
    }

    public List<MonthGrowthRange> createMonthGrowthRangeTableFor(boolean forBoy, WeightUnit weightUnit, HeightUnit heightUnit, HeightUnit headCirUnit) {
        List<MonthGrowthRange> monthGrowthRangeList = forBoy ? boyRecords : girlRecords;

        List<MonthGrowthRange> result = new ArrayList<>();
        for (MonthGrowthRange range : monthGrowthRangeList) {
            result.add(range.getMonthGrowthRangeFor(weightUnit, heightUnit, headCirUnit));
        }
        return result;
    }
}
