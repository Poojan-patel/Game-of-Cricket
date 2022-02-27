package com.tekion.intern.enums;

import com.tekion.intern.game.Player;

public enum TypeOfBowler {
    FAST,
    SPIN,
    MEDIUM,
    NA;

    public static TypeOfBowler fromStringToEnum(String type) {
        if (type.equals("FAST"))
            return TypeOfBowler.FAST;
        else if (type.equals("SPIN"))
            return TypeOfBowler.SPIN;
        else if (type.equals("MEDIUM"))
            return TypeOfBowler.MEDIUM;
        else
            return TypeOfBowler.NA;
    }
}
