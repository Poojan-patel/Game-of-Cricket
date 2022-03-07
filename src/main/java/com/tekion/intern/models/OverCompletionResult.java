package com.tekion.intern.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public class OverCompletionResult implements ScoreBoard{
    @JsonView
    private String teamScore;
    @JsonView
    private String strike;
    @JsonView
    private String nonStrike;
    @JsonView
    private String bowler;
    @JsonView
    private String scoreToChase;
    @JsonView
    private BallLogs ballLogs;
    @JsonView
    private List<PlayerDTO> bowlerForNextOver;
    @JsonView
    private String intermediateResult;

    public OverCompletionResult(){
        ballLogs = new BallLogs();
    }

    public void setTeamScore(String teamScore) {
        this.teamScore = teamScore;
    }

    public void setStrike(String strike) {
        this.strike = strike;
    }

    public void setNonStrike(String nonStrike) {
        this.nonStrike = nonStrike;
    }

    public void setBowler(String bowler) {
        this.bowler = bowler;
    }

    public void setScoreToChase(String scoreToChase) {
        this.scoreToChase = scoreToChase;
    }

    public void appendBallLogs(String ballEvent) {
        ballLogs.appendLog(ballEvent);
    }

    public void setBowlerForNextOver(List<PlayerDTO> bowlerForNextOver) {
        this.bowlerForNextOver = bowlerForNextOver;
    }

    public void setIntermediateResult(String intermediateResult) {
        this.intermediateResult = intermediateResult;
    }
}
