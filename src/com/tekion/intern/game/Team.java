package com.tekion.intern.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


class Team {
    private String teamName;
    private int teamScore;
    private int totalPlayedBalls;
    private List<Player> players;
    private int totalWicketsFallen;
    private final int NUM_OF_PLAYERS;
    private int extras;
    private int[] scoreDistribution;
    private Set<Integer> availableBowlers;

    public Team(String teamName, List<String> playerNames, List<String> playerTypes, int balls) {
        this.teamName = teamName;
        teamScore = 0;
        totalPlayedBalls = 0;
        totalWicketsFallen = 0;
        players = new ArrayList<>();
        this.NUM_OF_PLAYERS = playerNames.size();
        scoreDistribution = new int[7];
        availableBowlers = new TreeSet<>();
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
        scoreDistribution[score]++;
        updatePlayerScore(score, playerNumber);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getTotalWicketsFallen() {
        return totalWicketsFallen;
    }

    public void updateWickets() {
        this.totalWicketsFallen++;
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

    public void showPlayerwiseScore(){
        Player currentPlayer;
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            currentPlayer = players.get(i);
            System.out.println(currentPlayer);
        }
    }

    public Set<Integer> getAvailableBowlers(int previousBowler, int secondLastBowler, int maxOversCanBeThrown){
        if(secondLastBowler != -1 && !players.get(secondLastBowler).hasExaustedOvers(maxOversCanBeThrown)){
            availableBowlers.add(secondLastBowler);
        }
        availableBowlers.remove(previousBowler);
        return availableBowlers;
    }

    public void incrementBowlersNumberOfBalls(int bowlerIndex){
        players.get(bowlerIndex).incrementNumberOfBallsThrown();
    }

    public String getNameOfPlayer(int currentPlayer) {
        return players.get(currentPlayer).getName();
    }

    public Player.PlayerType getPlayerType(int currentPlayer) {
        return players.get(currentPlayer).getPlayerType();
    }

    public String getPlayerIndividualScore(int currentStrike) {
        return players.get(currentStrike).toString();
    }

    public void incrementWicketsTakenByBowler(int currentBowler) {
        players.get(currentBowler).incrementWicketsTaken();
    }

    public String getTypeOfBowler(int currentBowler) {
        return players.get(currentBowler).getTypeOfBowler();
    }

    private void setPlayers(List<String> playerNames, List<String> playerTypes){
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            if(!playerTypes.get(i).equals("BATSMAN"))
                availableBowlers.add(i);
            players.add(new Player(playerNames.get(i), playerTypes.get(i)));
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

    @Override
    public String toString() {
        String scoreDistributionToString = "[";
        for(int i = 0; i < 6; i++)
            scoreDistributionToString += (i + ":" + scoreDistribution[i] + ", ");
        scoreDistributionToString += ("6:" + scoreDistribution[6] + "]");
        return String.format("%s: %d/%d (%d.%d Overs)\nDistribution: %s\n Extras: %d",
                teamName,
                teamScore,
                totalWicketsFallen,
                totalPlayedBalls/6,
                totalPlayedBalls%6,
                scoreDistributionToString,
                extras
        );
    }

    public void incrementTeamScoreForUnfair(int score) {
        scoreDistribution[score]++;
        extras++;
    }
}