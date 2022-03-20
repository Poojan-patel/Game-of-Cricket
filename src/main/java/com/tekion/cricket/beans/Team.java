package com.tekion.cricket.beans;

import java.util.UUID;

public class Team {

    private String teamName;
    private String teamId;

    /*
    Constructor for persisting data in database
     */
    public Team(String teamName) {
        this.teamName = teamName;
        this.teamId = UUID.randomUUID().toString();
    }

    public Team(String teamName, String teamId) {
        this.teamName = teamName;
        this.teamId = teamId;
    }

    public String getTeamId(){ return teamId; }

    public void setTeamId(String teamId){
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }
}