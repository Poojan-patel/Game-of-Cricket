package com.tekion.cricket.beans;

import com.tekion.cricket.constants.Common;
import com.tekion.cricket.models.PlayerDTO;

public class Player {
    private int playerOrder;
    private String name;

    /** {@link com.tekion.cricket.enums.PlayerType}
     */
    private String playerType;

    /** {@link com.tekion.cricket.enums.TypeOfBowler}
     */
    private String typeOfBowler;
    private String teamId;

    /*
    Constructor for persisting data in database
     */
    public Player(PlayerDTO p){
        name = p.getName();
        playerType = p.getPlayerType().toString();
        typeOfBowler = p.getTypeOfBowler().toString();
    }

    /*
    Constructor for persisting data in database and creation of POJO from db
     */
    public Player(String name, String playerType, String typeOfBowler, int playerOrder){
        this.name = name;
        this.playerType = playerType;
        this.typeOfBowler = typeOfBowler;
        this.playerOrder = playerOrder;
    }

    public String getName() {
        return name;
    }

    public String getPlayerType() {
        return playerType;
    }

    public String getTypeOfBowler() {
        return typeOfBowler;
    }

    public int getPlayerOrder() {
        return playerOrder;
    }

    @Override
    public String toString() {
        return
                playerOrder +
                ". " + name +
                " - " + playerType + ((Common.BATSMAN.equals(playerType)) ? "" : "," + typeOfBowler);
    }
}
