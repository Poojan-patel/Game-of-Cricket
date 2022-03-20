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
    private String bowlingPace;
    private String teamId;

    /*
    Constructor for persisting data in database
     */
    public Player(PlayerDTO p){
        name = p.getName();
        playerType = p.getPlayerType().toString();
        bowlingPace = p.getTypeOfBowler().toString();
    }

    /*
    Constructor for persisting data in database and creation of POJO from db
     */
    public Player(String name, String playerType, String bowlingPace, int playerOrder){
        this.name = name;
        this.playerType = playerType;
        this.bowlingPace = bowlingPace;
        this.playerOrder = playerOrder;
    }

    public Player() {
    }

    public void setPlayerOrder(int playerOrder) {
        this.playerOrder = playerOrder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public void setBowlingPace(String bowlingPace) {
        this.bowlingPace = bowlingPace;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public String getPlayerType() {
        return playerType;
    }

    public String getBowlingPace() {
        return bowlingPace;
    }

    public int getPlayerOrder() {
        return playerOrder;
    }

    @Override
    public String toString() {
        return
                playerOrder +
                ". " + name +
                " - " + playerType + ((Common.BATSMAN.equals(playerType)) ? "" : "," + bowlingPace);
    }
}
