package com.tekion.intern.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamDTO {
    @JsonView
    private String teamName;
    @JsonIgnore
    private List<PlayerDTO> players;
    @JsonView
    private Integer teamId;

    public String getTeamName() {
        return teamName;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        String ts = "Team{" +
                "teamName='" + teamName + ',' + "teamId=" + teamId + '}';
        return ts;
    }

    public TeamDTO(String teamName, Integer teamId){
        this.teamName = teamName;
        this.teamId = teamId;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("players")
    private void unpackPlayers(List<Map<String,Object>> playerList){
        players = new ArrayList<>();
        for(Map<String, Object> singlePlayer: playerList){
            players.add(new PlayerDTO((String)singlePlayer.get("name"), (String)singlePlayer.get("playerType"), (String)singlePlayer.get("bowlingType")));
        }
    }

    public int getTeamId() {
        return teamId;
    }
}
