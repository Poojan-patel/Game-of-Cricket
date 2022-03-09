package com.tekion.intern.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.enums.TypeOfBowler;

public class BatsmanStats {
    @JsonView
    private int playerId;
    @JsonView
    private String name;
    @JsonView
    private int currentScore;
    @JsonView
    private int playedBalls;

    public BatsmanStats(){
    }

    public BatsmanStats(String name, int playerId, int currentScore, int playedBalls) {
        this.name = name;
        this.currentScore = currentScore;
        this.playerId = playerId;
        this.playedBalls = playedBalls;
    }


    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%d. %s: %d runs in %d balls", playerId, name, currentScore, playedBalls);
    }

    public int getPlayerId() {
        return playerId;
    }
}
