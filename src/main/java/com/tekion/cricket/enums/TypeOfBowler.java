package com.tekion.cricket.enums;

public enum TypeOfBowler {
    FAST,
    SPIN,
    MEDIUM,
    NA;

    public static TypeOfBowler fromStringToEnum(String type) {
        if(type == null) {
            return TypeOfBowler.NA;
        }
        switch (type) {
            case "NA":
                return TypeOfBowler.NA;
            case "FAST":
                return TypeOfBowler.FAST;
            case "SPIN":
                return TypeOfBowler.SPIN;
            case "MEDIUM":
                return TypeOfBowler.MEDIUM;
            default:
                throw new IllegalArgumentException("Enum Value not defined");
        }
    }
}
