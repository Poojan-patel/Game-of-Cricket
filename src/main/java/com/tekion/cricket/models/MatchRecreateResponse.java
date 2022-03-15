package com.tekion.cricket.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;
import java.util.Map;

public class MatchRecreateResponse {
    @JsonView
    private MatchResult matchResult;
    @JsonView
    private BallLogs ballLogs;

    public MatchRecreateResponse(Map<Integer, String> playerNamesFromIds){
        ballLogs = new BallLogs(playerNamesFromIds);
    }

    public void appendLog(String ballEvent){
        ballLogs.appendLog(ballEvent);
    }

    public void appendLog(String ballEvent, Integer playerId){
        ballLogs.appendLog(ballEvent, playerId);
    }

    public void setMatchResult(MatchResult matchResult){
        this.matchResult = matchResult;
    }
}
