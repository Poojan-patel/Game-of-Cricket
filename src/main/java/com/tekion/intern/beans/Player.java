package com.tekion.intern.beans;

import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.enums.TypeOfBowler;
import com.tekion.intern.models.PlayerDTO;

public class Player {

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

    public String getName() {
        return name;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getTypeOfBowler() {
        return typeOfBowler.toString();
    }
}
