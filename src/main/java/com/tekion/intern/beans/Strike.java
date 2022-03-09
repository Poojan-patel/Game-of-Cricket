package com.tekion.intern.beans;

public class Strike {
    private int strike;
    private int nonStrike;
    private int bowler;
    private int matchId;
    private int teamId;
    private int currentWickets;

    public Strike(int strike, int nonStrike, int bowler, int matchId, int teamId, int currentWickets) {
        this.strike = strike;
        this.nonStrike = nonStrike;
        this.bowler = bowler;
        this.matchId = matchId;
        this.teamId = teamId;
        this.currentWickets = currentWickets;
    }

    public void changeStrike(){
        int tempForSwap = strike;
        strike = nonStrike;
        nonStrike = tempForSwap;
    }

    public int getStrike() {
        return strike;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setNewBatsman(int newBatter) {
        strike = newBatter;
    }

    public boolean isAllOut() {
        return (currentWickets == 10);
    }

    public int getTeamId() {
        return teamId;
    }

    public int getCurrentWickets() {
        return currentWickets;
    }

    public int getNonStrike() {
        return nonStrike;
    }

    public int getBowler() {
        return bowler;
    }

    public int getMaxOrderedPlayer() {
        return Integer.max(strike, nonStrike);
    }

    public void incrementWickets() {
        currentWickets++;
    }
}
