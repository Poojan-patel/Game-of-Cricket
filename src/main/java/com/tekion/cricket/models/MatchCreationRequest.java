package com.tekion.cricket.models;

public class MatchCreationRequest {
    private String team1Id;
    private String team2Id;
    private int overs;

    public String getTeam1Id() {
        return team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public int getOvers() {
        return overs;
    }
}
