package com.tekion.cricket.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

public class MatchResult implements ScoreBoard{
    @JsonView
    private String winner;

    @JsonView
    private List<String> teamScores;

    @JsonView
    private List<String> battingStats;
    @JsonView
    private List<String> bowlingStats;
    public MatchResult() {
        teamScores = new ArrayList<>();
        battingStats = new ArrayList<>();
        bowlingStats = new ArrayList<>();
    }

    public void appendTeamScores(String teamScore){
        teamScores.add(teamScore);
    }

    public void appendBattingStats(String battingStat){
        battingStats.add(battingStat);
    }

    public void appendBowlingStats(String bowlingStat){
        bowlingStats.add(bowlingStat);
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
