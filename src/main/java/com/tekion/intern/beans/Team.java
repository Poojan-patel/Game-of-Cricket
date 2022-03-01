package com.tekion.intern.beans;

import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.repository.PlayerRepository;

import java.sql.SQLException;
import java.util.*;


public class Team {
    private String teamName;
    private int teamScore;
    private int totalPlayedBalls;
    private List<Player> players;
    private int totalWicketsFallen;
    private final int NUM_OF_PLAYERS;
    private int extras;
    private int totalBowlers;
    private int[] scoreDistribution;
    private Set<Integer> availableBowlers;
    private int teamId;

    public Team(String teamName, List<String> playerNames, List<String> playerTypes) {
        this.teamName = teamName;
        teamScore = 0;
        totalPlayedBalls = 0;
        totalWicketsFallen = 0;
        players = new ArrayList<>();
        this.NUM_OF_PLAYERS = playerNames.size();
        scoreDistribution = new int[7];
        availableBowlers = new TreeSet<>();
        totalBowlers = 0;
        setPlayers(playerNames, playerTypes);
        this.teamId = 0;
    }

    public Team(String teamName, int teamId) {
        this.teamName = teamName;
        teamScore = 0;
        totalPlayedBalls = 0;
        totalWicketsFallen = 0;
        players = new ArrayList<>();
        this.NUM_OF_PLAYERS = 11;
        scoreDistribution = new int[7];
        availableBowlers = new TreeSet<>();
        totalBowlers = 0;
        setPlayers();
        this.teamId = teamId;
    }

    public int getTeamId(){ return teamId; }

    public void setTeamId(int teamId){
        this.teamId = teamId;
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

    public Set<Integer> getAvailableBowlers(int previousBowler, int secondLastBowler, int totalOvers, int remainingOvers){
        int maxOversCanBeThrown = (int)Math.ceil(totalOvers*1.0 / totalBowlers);
        if(secondLastBowler != -1 && players.get(secondLastBowler).remainingOvers(maxOversCanBeThrown) > 0){
            availableBowlers.add(secondLastBowler);
        }
        availableBowlers.remove(previousBowler);
        if((totalBowlers == 5) && (totalOvers % 5 == 0)) {
            int bowlerWithMaxRemainingOvers = Collections.min(availableBowlers);
            int totalRemainingOversOfAllBowlers = 0;
            for (int i : availableBowlers) {
                if (players.get(i).remainingOvers(maxOversCanBeThrown) > players.get(bowlerWithMaxRemainingOvers).remainingOvers(maxOversCanBeThrown))
                    bowlerWithMaxRemainingOvers = i;
            }
            if (players.get(bowlerWithMaxRemainingOvers).remainingOvers(maxOversCanBeThrown) > (remainingOvers / 2))
                return Collections.singleton(bowlerWithMaxRemainingOvers);
        }
        return availableBowlers;
    }

    public void incrementBowlersNumberOfBalls(int bowlerIndex){
        players.get(bowlerIndex).incrementNumberOfBallsThrown();
    }

    public String getNameOfPlayer(int currentPlayer) {
        return players.get(currentPlayer).getName();
    }

    public PlayerType getPlayerType(int currentPlayer) {
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

    public void setPlayers() {
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            players.add(null);
        }
    }

    public void setPlayers(List<Player> bowlers) {
        this.players = bowlers;
        int cnt = -1;
        for(Player p:bowlers){
            cnt++;
            if(p != null) {
                availableBowlers.add(cnt);
                totalBowlers++;
            }
        }
        System.gc();
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

    public void incrementTeamScoreForUnfair() {
        scoreDistribution[1]++;
        extras++;
        teamScore++;
    }

    public int getPlayerId(int playerOffset) {
        return players.get(playerOffset).getPlayerId();
    }

    public void removePlayer(int outPlayer) {
        players.set(outPlayer, null);
        System.gc();
    }

    public int insertPlayer(int playerOffset) {
        try {
            Player newPlayer = PlayerRepository.getPlayerFromOffsetByTeamId(teamId, playerOffset);
            players.set(playerOffset, newPlayer);
            return newPlayer.getPlayerId();
        } catch (SQLException sqle) {
            System.out.println(sqle);
        } catch (Exception e) {

        }
        return 0;
    }

    private void updatePlayerScore(int score, int playerNumber){
        Player player = players.get(playerNumber);
        player.incrementScore(score);
    }

    private void setPlayers(List<String> playerNames, List<String> playerTypes){
        for(int i = 0; i < NUM_OF_PLAYERS; i++){
            if(!playerTypes.get(i).equals("BATSMAN")) {
                availableBowlers.add(i);
                totalBowlers++;
            }
            players.add(new Player(playerNames.get(i), playerTypes.get(i)));
        }
    }

    private void updatePlayerBalls(int playerNumber) {
        Player player = players.get(playerNumber);
        player.incrementBallsPlayed();
    }

    public void clearAllPlayers() {
        for(int i = 0; i < players.size(); i++){
            players.set(i,null);
        }
    }

    public void fetchBowlersFromDB() throws SQLException, Exception{
        PlayerRepository.fetchBowlersForBowlingTeamByTeamId(this);
    }
}