package com.tekion.cricket.enums;

public enum PlayerType {
    BOWLER,
    BATSMAN,
    ALLROUNDER;

    public static PlayerType fromStringToEnum(String playerType){
        switch (playerType) {
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
