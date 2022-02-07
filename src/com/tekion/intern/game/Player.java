package com.tekion.intern.game;

class Player {
    enum State{
        OUT,
        NOT_OUT,
        YET_TO_PLAY
    }
    private String name;
    private int score;
    private int ballsPlayed;


    public Player(String name){
        this.name = name;
        this.score = 0;
        this.ballsPlayed = 0;
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
}
