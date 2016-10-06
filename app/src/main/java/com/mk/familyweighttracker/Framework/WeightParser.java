package com.mk.familyweighttracker.Framework;

/**
 * Created by mvalhekar on 04-09-2016.
 */
public class WeightParser {
    public static final int HUNDREDTHS_PLACE = 0;
    public static final int TENTHS_PLACE = 1;
    public static final int ONES_PLACE = 2;
    public static final int TENS_PLACE = 3;
    public static final int HUNDREDS_PLACE = 4;

    public int HundredthsValue;
    public int TenthsValue;
    public int OnesValue;
    public int TensValue;
    public int HundredsValue;

    public WeightParser(double number){
        number *= 100;
        HundredsValue = (int)number / 10000;
        number %= 10000;
        TensValue = (int)number / 1000;
        number %= 1000;
        OnesValue = (int)number / 100;
        number %= 100;
        TenthsValue = (int)number / 10;
        number %= 10;
        HundredthsValue = (int)number;
    }

    public double getNumber() {
        return HundredthsValue * 0.01
                + TenthsValue * 0.1
                + OnesValue * 1.0
                + TensValue * 10.0
                + HundredsValue * 100.0;
    }

    public void setValue(int id, int newValue) {
        switch (id) {
            case WeightParser.HUNDREDTHS_PLACE: HundredthsValue = newValue; break;
            case WeightParser.TENTHS_PLACE: TenthsValue = newValue; break;
            case WeightParser.ONES_PLACE: OnesValue = newValue; break;
            case WeightParser.TENS_PLACE: TensValue = newValue; break;
            case WeightParser.HUNDREDS_PLACE: HundredsValue = newValue; break;
        }
    }
}
