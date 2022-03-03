package com.tekion.intern.beans;

import com.tekion.intern.enums.Winner;

public class Match {
    private int matchId;
    private int team1Id;
    private int team2Id;
    private int overs;
    private int maxovers;
    private Winner matchState;

    public Match(int team1Id, int team2Id, int overs) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.overs = overs;
        this.maxovers = (int)Math.ceil(overs/5.0);
        matchState = Winner.TOSS_LEFT;
    }

    public Match(int matchId, int team1Id, int team2Id, int overs, int maxovers, String matchState) {
        this.matchId = matchId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.overs = overs;
        this.maxovers = maxovers;
        this.matchState = Winner.fromString(matchState);
    }

    public int getTeam1Id() {
        return team1Id;
    }

    public int getTeam2Id() {
        return team2Id;
    }

    public int getOvers() {
        return overs;
    }

    public int getMatchId() {
        return matchId;
    }

    public Winner getMatchState() {
        return matchState;
    }

    public void setTeam1Id(int team1Id) {
        this.team1Id = team1Id;
    }

    public void setTeam2Id(int team2Id) {
        this.team2Id = team2Id;
    }

    public void setMatchState(Winner matchState) {
        this.matchState = matchState;
    }
}
