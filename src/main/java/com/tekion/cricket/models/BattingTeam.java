package com.tekion.cricket.models;

public class BattingTeam {
    private String name;
    private int currentScore;
    private int playedBalls;
    private int teamId;
    private int scoreToChase;

    public BattingTeam(String name, int currentScore, int playedBalls, int teamId) {
        this.name = name;
        this.currentScore = currentScore;
        this.playedBalls = playedBalls;
        this.teamId = teamId;
    }

    public void setScoreToChase(int scoreToChase) {
        this.scoreToChase = scoreToChase;
    }

    public String getTeamName() {
        return name;
    }

    public int getTeamScore() {
        return currentScore;
    }

    public int getPlayedBalls() {
        return playedBalls;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getScoreToChase() {
        return scoreToChase;
    }

    public void incrementTotalBalls() {
        playedBalls++;
    }

    public int getCurrentOver() {
        return playedBalls/6;
    }

    public void incrementRuns(int runScored){
        currentScore += runScored;
    }

    public String convertToLog(int currentWickets) {
        return String.format("%s: %d/%d in %d.%d overs", name, currentScore, currentWickets, playedBalls/6, playedBalls%6);
    }
}
