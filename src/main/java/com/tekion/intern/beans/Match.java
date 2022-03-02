package com.tekion.intern.beans;

import com.tekion.intern.enums.Winner;

public class Match {
    private int matchId;
    private int team1Id;
    private int team2Id;
    private int overs;
    private int maxovers;
    private Winner winner;

    public Match(int team1Id, int team2Id, int overs) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.overs = overs;
        this.maxovers = (int)Math.ceil(overs/5.0);
        winner = Winner.STARTED;
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
}
