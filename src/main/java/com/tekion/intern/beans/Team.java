package com.tekion.intern.beans;

import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TeamDTO;

import javax.persistence.*;
import java.util.*;

@Entity
public class Team {

    private String teamName;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer teamId;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Player> playerList;

    public Team(TeamDTO t) {
        teamName = t.getTeamName();
        playerList = new ArrayList<>();
        Player currentPlayer;
        for(PlayerDTO p:t.getPlayers()){
            currentPlayer = new Player(p);
            currentPlayer.setTeam(this);
            playerList.add(currentPlayer);
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
}