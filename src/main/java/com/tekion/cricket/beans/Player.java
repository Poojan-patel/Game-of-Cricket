package com.tekion.cricket.beans;

import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.enums.TypeOfBowler;
import com.tekion.cricket.models.PlayerDTO;

public class Player {
    private int playerId;
    private String name;
    private PlayerType playerType;
    private TypeOfBowler typeOfBowler;

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

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public String toString() {
        return
                playerId +
                ". " + name +
                " - " + playerType + ((playerType == PlayerType.BATSMAN) ? "" : "," + typeOfBowler);
    }
}
