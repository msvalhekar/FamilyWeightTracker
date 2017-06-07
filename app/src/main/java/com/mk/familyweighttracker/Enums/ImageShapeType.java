package com.mk.familyweighttracker.Enums;

public enum ImageShapeType {
    Square(1),
    RoundSquare(2),
    Rectangle(3),
    RoundRectangle(4),
    Circle(5),
    Oval(6);

    private final int value;

    ImageShapeType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
