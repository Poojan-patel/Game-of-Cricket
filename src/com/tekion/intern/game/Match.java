package com.tekion.intern.game;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Match {
    enum Winner{
        TEAM1,
        TEAM2,
        TIE,
        STARTED
    }
    private final Team team1;
    private final Team team2;
    private Winner winner;
    private int totalAvailableBalls;
    private Strike strike;

    public Match(int numOfOvers,
                 String team1Name,
                 List<String> team1PlayerNames,
                 List<String> team1PlayerTypes,
                 String team2Name,
                 List<String> team2PlayerNames,
                 List<String> team2PlayerTypes
    ){
        totalAvailableBalls = numOfOvers*6;
        team1 = new Team(team1Name, team1PlayerNames, team1PlayerTypes, totalAvailableBalls);
        team2 = new Team(team2Name, team2PlayerNames, team2PlayerTypes, totalAvailableBalls);
        winner = Winner.STARTED;
    }

    public void stimulateGame(int headOrTails, int choiceOfInning){
        int tossOutcome = choiceOfTossWinner(headOrTails, choiceOfInning);
        //if((tossWinner == 0 && choiceOfInning == 1) || (tossWinner == 1 && choiceOfInning == 0)){
        if(tossOutcome == 1){
            stimulateInnings(team1, team2);
        } else{
            stimulateInnings(team2, team1);
        }
        declareTheWinner();
    }

    public void showFinalScoreBoard(){
        System.out.println(team1);
        System.out.println(team2);
        System.out.println("Team: " + team1.getTeamName());
        team1.showPlayerwiseScore();
        System.out.println("Team: " + team2.getTeamName());
        team2.showPlayerwiseScore();
    }

    /*
       if tossWinner is 1 and choiceOfInning is 0 or tossWinner is 0 and choice is 1, in both the case team1 will bat
       if summation is other than 1, team2 will bat
     */
    private int choiceOfTossWinner(int tossWinner, int choiceOfInning){
        System.out.println(((tossWinner == 0) ? team1.getTeamName() : team2.getTeamName()) + " has won the toss and opted for " +
                ((choiceOfInning == 0) ? "Fielding" : "Batting"));

        return (tossWinner + choiceOfInning);
    }

    private void stimulateInnings(Team first, Team second){
        System.out.println(first.getTeamName() + " Will Start Batting");
        startInning(first, false, 0);

        int scoreToChase = first.getTeamScore();

        System.out.println(second.getTeamName() + " Will Start Batting");
        startInning(second, true, scoreToChase);
    }

    private void startInning(Team team, boolean isChasser, int scoreToChase) {
        strike = new Strike();
        int overs = totalAvailableBalls/6;
        boolean allOut = false;
        for(int i = 0; i < overs; i++) {
            System.out.println("Over: " + (i + 1));

            for (int j = 0; j < 6; j++) {
                allOut = playTheBall(team, j + 1);
                if (allOut || (isChasser && (scoreToChase < team.getTeamScore()))) {
                    break;
                }
            }

            if (allOut || (isChasser && (scoreToChase < team.getTeamScore())))
                break;
            strike.overChanged();
        }
    }

    private boolean playTheBall(Team team, int ball){
        int currentPlayer = strike.getCurrentStrike();
        int outcomeOfBallBowled = MatchUtil.generateRandomScore(team.getPlayerType(currentPlayer));
        team.incrementTotalBalls(currentPlayer);

        if(outcomeOfBallBowled != -1){
            System.out.println(ball + ": " + outcomeOfBallBowled + " run || Player: " + team.getNameOfPlayer(currentPlayer));
            team.incrementTeamScore(outcomeOfBallBowled, currentPlayer);
            strike.changeStrike(outcomeOfBallBowled);
            return false;
        }

        int outPlayer = strike.updateStrikeOnWicket();
        team.updateWickets();
        System.out.println(ball + ": Wicket-" + team.getCurrentWickets() + " || Player: " + team.getNameOfPlayer(outPlayer));
        return (team.getCurrentWickets() == team.getNumberOfPlayers()-1);
    }

    private void declareTheWinner(){
        winner = MatchUtil.decideWinner(team1.getTeamScore(), team2.getTeamScore());
        System.out.println("Game Ended");
        if(winner == Winner.TEAM1)
            System.out.println(team1.getTeamName() + " Won the Game");
        else if(winner == Winner.TEAM2)
            System.out.println(team2.getTeamName() + " Won the Game");
        else
            System.out.println("Game Tied");
    }

}
