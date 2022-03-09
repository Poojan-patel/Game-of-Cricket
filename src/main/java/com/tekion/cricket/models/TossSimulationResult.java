package com.tekion.cricket.models;

import com.fasterxml.jackson.annotation.JsonView;

public class TossSimulationResult {
    @JsonView
    private int matchId;
    @JsonView
    private String team1Name;
    @JsonView
    private String team2Name;
    @JsonView
    private String tossWinner;

    public TossSimulationResult(int matchId, String team1Name, String team2Name) {
        this.matchId = matchId;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.tossWinner = team1Name + " will start batting First";
    }
}
