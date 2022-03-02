package com.tekion.intern.models;

import com.fasterxml.jackson.annotation.JsonView;

public class MatchCreationResponse {
    @JsonView
    private int matchId;
    @JsonView
    private String team1Name;
    @JsonView
    private String team2Name;
    @JsonView
    private int overs;

    public MatchCreationResponse(int matchId, String team1Name, String team2Name, int overs) {
        this.matchId = matchId;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.overs = overs;
    }
}
