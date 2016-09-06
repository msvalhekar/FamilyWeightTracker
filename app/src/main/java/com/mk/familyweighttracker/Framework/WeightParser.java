package com.mk.familyweighttracker.Framework;

/**
 * Created by mvalhekar on 04-09-2016.
 */
public class WeightParser {
    public static final int TWO_PLACES_AFTER_DECIMAL = 0;
    public static final int ONE_PLACE_AFTER_DECIMAL = 1;
    public static final int ONE_PLACE_BEFORE_DECIMAL = 2;
    public static final int TWO_PLACES_BEFORE_DECIMAL = 3;
    public static final int THREE_PLACES_BEFORE_DECIMAL = 4;

    public int TwoPlacesAfterDecimal;
    public int OnePlaceAfterDecimal;
    public int OnePlaceBeforeDecimal;
    public int TwoPlacesBeforeDecimal;
    public int ThreePlacesBeforeDecimal;

    public WeightParser(double weight){
        weight *= 100;
        ThreePlacesBeforeDecimal = (int)weight / 10000;
        weight %= 10000;
        TwoPlacesBeforeDecimal = (int)weight / 1000;
        weight %= 1000;
        OnePlaceBeforeDecimal = (int)weight / 100;
        weight %= 100;
        OnePlaceAfterDecimal = (int)weight / 10;
        weight %= 10;
        TwoPlacesAfterDecimal = (int)weight;
    }

    public double getWeight() {
        return TwoPlacesAfterDecimal * 0.01
                + OnePlaceAfterDecimal * 0.1
                + OnePlaceBeforeDecimal * 1.0
                + TwoPlacesBeforeDecimal * 10.0
                + ThreePlacesBeforeDecimal * 100.0;
    }

    public void setValue(int id, int newValue) {
        switch (id) {
            case WeightParser.TWO_PLACES_AFTER_DECIMAL: TwoPlacesAfterDecimal = newValue; break;
            case WeightParser.ONE_PLACE_AFTER_DECIMAL: OnePlaceAfterDecimal = newValue; break;
            case WeightParser.ONE_PLACE_BEFORE_DECIMAL: OnePlaceBeforeDecimal = newValue; break;
            case WeightParser.TWO_PLACES_BEFORE_DECIMAL: TwoPlacesBeforeDecimal = newValue; break;
            case WeightParser.THREE_PLACES_BEFORE_DECIMAL: ThreePlacesBeforeDecimal = newValue; break;
        }
    }
}
