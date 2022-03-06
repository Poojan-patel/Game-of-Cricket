package com.tekion.intern.beans;

import com.tekion.intern.beans.Team;
import com.tekion.intern.repository.TeamInPlayRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Strike {
    //private List<Player> strikeHolders;
    private int currentStrike;
    private Player currentBowler;
    private int matchId;
    private Team team;

    public Strike(int matchId, Player bowler, Team team){
        this.currentStrike = 0;
        this.currentBowler = bowler;
        this.team = team;
        this.matchId = matchId;
    }

    public int getCurrentOver(){
        return team.getPlayedBalls()/6;
    }

    public int getCurrentBowlerPlayerId(){
        return currentBowler.getPlayerId();
    }

//    public void overChanged(){
//        currentStrike = (currentStrike+1)%2;
//    }

    public void changeStrike(){
        currentStrike = (currentStrike+1)%2;
    }

    public int getCurrentStrike() {
        return currentStrike;
    }

//    public int getCurrentNonStrike(){
//        return 1-currentStrike;
//    }


//    public void removeOutPlayer(int outPlayer){
//        team.removePlayer(outPlayer);
//    }

//    public int getCurrentBowler() {
//        return currentBowler;
//    }
//
//    public int getPreviousBowler() {
//        return previousBowler;
//    }

    public boolean isAllOut(){
        return team.isAllOut();
    }

    public Player getCurrentStrikePlayer() {
        return team.getPlayers().get(currentStrike);
    }

    public Team getBattingTeam() {
        return team;
    }

    public void incrementTotalBalls() {
        team.incrementTotalBalls(currentStrike);
    }

    public void updateWickets() {
        team.incrementWickets();
    }

    public int getMatchId() {
        return matchId;
    }

    public int getTeamId() {
        return team.getTeamId();
    }

    public void setNewBatsman(Player newBatter) {
        team.setNewPlayerAtStrike(currentStrike, newBatter);
    }
}
