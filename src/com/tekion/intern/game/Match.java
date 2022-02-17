package com.tekion.intern.game;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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
    private int maxOversCanBeThrown;
    private Strike strike;
    private static Scanner sc = new Scanner(System.in);

    public Match(int numOfOvers,
                 String team1Name,
                 List<String> team1PlayerNames,
                 List<String> team1PlayerTypes,
                 String team2Name,
                 List<String> team2PlayerNames,
                 List<String> team2PlayerTypes
    ){
        totalAvailableBalls = numOfOvers*6;
        maxOversCanBeThrown = (int)Math.ceil(numOfOvers/5.0);
        team1 = new Team(team1Name, team1PlayerNames, team1PlayerTypes, totalAvailableBalls);
        team2 = new Team(team2Name, team2PlayerNames, team2PlayerTypes, totalAvailableBalls);
        winner = Winner.STARTED;
    }

    public void stimulateGame(int headOrTails, int choiceOfInning) throws IOException, InterruptedException {
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

    private void stimulateInnings(Team first, Team second) throws IOException, InterruptedException {
        System.out.println(first.getTeamName() + " Will Start Batting");
        startInning(first, second, -1);

        int scoreToChase = first.getTeamScore();

        System.out.println(second.getTeamName() + " Will Start Batting");
        startInning(second, first, scoreToChase);
    }

    private void startInning(Team battingTeam, Team bowlingTeam, int scoreToChase) throws IOException, InterruptedException {
        System.out.println("Here are your choices for key pressing before playing the ball:\n");
        System.out.println("1... Current Player Score:");
        System.out.println("2... Current Players Score (Strike and Non-Strike):");
        System.out.println("3... Current Wickets and Remaining Wickets:");
        System.out.println("4... Current Team Scoreboard:");
        System.out.println("5... Opposite Team Scoreboard:");
        System.out.println("6... Match Scoreboard:");
        if(scoreToChase != -1)   System.out.println("7... Runs to Chase for becoming winner:");
        System.out.println("Any to Next Ball:");

        strike = new Strike();
        int overs = totalAvailableBalls/6;
        boolean allOutOrChased = false;
        List<Integer> availableBowlers;
        int selectedBowler;
        for(int i = 0; i < overs; i++) {
            availableBowlers = bowlingTeam.getAvailableBowlers(strike.getCurrentBowler(), maxOversCanBeThrown);
            selectedBowler = MatchUtil.selectBowler(bowlingTeam, availableBowlers);
            //bowlingTeam.incrementBowlersNumberOfBalls(selectedBowler);
            strike.setCurrentBowler(selectedBowler);
            System.out.println("Over: " + i);
            System.out.println("-----------------------------------------------------------------");
            System.out.println("-----------------------------------------------------------------");

            allOutOrChased = playTheOver(battingTeam, bowlingTeam, scoreToChase, i);
            if (allOutOrChased)
                break;

            strike.overChanged();
            MatchUtil.clearConsole();
        }
    }

    private boolean playTheOver(Team battingTeam, Team bowlingTeam, int scoreToChase, int currentOver) {
        boolean allOut;
        for (int j = 0; j < 6; j++) {
            allOut = playTheBall(battingTeam, bowlingTeam, j + 1, currentOver);
            System.out.println("-----------------------------------------------------------------");
            if (allOut || ((scoreToChase != -1) && (scoreToChase < battingTeam.getTeamScore()))) {
                return true;
            }
            userChoiceHandler(battingTeam, bowlingTeam, strike, scoreToChase);
            System.out.println("-----------------------------------------------------------------");
        }
        return false;
    }

    private void userChoiceHandler(Team battingTeam, Team bowlingTeam, Strike strike, int scoreToChase) {
        String choiceBetweenBalls = sc.nextLine();
        switch (choiceBetweenBalls){
            case "1": System.out.println(battingTeam.getPlayerIndividualScore(strike.getCurrentStrike()));
                      break;
            case "2": System.out.println(battingTeam.getPlayerIndividualScore(strike.getCurrentStrike()));
                      System.out.println(battingTeam.getPlayerIndividualScore(strike.getCurrentNonStrike()));
                      break;
            case "3": System.out.println("Current Wickets: " + battingTeam.getTotalWicketsFallen() + " Remaining Wickets:" + (battingTeam.getNumberOfPlayers() - battingTeam.getTotalWicketsFallen()-1));
                      break;
            case "4": System.out.println(battingTeam);
                      break;
            case "5": System.out.println(bowlingTeam);
                      break;
            case "6": System.out.println(battingTeam);
                      System.out.println(bowlingTeam);
                      break;
            case "7": if(scoreToChase != 0)
                        System.out.println("Remaining score to chase:" + (scoreToChase + 1 - battingTeam.getTeamScore()) + " in " + (totalAvailableBalls - battingTeam.getTotalPlayedBalls()) + " balls");
                      break;
            default:
        }
    }

    private boolean playTheBall(Team battingTeam, Team bowlingTeam, int ballNumber, int over){
        if(ballNumber == 6){
            over++;
            ballNumber = 0;
        }
        int currentPlayer = strike.getCurrentStrike();
        int outcomeOfBallBowled = MatchUtil.generateRandomScore(battingTeam.getPlayerType(currentPlayer));
        battingTeam.incrementTotalBalls(currentPlayer);

        if(outcomeOfBallBowled != -1){
            bowlingTeam.incrementBowlersNumberOfBalls(strike.getCurrentBowler());
            System.out.println(over + "." + ballNumber + ": " + outcomeOfBallBowled + " run || Player: " + battingTeam.getNameOfPlayer(currentPlayer));
            battingTeam.incrementTeamScore(outcomeOfBallBowled, currentPlayer);
            strike.changeStrike(outcomeOfBallBowled);
            return false;
        }

        int outPlayer = strike.updateStrikeOnWicket();
        battingTeam.updateWickets();
        bowlingTeam.incrementWicketsTakenByBowler(strike.getCurrentBowler());
        System.out.println(over + "." + ballNumber + ": Wicket-" + battingTeam.getTotalWicketsFallen() + " || Player: " + battingTeam.getNameOfPlayer(outPlayer));
        return (battingTeam.getTotalWicketsFallen() == battingTeam.getNumberOfPlayers()-1);
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
