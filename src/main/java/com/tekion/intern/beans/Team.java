package com.tekion.intern.beans;

import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TeamDTO;

import java.util.*;

public class Team {

    private String teamName;
    private Integer teamId;
    private List<Player> playerList;

    public Team(TeamDTO t) {
        teamName = t.getTeamName();
        playerList = new ArrayList<>();
        for(PlayerDTO p:t.getPlayers()){
            playerList.add(new Player(p));
        }
    }

    public Team() {

    }

    public Integer getTeamId(){ return teamId; }

    public void setTeamId(Integer teamId){
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamName='" + teamName + '\'' +
                ", teamId=" + teamId +
                ", playerList=" + playerList +
                '}';
    }

    public List<Player> getPlayers() {
        return playerList;
    }
}