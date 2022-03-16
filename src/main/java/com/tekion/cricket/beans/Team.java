package com.tekion.cricket.beans;

public class Team {

    private String teamName;
    private Integer teamId;

    /*
    Constructor for persisting data in database
     */
    public Team(String teamName) {
        this.teamName = teamName;
    }

    public Integer getTeamId(){ return teamId; }

    public void setTeamId(Integer teamId){
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }
}