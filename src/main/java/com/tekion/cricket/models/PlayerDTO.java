package com.tekion.cricket.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.enums.TypeOfBowler;

public class PlayerDTO {
    @JsonView
    private int playerId;
    @JsonView
    private String name;
    @JsonView
    private PlayerType playerType;
    @JsonView
    private TypeOfBowler typeOfBowler;
    @JsonView
    private int remainingOvers;

    public PlayerDTO(){
    }

    public PlayerDTO(String name, String playerType, String typeOfBowling) {
        this.name = name;
        this.playerType = PlayerType.fromStringToEnum(playerType);
        this.typeOfBowler = TypeOfBowler.fromStringToEnum(typeOfBowling);
        if(this.playerType != PlayerType.BATSMAN && this.typeOfBowler == TypeOfBowler.NA) {
            throw new IllegalStateException("Non Batsman Player Must have Bowling type Defined");
        }
    }

    public PlayerDTO(String name, String playerType, String typeOfBowling, int playerId, int remainingOvers) {
        this(name, playerType, typeOfBowling);
        this.playerId = playerId;
        this.remainingOvers = remainingOvers;
    }

    public String getName() {
        return name;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public TypeOfBowler getTypeOfBowler() {
        return typeOfBowler;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", playerType=" + playerType +
                ", typeOfBowler=" + typeOfBowler +
                '}';
    }

    public int getPlayerId() {
        return playerId;
    }
}
