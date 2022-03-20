package com.tekion.cricket.beans;

import com.tekion.cricket.enums.MatchState;

import java.util.UUID;

public class Match {
    private String matchId;
    private String team1Id;
    private String team2Id;
    private int overs;
    private int maxOvers;

    /** {@link com.tekion.cricket.enums.MatchState}
     */
    private String matchState;

    /*
    Constructor for persisting data in database
     */
    public Match(String team1Id, String team2Id, int overs) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.overs = overs;
        this.maxOvers = (int)Math.ceil(overs/5.0);
        matchState = MatchState.TOSS_LEFT.toString();
        this.matchId = UUID.randomUUID().toString();
    }

    public Match() {
    }

    /*
        Constructor for creation of POJO from db data
    */
    public Match(String matchId, String team1Id, String team2Id, int overs, int maxOvers, String matchState) {
        this.matchId = matchId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.overs = overs;
        this.maxOvers = maxOvers;
        this.matchState = matchState;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setOvers(int overs) {
        this.overs = overs;
    }

    public void setMaxOvers(int maxOvers) {
        this.maxOvers = maxOvers;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public int getOvers() {
        return overs;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchState() {
        return matchState;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    public void setMatchState(String matchState) {
        this.matchState = matchState;
    }

    public int getMaxOvers() {
        return maxOvers;
    }

}
