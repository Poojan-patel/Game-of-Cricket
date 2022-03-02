package com.tekion.intern.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

public class MatchCreationRequest {
    private int team1Id;
    private int team2Id;
    private int overs;

    public int getTeam1Id() {
        return team1Id;
    }

    public int getTeam2Id() {
        return team2Id;
    }

    public int getOvers() {
        return overs;
    }
}
