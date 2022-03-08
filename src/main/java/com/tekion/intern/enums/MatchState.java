package com.tekion.intern.enums;

public enum MatchState {
    TEAM1,
    TEAM2,
    TIE,
    TOSS_LEFT,
    TEAM1_BATTING,
    TEAM2_BATTING;

    public static MatchState fromString(String winner) {
        switch (winner){
            case "TIE":
                return MatchState.TIE;
            case "TEAM1":
                return MatchState.TEAM1;
            case "TEAM2":
                return MatchState.TEAM2;
            case "TEAM1_BATTING":
                return MatchState.TEAM1_BATTING;
            case "TEAM2_BATTING":
                return MatchState.TEAM2_BATTING;
            case "TOSS_LEFT":
                return MatchState.TOSS_LEFT;
            default:
                throw new IllegalStateException("State Does not Exists");
        }
    }
}
