package com.mk.familyweighttracker.Enums;

/**
 * Created by mvalhekar on 16-11-2016.
 */
public enum UserType {
    Pregnancy ("Pregnancy Weight"),
    Infant ("Infant Growth");

    final static String PREGNANCY = "Pregnancy Weight";
    final static String INFANT = "Infant Growth";

    final String value;
    UserType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static UserType getUserType(String value){
        switch (value) {
            case PREGNANCY: return Pregnancy;
            case INFANT: return Infant;
        }
        return Pregnancy;
    }
}


