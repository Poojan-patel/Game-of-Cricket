package com.tekion.cricket.beans;

import com.tekion.cricket.constants.Common;

public class BallEvent {
    private int eventId;
    private int matchId;
    private int team;
    private int ballNumber;
    private int batsman;
    private int bowler;
    private int score;

    /** {@link com.tekion.cricket.enums.UnfairBallType}
     */
    private String unfairBallType;
    private String wicketType;

    /*
    Constructor for creation of POJO from db data
     */
    public BallEvent(int eventId, int matchId, int team, int ballNumber, int batsman, int bowler, int score, String unfairBallType, String wicketType) {
        this(matchId, team, ballNumber, batsman, bowler, score, unfairBallType, wicketType);
        this.eventId = eventId;
    }

    /*
    Constructor for persisting data in database
     */
    public BallEvent(int matchId, int team, int ballNumber, int batsman, int bowler, int score, String unfairBallType, String wicketType) {
        this.matchId = matchId;
        this.team = team;
        this.ballNumber = ballNumber;
        this.batsman = batsman;
        this.bowler = bowler;
        this.score = score;
        this.unfairBallType = unfairBallType;
        this.wicketType = wicketType;
    }

    public int getBallNumber() {
        return ballNumber;
    }

    public int getOverNumber(){
        return ballNumber / Common.BALLS_IN_ONE_OVER;
    }

    public int getBallNumberForOver(){
        return ballNumber % Common.BALLS_IN_ONE_OVER;
    }

    public int getBatsman() {
        return batsman;
    }

    public int getBowler() {
        return bowler;
    }

    public int getScore() {
        return score;
    }

    public String getUnfairBallType() {
        return unfairBallType;
    }

    public String getWicketType() {
        return wicketType;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getTeam() {
        return team;
    }
}
