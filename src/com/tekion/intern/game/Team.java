package com.tekion.intern.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class Team {
    private String teamName;
    private int teamScore;
    private int totalPlayedBalls;
    private List<Player> players;
    private int currentWickets;
    private int totalAvailableBalls;
    private final int NUM_OF_PLAYERS;

    public List<Player> getPlayers() {
        return players;
    }

    public int getNumberOfPlayers(){
        return NUM_OF_PLAYERS;
    }

    public Team(String teamName, List<String> playerNames, int balls) {
        this.teamName = teamName;
        teamScore = 0;
        totalPlayedBalls = 0;
        currentWickets = 0;
        totalAvailableBalls = balls;
        players = new ArrayList<>();
        //even indices contains name of the player and odd contains their type (bowler/batsman)
        this.NUM_OF_PLAYERS = playerNames.size()/2;
        setPlayers(playerNames);
    }

    public String getTeamName() {
        return teamName;
    }

    private void setPlayers(List<String> playerNames){
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            players.add(new Player(playerNames.get(2*i),playerNames.get(2*i+1),i));
        }
    }

    public void getPlayerwiseScore(){
        Player currentPlayer;
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            currentPlayer = players.get(i);
            System.out.println(String.format("%s: %d runs in %d balls", currentPlayer.getName(), currentPlayer.getScore(), currentPlayer.getBallsPlayed()));
        }
    }


    public int getTeamScore() {
        return teamScore;
    }

    private void updatePlayerScore(int score, int playerNumber){
        Player player = players.get(playerNumber);
        player.incrementScore(score);
    }

    public void incrementTeamScore(int score, int playerNumber) {
        this.teamScore += score;
        updatePlayerScore(score, playerNumber);
    }

    public int getTotalPlayedBalls() {
        return totalPlayedBalls;
    }

    public void incrementTotalBalls(int playerNumber) {
        this.totalPlayedBalls++;
        updatePlayerBalls(playerNumber);
    }

    private void updatePlayerBalls(int playerNumber) {
        Player player = players.get(playerNumber);
        player.incrementBallsPlayed();
    }

    public int getCurrentPlayer() {
        return currentWickets;
    }

    public void updateWickets() {
        this.currentWickets++;
    }
}
