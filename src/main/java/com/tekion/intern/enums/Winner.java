package com.tekion.intern.enums;

public enum Winner {
    TEAM1,
    TEAM2,
    TIE,
    TOSS_LEFT,
    TEAM1_BATTING,
    TEAM2_BATTING;

    public static Winner fromString(String winner) {
        switch (winner){
            case "TIE": return Winner.TIE;
            case "TEAM1": return Winner.TEAM1;
            case "TEAM2": return Winner.TEAM2;
            case "TEAM1_BATTING": return Winner.TEAM1_BATTING;
            case "TEAM2_BATTING": return Winner.TEAM2_BATTING;
            case "TOSS_LEFT": return Winner.TOSS_LEFT;
            default: throw new IllegalStateException("State Does not Exists");
        }
    }
}
