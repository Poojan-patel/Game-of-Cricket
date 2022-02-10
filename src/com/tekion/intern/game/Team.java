package com.tekion.intern.game;

import java.util.ArrayList;
import java.util.List;


class Team {
    private String teamName;
    private int teamScore;
    private int totalPlayedBalls;
    private List<Player> players;
    private int currentWickets;
    private int totalAvailableBalls;
    private final int NUM_OF_PLAYERS;

    public Team(String teamName, List<String> playerNames, List<String> playerTypes, int balls) {
        this.teamName = teamName;
        teamScore = 0;
        totalPlayedBalls = 0;
        currentWickets = 0;
        totalAvailableBalls = balls;
        players = new ArrayList<>();
        this.NUM_OF_PLAYERS = playerNames.size();
        setPlayers(playerNames, playerTypes);
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTeamScore() {
        return teamScore;
    }

    public void incrementTeamScore(int score, int playerNumber) {
        this.teamScore += score;
        updatePlayerScore(score, playerNumber);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentWickets() {
        return currentWickets;
    }

    public void updateWickets() {
        this.currentWickets++;
    }

    public int getNumberOfPlayers(){
        return NUM_OF_PLAYERS;
    }

    public int getTotalPlayedBalls() {
        return totalPlayedBalls;
    }

    public void incrementTotalBalls(int playerNumber) {
        this.totalPlayedBalls++;
        updatePlayerBalls(playerNumber);
    }

    public void getPlayerwiseScore(){
        Player currentPlayer;
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            currentPlayer = players.get(i);
            System.out.println(String.format("%s: %d runs in %d balls", currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getBallsPlayed()));
        }
    }

    private void setPlayers(List<String> playerNames, List<String> playerTypes){
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            players.add(new Player(playerNames.get(i),playerTypes.get(i),i));
        }
    }

    private void updatePlayerScore(int score, int playerNumber){
        Player player = players.get(playerNumber);
        player.incrementScore(score);
    }


    private void updatePlayerBalls(int playerNumber) {
        Player player = players.get(playerNumber);
        player.incrementBallsPlayed();
    }


}
