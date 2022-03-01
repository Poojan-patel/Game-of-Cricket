package com.tekion.intern.enums;

public enum TypeOfBowler {
    FAST,
    SPIN,
    MEDIUM,
    NA;

    public static TypeOfBowler fromStringToEnum(String type) {
        if (type == null)
            return TypeOfBowler.NA;
        else if (type.equals("FAST"))
            return TypeOfBowler.FAST;
        else if (type.equals("SPIN"))
            return TypeOfBowler.SPIN;
        else if (type.equals("MEDIUM"))
            return TypeOfBowler.MEDIUM;
        else
            throw new IllegalArgumentException("Enum Value not defined");
    }
}
