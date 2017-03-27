package com.mk.familyweighttracker.Enums;

/**
 * Created by mvalhekar on 02-04-2016.
 */
public enum CollageTemplateType {
    Pregnancy(0),
    Infant(1);

    private final int value;

    CollageTemplateType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
