package com.tekion.cricket.beans;

import com.tekion.cricket.constants.Common;

public class Strike {
    private String matchId;
    private String battingTeam;
    private int strike;
    private int nonStrike;
    private String bowlingTeam;
    private int bowler;
    private int currentWickets;

    /*
    Constructor for creation of POJO from db data
     */
    public Strike(int strike, int nonStrike, int bowler, String matchId, String battingTeam, String bowlingTeam, int currentWickets) {
        this.strike = strike;
        this.nonStrike = nonStrike;
        this.bowler = bowler;
        this.matchId = matchId;
        this.battingTeam = battingTeam;
        this.currentWickets = currentWickets;
        this.bowlingTeam = bowlingTeam;
    }

    /*
    Constructor for persisting data in database
     */
    public Strike(int strike, int nonStrike, String matchId, String battingTeam, String bowlingTeam) {
        this.strike = strike;
        this.nonStrike = nonStrike;
        this.matchId = matchId;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
    }

    public Strike() {
    }

    public void changeStrike(){
        int tempForSwap = strike;
        strike = nonStrike;
        nonStrike = tempForSwap;
    }

    public int getStrike() {
        return strike;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setStrike(int newBatter) {
        strike = newBatter;
    }

    public boolean isAllOut() {
        return (currentWickets == Common.NUM_OF_WICKETS);
    }

    public String getBattingTeam() {
        return battingTeam;
    }

    public int getCurrentWickets() {
        return currentWickets;
    }

    public String getBowlingTeam() {
        return bowlingTeam;
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

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setBattingTeam(String battingTeam) {
        this.battingTeam = battingTeam;
    }

    public void setNonStrike(int nonStrike) {
        this.nonStrike = nonStrike;
    }

    public void setBowlingTeam(String bowlingTeam) {
        this.bowlingTeam = bowlingTeam;
    }

    public void setBowler(int bowler) {
        this.bowler = bowler;
    }

    public void setCurrentWickets(int currentWickets) {
        this.currentWickets = currentWickets;
    }
}
