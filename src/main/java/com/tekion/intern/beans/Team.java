package com.tekion.intern.beans;

import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TeamDTO;

import java.util.*;

public class Team {

    private String teamName;
    private Integer teamId;
    private int currentScore;
    private int playedBalls;
    private List<Player> playerList;
    private int currentWickets;
    private int scoreToChase;

    public Team(TeamDTO t) {
        teamName = t.getTeamName();
        playerList = new ArrayList<>();
        for(PlayerDTO p:t.getPlayers()){
            playerList.add(new Player(p));
        }
    }

    public int getScoreToChase() {
        return scoreToChase;
    }

    public void setScoreToChase(int scoreToChase) {
        this.scoreToChase = scoreToChase;
    }

    public Team() {

    }

    public Team(String teamName, int currentScore, int playedBalls, int teamId){
        this.teamName = teamName;
        this.currentScore = currentScore;
        this.playedBalls = playedBalls;
        this.teamId = teamId;
    }

    public int getPlayedBalls(){
        return playedBalls;
    }


    public void setCurrentWickets(int currentWickets) {
        this.currentWickets = currentWickets;
    }

    public Integer getTeamId(){ return teamId; }

    public void setTeamId(Integer teamId){
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<Player> getPlayers() {
        return playerList;
    }

    public void fetchNewPlayer(int currentStrike) {
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public void setPlayedBalls(int playedBalls) {
        this.playedBalls = playedBalls;
    }

    public void incrementTotalBalls(int currentStrike) {
        playedBalls++;
        playerList.get(currentStrike).incrementTotalBalls();
    }

    public void incrementWickets() {
        currentWickets++;
    }

    public boolean isAllOut(){
        return (currentWickets == 10);
    }

    public String getNameOfPlayer(int currentStrike) {
        return playerList.get(currentStrike).getName();
    }

    public int getMaxOrderedPlayer(){
        return Integer.max(playerList.get(0).getPlayerId(), playerList.get(1).getPlayerId());
    }

    public void setNewPlayerAtStrike(int currentStrike, Player newBatter) {
        playerList.set(currentStrike, newBatter);
    }

    public int getCurrentWickets() {
        return currentWickets;
    }

    public int getPlayerIdByIndex(int index){
        if(playerList.get(index) == null)
            return -1;
        return playerList.get(index).getPlayerId();
    }

    public int getTeamScore() {
        return currentScore;
    }

    public void incrementTeamScoreForUnfair(int outcomeOfBallBowled) {
        currentScore += outcomeOfBallBowled;
    }

    public void incrementTeamScore(int outcomeOfBallBowled, int currentPlayer) {
        currentScore += outcomeOfBallBowled;
        playerList.get(currentPlayer).incrementScore(outcomeOfBallBowled);
    }

    public int getNumberOfPlayers() {
        return 10;
    }

    @Override
    public String toString(){
        return teamName + ": " + currentScore + "/" + currentWickets + String.format(" in %d.%d overs", playedBalls/6, playedBalls%6);
    }
}