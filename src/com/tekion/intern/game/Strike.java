package com.tekion.intern.game;

import com.tekion.intern.repository.TeamInPlayRepository;

import java.sql.SQLException;

class Strike{
    private int[] strikeHolders;
    private int currentStrike;
    private int currentBowler;
    private int previousBowler;

    public Strike(){
        this.currentStrike = 0;
        this.strikeHolders = new int[]{0, 1};
        currentBowler = -1;
        previousBowler = -1;
    }

    public void overChanged(){
        currentStrike = (currentStrike+1)%2;
    }

    public void changeStrike(int run){
        if(run%2 == 1)
            currentStrike = (currentStrike+1)%2;
    }

    public int getCurrentStrike() {
        return strikeHolders[currentStrike];
    }

    public int getCurrentNonStrike(){
        return strikeHolders[1-currentStrike];
    }

    /*
        If x is the maximum indexOfPlayer between both players, then
        At any point of time, when wicket falls, The next player who comes on the pitch is x+1
    */
    public int updateStrikeOnWicket(){
        int maxOrder = Integer.max(strikeHolders[0], strikeHolders[1]);
        int outPlayer = strikeHolders[currentStrike];
        strikeHolders[currentStrike] = maxOrder+1;
        return outPlayer;
    }

    public int getCurrentBowler() {
        return currentBowler;
    }

    public int getPreviousBowler() {
        return previousBowler;
    }

    public void setCurrentBowler(int bowlerIndex){
        currentBowler = bowlerIndex;
    }

    public void setPreviousBowler(int bowlerIndex){
        previousBowler = bowlerIndex;
    }

    public void updateStrikeInDB(int matchId, Team team) {
        int teamId = team.getTeamId();
        int onStrike, offStrike;
        //if(team.getTotalWicketsFallen() == team.getNumberOfPlayers()-1) {
            onStrike = (getCurrentStrike() >= team.getNumberOfPlayers())? -1: team.getPlayerId(getCurrentStrike());
            offStrike = (getCurrentNonStrike() >= team.getNumberOfPlayers())? -1: team.getPlayerId(getCurrentNonStrike());
        //}
        try {
            TeamInPlayRepository.updateStrikes(onStrike, offStrike, matchId, teamId);
        } catch (SQLException sqle){
            System.out.println(sqle);
        } catch (Exception e){

        }
    }
}
