package com.tekion.intern.game;

class Player {
    enum PlayerType{
        BOWLER,
        BATSMAN,
        ALLROUNDER
    }
    private String name;
    private int score;
    private int ballsPlayed;
    private int playerOrder;
    private PlayerType playerType;
    private int[] scoreDistribution;
    private int currentlyThrownBalls;
    private int wicketsTaken;

    public Player(String name, String type, int playerOrder){
        this.name = name;
        this.score = 0;
        this.ballsPlayed = 0;
        this.playerOrder = playerOrder;
        if(type.equals("BOWLER")){
            playerType = PlayerType.BOWLER;
        }
        else if(type.equals("BATSMAN")){
            playerType = PlayerType.BATSMAN;
        }
        else{
            playerType = PlayerType.ALLROUNDER;
        }
        scoreDistribution = new int[7];
        currentlyThrownBalls = 0;
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

    public boolean hasExaustedOvers(int maxOversCanBeThrown) {
        return (Math.ceil(currentlyThrownBalls/6.0) == maxOversCanBeThrown);
    }

    public void incrementNumberOfBallsThrown(){
        currentlyThrownBalls++;
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
        if(this.playerType != PlayerType.BATSMAN){
            objectAsString += String.format(" Overs taken:%d.%d, Wickets Taken:%d", currentlyThrownBalls/6, currentlyThrownBalls%6, wicketsTaken);
        }
        return objectAsString;
    }
}
