package com.tekion.intern.game;

class Player {
    enum PlayerType{
        BOWLER,
        BATSMAN
    }
    private String name;
    private int score;
    private int ballsPlayed;
    private int playerOrder;
    private PlayerType playerType;
    private int[] scoreDistribution;
    private int maxOversCanBeThrown;
    private int currentlyThrownOvers;
    private int wicketsTaken;

    public Player(String name, String type, int playerOrder, int maxOversCanBeThrown){
        this.name = name;
        this.score = 0;
        this.ballsPlayed = 0;
        this.playerOrder = playerOrder;
        if(type.equals("BOWLER")){
            playerType = PlayerType.BOWLER;
        }
        else{
            playerType = PlayerType.BATSMAN;
        }
        scoreDistribution = new int[7];
        this.maxOversCanBeThrown = maxOversCanBeThrown;
        currentlyThrownOvers = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int score) {
        this.score += score;
        scoreDistribution[score]++;
    }

    public int getBallsPlayed() {
        return ballsPlayed;
    }

    public void incrementBallsPlayed() {
        this.ballsPlayed++;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public boolean hasExaustedOvers() {
        return (currentlyThrownOvers == maxOversCanBeThrown);
    }

    public void incrementTotalThrownOvers(){
        currentlyThrownOvers++;
    }

    public void incrementWicketsTaken() {
        wicketsTaken++;
    }

    @Override
    public String toString() {
        String scoreDistributionToString = "[";
        for(int i = 0; i < 6; i++)
            scoreDistributionToString += (i + ":" + scoreDistribution[i] + ", ");
        scoreDistributionToString += ("6:" + scoreDistribution[6] + "]");
        String objectAsString = String.format("%s: %d runs in %d balls, scorewise:%s", name, score, ballsPlayed, scoreDistributionToString);
        if(this.playerType == PlayerType.BOWLER){
            objectAsString += String.format(" Overs taken(rounded):%d/%d, Wickets Taken:%d", currentlyThrownOvers, maxOversCanBeThrown, wicketsTaken);
        }
        return objectAsString;
    }
}
