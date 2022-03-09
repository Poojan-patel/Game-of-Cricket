package com.tekion.cricket.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BallLogs {
    @JsonView
    private List<String> logs;
    private Map<Integer, String> playerNamesFromIds;

    public BallLogs(Map<Integer, String> playerNamesFromIds){
        logs = new ArrayList<>();
        this.playerNamesFromIds = playerNamesFromIds;
    }

    public void appendLog(String ballEvent){
        logs.add(ballEvent);
    }

    public void appendLog(String ballEvent, int playerId){
        logs.add(String.format(ballEvent, playerNamesFromIds.get(playerId)));
    }
}
