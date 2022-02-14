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
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int score) {
        this.score += score;
    }

    public int getBallsPlayed() {
        return ballsPlayed;
    }

    public void incrementBallsPlayed() {
        this.ballsPlayed++;
    }

    @Override
    public String toString() {
        return String.format("%s: %d runs in %d balls", name, score, ballsPlayed);
    }
}
