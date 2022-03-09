package com.tekion.cricket.enums;

public enum PlayerType {
    BOWLER,
    BATSMAN,
    ALLROUNDER;

    public static PlayerType fromStringToEnum(String s){
        switch (s) {
            case "BOWLER":
                return PlayerType.BOWLER;
            case "BATSMAN":
                return PlayerType.BATSMAN;
            case "ALLROUNDER":
                return PlayerType.ALLROUNDER;
            default:
                throw new IllegalArgumentException("Enum Value Illegal");
        }
    }
}
