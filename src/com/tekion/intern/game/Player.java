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

    public Player(String name, String type, int playerOrder){
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

    @Override
    public String toString() {
        String scoreDistributionToString = "[";
        for(int i = 0; i < 6; i++)
            scoreDistributionToString += (i + ":" + scoreDistribution[i] + ", ");
        scoreDistributionToString += ("6:" + scoreDistribution[6] + "]");
        return String.format("%s: %d runs in %d balls, scorewise:%s", name, score, ballsPlayed, scoreDistributionToString);
    }
}
