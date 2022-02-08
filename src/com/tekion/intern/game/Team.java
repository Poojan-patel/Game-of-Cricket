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

    public Team(String teamName, int balls, int NUM_OF_PLAYERS) {
        this.teamName = teamName;
        teamScore = 0;
        totalPlayedBalls = 0;
        currentWickets = 0;
        totalAvailableBalls = balls;
        players = new ArrayList<>();
        this.NUM_OF_PLAYERS = NUM_OF_PLAYERS;
        setPlayers();
    }

    public String getTeamName() {
        return teamName;
    }

    private void setPlayers(){
        Scanner sc = new Scanner(System.in);
        String name;
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            System.out.print("Player-"+(i+1)+": ");
            name = sc.nextLine();
            players.add(new Player(name,i));
        }
    }

    public void getPlayerwiseScore(){
        //int playersWhoPlayed = Integer.min(currentPlayer+1,NUM_OF_PLAYERS);
        Player p;
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            p = players.get(i);
            System.out.println(String.format("%s: %d runs in %d balls", p.getName(), p.getScore(), p.getBallsPlayed()));
        }
    }


    public int getTeamScore() {
        return teamScore;
    }

    private void updatePlayerScore(int score, int strikeIndex){
        Player player = players.get(strikeIndex);
        player.incrementScore(score);
    }

    public void incrementTeamScore(int score, int strikeIndex) {
        this.teamScore += score;
        updatePlayerScore(score, strikeIndex);
    }

    public int getTotalPlayedBalls() {
        return totalPlayedBalls;
    }

    public void incrementTotalBalls(int strikeIndex) {
        this.totalPlayedBalls++;
        updatePlayerBalls(strikeIndex);
    }

    private void updatePlayerBalls(int strikeIndex) {
        Player player = players.get(strikeIndex);
        player.incrementBallsPlayed();
    }

    public int getCurrentPlayer() {
        return currentWickets;
    }

    public void wicketFallen() {
        this.currentWickets++;
    }
}
