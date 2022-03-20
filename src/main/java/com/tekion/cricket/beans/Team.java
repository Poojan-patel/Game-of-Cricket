package com.tekion.cricket.beans;

import java.util.UUID;

public class Team {

    private String name;
    private String teamId;

    /*
    Constructor for persisting data in database
     */
    public Team(String name) {
        this.name = name;
        this.teamId = UUID.randomUUID().toString();
    }

    public Team(String name, String teamId) {
        this.name = name;
        this.teamId = teamId;
    }

    public Team() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamId(){ return teamId; }

    public void setTeamId(String teamId){
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }
}