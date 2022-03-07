package com.tekion.intern.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

public class BallLogs {
    @JsonView
    private List<String> logs;
    public BallLogs(){
        logs = new ArrayList<>();
    }
    public void appendLog(String ballEvent){
        logs.add(ballEvent);
    }
}
