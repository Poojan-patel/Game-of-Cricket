package com.tekion.intern.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.enums.TypeOfBowler;
import com.tekion.intern.models.PlayerDTO;

public class Player {
    private int playerId;
    private String name;
    private PlayerType playerType;
    private TypeOfBowler typeOfBowler;
    private int currentRuns;
    private int currentBalls;

    public Player(PlayerDTO p){
        name = p.getName();
        playerType = p.getPlayerType();
        typeOfBowler = p.getTypeOfBowler();
    }

    public Player() {

    }

    public Player(String name, String playerType, String typeOfBowler, int playerId){
        this.name = name;
        this.playerType = PlayerType.fromStringToEnum(playerType);
        this.typeOfBowler = TypeOfBowler.fromStringToEnum(typeOfBowler);
        this.playerId = playerId;
    }

    public Player(String name, String playerType, String typeOfBowler, int playerId, int currentBalls, int currentRuns){
        this(name, playerType, typeOfBowler, playerId);
        this.currentBalls = currentBalls;
        this.currentRuns = currentRuns;
    }

    public String getName() {
        return name;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getTypeOfBowler() {
        return typeOfBowler.toString();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void incrementTotalBalls() {
        currentBalls++;
    }

    public void incrementScore(int outcomeOfBallBowled) {
        currentRuns += outcomeOfBallBowled;
    }
}
