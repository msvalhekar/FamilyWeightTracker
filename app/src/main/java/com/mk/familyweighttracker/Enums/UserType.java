package com.mk.familyweighttracker.Enums;

/**
 * Created by mvalhekar on 16-11-2016.
 */
public enum UserType {
    Pregnancy ("Pregnancy Weight", "From conceiving to delivery (0 to 40 weeks)"),
    Infant ("Child Growth", "From birth to 3 years of age (0 to 36 months)");

    final static String PREGNANCY = "Pregnancy Weight";
    final static String INFANT = "Child Growth";

    public final String title;
    public final String description;

    UserType(String title, String desc) {
        this.title = title;
        this.description = desc;
    }

    public String toString() {
        return title;
    }

    public static UserType getUserType(String value){
        switch (value) {
            case PREGNANCY: return Pregnancy;
            case INFANT: return Infant;
        }
        return Pregnancy;
    }

}


