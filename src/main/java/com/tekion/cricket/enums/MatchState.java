package com.tekion.cricket.enums;

public enum MatchState {
    TEAM1_WON,
    TEAM2_WON,
    TIE,
    TOSS_LEFT,
    TEAM1_BATTING,
    TEAM2_BATTING;

    public static MatchState fromString(String winner) {
        switch (winner){
            case "TIE":
                return MatchState.TIE;
            case "TEAM1_WON":
                return MatchState.TEAM1_WON;
            case "TEAM2_WON":
                return MatchState.TEAM2_WON;
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
