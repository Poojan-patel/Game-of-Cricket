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
    private int remainingOvers;

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

    public String getName() {
        return name;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getTypeOfBowler() {
        return typeOfBowler.toString();
    }

    public void setRemainingOvers(int remainingOvers) {
        this.remainingOvers = remainingOvers;
    }

    public int getPlayerId() {
        return playerId;
    }
}
