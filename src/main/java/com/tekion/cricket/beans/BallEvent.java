package com.tekion.cricket.beans;

import com.tekion.cricket.constants.Common;

public class BallEvent {
    private String eventId;
    private String matchId;
    private String battingTeam;
    private int batsman;
    private int ballNumber;
    private String bowlingTeam;
    private int bowler;
    private int score;

    /** {@link com.tekion.cricket.enums.UnfairBallType}
     */
    private String unfairBallType;
    private String wicketType;

    /*
    Constructor for creation of POJO from db data
     */

    public BallEvent(String eventId, String matchId, String battingTeam, int batsman, int ballNumber, String bowlingTeam, int bowler, int score, String unfairBallType, String wicketType) {
        this(matchId, battingTeam, batsman, ballNumber, bowlingTeam, bowler, score, unfairBallType, wicketType);
        this.eventId = eventId;
    }

    /*
        Constructor for persisting data in database
         */
    public BallEvent(String matchId, String battingTeam, int batsman, int ballNumber, String bowlingTeam, int bowler, int score, String unfairBallType, String wicketType) {
        this.matchId = matchId;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
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

    public String getMatchId() {
        return matchId;
    }

    public String getBattingTeam() {
        return battingTeam;
    }

    public String getBowlingTeam() {
        return bowlingTeam;
    }
}
