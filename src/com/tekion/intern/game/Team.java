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
    private int numOfBatsman;
    private int totalWicketsFallen;
    private final int NUM_OF_PLAYERS;
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

    private void setPlayers(List<String> playerNames, List<String> playerTypes){
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            if(playerTypes.get(i).equals("BATSMAN"))
                numOfBatsman++;
            else
                availableBowlers.add(i);
            players.add(new Player(playerNames.get(i), playerTypes.get(i), i));
        }
    }

    public Set<Integer> getAvailableBowlers(int previousBowler, int secondLastBowler, int maxOversCanBeThrown){
        //List<Integer> availableBowlers = new ArrayList<>();
        if(secondLastBowler != -1 && !players.get(secondLastBowler).hasExaustedOvers(maxOversCanBeThrown)){
            availableBowlers.add(secondLastBowler);
        }
        availableBowlers.remove(previousBowler);
//        for(int i = 0; i < NUM_OF_PLAYERS; i++){
//            if(!(players.get(i).getPlayerType() == Player.PlayerType.BATSMAN) && !players.get(i).hasExaustedOvers(maxOversCanBeThrown) && (i != previousBowler)){
//                availableBowlers.add(i);
//            }
//        }
        return availableBowlers;
    }

    public void incrementBowlersNumberOfBalls(int bowlerIndex){
        players.get(bowlerIndex).incrementNumberOfBallsThrown();
    }

    private void updatePlayerScore(int score, int playerNumber){
        Player player = players.get(playerNumber);
        player.incrementScore(score);
    }


    private void updatePlayerBalls(int playerNumber) {
        Player player = players.get(playerNumber);
        player.incrementBallsPlayed();
    }

    public String getNameOfPlayer(int currentPlayer) {
        return players.get(currentPlayer).getName();
    }

    @Override
    public String toString() {
        String scoreDistributionToString = "[";
        for(int i = 0; i < 6; i++)
            scoreDistributionToString += (i + ":" + scoreDistribution[i] + ", ");
        scoreDistributionToString += ("6:" + scoreDistribution[6] + "]");
        return String.format("%s: %d/%d (%d.%d Overs)\nDistribution: %s",
                teamName,
                teamScore,
                totalWicketsFallen,
                totalPlayedBalls/6,
                totalPlayedBalls%6,
                scoreDistributionToString
        );
    }

    public Player.PlayerType getPlayerType(int currentPlayer) {
        return players.get(currentPlayer).getPlayerType();
    }

    public String getPlayerIndividualScore(int currentStrike) {
        return players.get(currentStrike).toString();
    }
    /*
        Because wicket can only be taken after ball is thrown, in normal cases
        So balls can be incremented from here only
     */
    public void incrementWicketsTakenByBowler(int currentBowler) {
        players.get(currentBowler).incrementWicketsTaken();
        players.get(currentBowler).incrementNumberOfBallsThrown();
    }
}