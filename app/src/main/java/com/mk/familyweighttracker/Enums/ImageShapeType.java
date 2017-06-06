package com.mk.familyweighttracker.Enums;

public enum ImageShapeType {
    Rectangle(1),
    Circle(2),
    Oval(3),
    Square(4);

    private final int value;

    ImageShapeType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
