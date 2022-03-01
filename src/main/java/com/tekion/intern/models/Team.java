package com.tekion.intern.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Team {
    private String teamName;
    private List<Player> players;

//    public Team(){
//
//    }
//
//    public Team(String teamName, List<Player> players){
//        this.teamName = teamName;
//        this.players = players;
//    }

    public String getTeamName() {
        return teamName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        String ts = "Team{" +
                "teamName='" + teamName + '\n';
        for(Player i:players)
            ts += i + "\n";
        return ts;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("players")
    private void unpackPlayers(List<Map<String,Object>> playerList){
        players = new ArrayList<>();
        for(Map<String, Object> singlePlayer: playerList){
            players.add(new Player((String)singlePlayer.get("name"), (String)singlePlayer.get("playerType"), (String)singlePlayer.get("bowlingType")));
        }
    }
}
