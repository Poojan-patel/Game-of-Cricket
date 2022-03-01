package com.tekion.intern.enums;

public enum PlayerType {
    BOWLER,
    BATSMAN,
    ALLROUNDER;

    public static PlayerType fromStringToEnum(String s){
        if(s.equals("BOWLER"))
            return PlayerType.BOWLER;
        else if(s.equals("BATSMAN"))
            return PlayerType.BATSMAN;
        else if(s.equals("ALLROUNDER"))
            return PlayerType.ALLROUNDER;
        else
            throw new IllegalArgumentException("Enum Value Illegal");
    }
}
