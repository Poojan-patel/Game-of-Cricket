package com.tekion.intern.game;

import com.tekion.intern.repository.TeamInPlayRepository;

import java.sql.SQLException;

class Strike{
    private int[] strikeHolders;
    private int currentStrike;
    private int currentBowler;
    private int previousBowler;
    private int matchId;
    private Team team;

    public Strike(int matchId, Team team){
        this.currentStrike = 0;
        this.strikeHolders = new int[]{0, 1};
        currentBowler = -1;
        previousBowler = -1;
        this.team = team;
        this.matchId = matchId;
        int strike = team.insertPlayer(0);
        int nonstrike = team.insertPlayer(1);
        try {
            TeamInPlayRepository.insertStrikeData(strike, nonstrike, matchId, team.getTeamId());
        } catch (SQLException sqle){
            System.out.println(sqle);
        } catch (Exception e){

        }
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
        team.insertPlayer(maxOrder+1);
        return outPlayer;
    }

    public void removeOutPlayer(Team team, int outPlayer){
        team.removePlayer(outPlayer);
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

    public void updateStrikeInDB() {
        int teamId = team.getTeamId();
        int onStrike, offStrike, curWickets;
        onStrike = (getCurrentStrike() >= team.getNumberOfPlayers())? -1: team.getPlayerId(getCurrentStrike());
        offStrike = (getCurrentNonStrike() >= team.getNumberOfPlayers())? -1: team.getPlayerId(getCurrentNonStrike());
        if(onStrike == -1 || offStrike == -1)
            curWickets = 10;
        else
            curWickets = Integer.max(strikeHolders[0], strikeHolders[1])-1;
        try {
            TeamInPlayRepository.updateStrikes(onStrike, offStrike, matchId, teamId, curWickets);
        } catch (SQLException sqle){
            System.out.println(sqle);
        } catch (Exception e){

        }
    }

    public void updateBowler(int bowlerId) {
        try {
            TeamInPlayRepository.updateBowler(bowlerId, matchId, team.getTeamId());
        } catch(SQLException sqle){
            System.out.println(sqle);
        } catch(Exception e){

        }
    }
}
