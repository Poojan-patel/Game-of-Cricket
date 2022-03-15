package com.tekion.cricket.beans;

import com.tekion.cricket.enums.UnfairBallType;

public class BallEvent {
    private int eventId;
    private int matchId;
    private int team;
    private int ballNumber;
    private int batsman;
    private int bowler;
    private int score;
    private UnfairBallType unfairBallType;
    private String wicketType;

    public BallEvent(int eventId, int matchId, int team, int ballNumber, int batsman, int bowler, int score, String unfairBallType, String wicketType) {
        this.eventId = eventId;
        this.matchId = matchId;
        this.team = team;
        this.ballNumber = ballNumber;
        this.batsman = batsman;
        this.bowler = bowler;
        this.score = score;
        this.unfairBallType = UnfairBallType.fromStringToEnum(unfairBallType);
        this.wicketType = wicketType;
    }

    public int getBallNumber() {
        return ballNumber;
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

    public UnfairBallType getUnfairBallType() {
        return unfairBallType;
    }

    public String getWicketType() {
        return wicketType;
    }
}
