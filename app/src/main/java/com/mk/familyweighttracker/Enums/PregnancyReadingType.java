package com.mk.familyweighttracker.Enums;

/**
 * Created by mvalhekar on 16-11-2016.
 */
public enum PregnancyReadingType {
    PrePregnancy ("Pre-pregnancy"),
    Pregnancy ("Pregnancy (week 1-40)"),
    Delivery ("Delivery");

    final static String PREPREGNANCY = "Pre-pregnancy";
    final static String PREGNANCY = "Pregnancy (week 1-40)";
    final static String DELIVERY = "Delivery";

    final String value;
    PregnancyReadingType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static PregnancyReadingType getType(String value){
        switch (value) {
            case PREPREGNANCY: return PrePregnancy;
            case PREGNANCY: return Pregnancy;
            case DELIVERY: return Delivery;
        }
        return Pregnancy;
    }
}


