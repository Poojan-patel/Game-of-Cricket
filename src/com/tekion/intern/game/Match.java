package com.tekion.intern.game;

import com.tekion.intern.repository.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
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
    private int maxOversCanBeThrown;
    private Strike strike;
    private static Scanner sc = new Scanner(System.in);
    private static List<String> ballTypes = Arrays.asList("SHORT", "LONG", "LEG", "YORKER", "BOUNCE", "FULLTOSS");
    private int matchId;
    private int inning;

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
        team1 = new Team(team1Name, team1PlayerNames, team1PlayerTypes);
        team2 = new Team(team2Name, team2PlayerNames, team2PlayerTypes);
        winner = Winner.STARTED;
        this.matchId = 0;
    }

    public Match(int numOfOvers, int maxOversCanBeThrown, Team team1, Team team2, int matchId){
        totalAvailableBalls = numOfOvers*6;
        this.maxOversCanBeThrown = maxOversCanBeThrown;
        this.team1 = team1;
        this.team2 = team2;
        winner = Winner.STARTED;
        this.matchId = matchId;
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
        try {
            MatchRepository.updateWinner(matchId, winner.toString());
        } catch (SQLException sqle){
            System.out.println(sqle);
        } catch (Exception e){

        }
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
        inning = 0;
        try {
            PlayerRepository.fetchBowlersForBowlingTeam(second);
        } catch(SQLException e){
            System.out.println(e);
        } catch(Exception e){
            System.out.println(e);
        }
        startInning(first, second, -1);
        int scoreToChase = first.getTeamScore();

        System.out.println(second.getTeamName() + " Will Start Batting");
        inning = 1;
        try {
            PlayerRepository.fetchBowlersForBowlingTeam(first);
        } catch(SQLException e){
            System.out.println(e);
        } catch(Exception e){

        }
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

        strike = new Strike(matchId, battingTeam);
        int overs = totalAvailableBalls/6;
        boolean allOutOrChased = false;
        Set<Integer> availableBowlers;
        int selectedBowler;
        for(int i = 0; i < overs; i++) {
            availableBowlers = bowlingTeam.getAvailableBowlers(strike.getCurrentBowler(), strike.getPreviousBowler(), maxOversCanBeThrown, totalAvailableBalls/6 - i);
            selectedBowler = MatchUtil.selectBowler(bowlingTeam, availableBowlers);
            strike.setPreviousBowler(strike.getCurrentBowler());
            strike.setCurrentBowler(selectedBowler);
            strike.updateBowler(bowlingTeam.getPlayerId(selectedBowler));
            System.out.println("Over: " + i);
            System.out.println("-----------------------------------------------------------------");
            System.out.println("-----------------------------------------------------------------");

            allOutOrChased = playTheOver(battingTeam, bowlingTeam, scoreToChase, i);
            if (allOutOrChased)
                break;

            strike.overChanged();
            strike.updateStrikeInDB();
            MatchUtil.clearConsole();
        }
    }

    private boolean playTheOver(Team battingTeam, Team bowlingTeam, int scoreToChase, int currentOver) {
        boolean allOut;
        String nextBallType;
        for (int j = 0; j < 6; j++) {
            System.out.print("Enter Ball Type:");
            nextBallType = MatchUtil.getStringFromAcceptableValues(ballTypes);
            allOut = playTheBall(battingTeam, bowlingTeam, j + 1, currentOver, true);
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
            case "7": if(scoreToChase != -1)
                        System.out.println("Remaining score to chase:" + (scoreToChase + 1 - battingTeam.getTeamScore()) + " in " + (totalAvailableBalls - battingTeam.getTotalPlayedBalls()) + " balls");
                      break;
            default:
        }
    }

    private boolean outcomeOnWicketBall(Team battingTeam, Team bowlingTeam, int ballNumber, int over){
        int outPlayer = strike.updateStrikeOnWicket();
        strike.updateStrikeInDB();
        String typeOfWicketFallen = MatchUtil.getRandomTypeOfWicket();
        try{
            BallEventsRepository.insertEvent(
                    matchId, battingTeam.getTeamId(), inning, over*6 + ballNumber, battingTeam.getPlayerId(outPlayer),
                    bowlingTeam.getPlayerId(strike.getCurrentBowler()), 0, "", typeOfWicketFallen
            );
        } catch (SQLException sqle){
            System.out.println(sqle);
        } catch (Exception e){

        }
        battingTeam.updateWickets();
        bowlingTeam.incrementWicketsTakenByBowler(strike.getCurrentBowler());
        bowlingTeam.incrementBowlersNumberOfBalls(strike.getCurrentBowler());
        System.out.println(typeOfWicketFallen);
        System.out.println(over + "." + ballNumber + ": Wicket-" + battingTeam.getTotalWicketsFallen() + " || Player: " + battingTeam.getNameOfPlayer(outPlayer));
        strike.removeOutPlayer(battingTeam, outPlayer);
        return (battingTeam.getTotalWicketsFallen() == battingTeam.getNumberOfPlayers()-1);
    }

    private boolean legitimateBall(Team battingTeam, Team bowlingTeam, int ballNumber, int over, int outcomeOfBallBowled){
        int currentPlayer = strike.getCurrentStrike();
        System.out.println(over + "." + ballNumber + ": " + outcomeOfBallBowled + " run || Player: " + battingTeam.getNameOfPlayer(currentPlayer));
        battingTeam.incrementTeamScore(outcomeOfBallBowled, currentPlayer);
        strike.changeStrike(outcomeOfBallBowled);

        try {
            BallEventsRepository.insertEvent(
                    matchId, battingTeam.getTeamId(), inning, over * 6 + ballNumber, battingTeam.getPlayerId(currentPlayer),
                    bowlingTeam.getPlayerId(strike.getCurrentBowler()), outcomeOfBallBowled, "", ""
            );
        } catch (SQLException sqle){
            System.out.println(sqle);
        } catch (Exception e){

        }

        if(outcomeOfBallBowled%2 == 1)
            strike.updateStrikeInDB();

        if(outcomeOfBallBowled != 4 && outcomeOfBallBowled != 6) {
            int possibilityOfRunOut = ThreadLocalRandom.current().nextInt(0, 10);
            if(possibilityOfRunOut == 9){
                System.out.println("RunOut-" + battingTeam.getNameOfPlayer(strike.getCurrentStrike()));
                try{
                    BallEventsRepository.insertEvent(
                            matchId, battingTeam.getTeamId(), inning, over*6 + ballNumber,
                            battingTeam.getPlayerId(strike.getCurrentStrike()),
                            -1, 0, "", "RUN OUT"
                    );
                } catch (SQLException sqle){
                    System.out.println(sqle);
                } catch (Exception e){

                }
                int outPlayer = strike.updateStrikeOnWicket();
                battingTeam.updateWickets();
                strike.updateStrikeInDB();
                strike.removeOutPlayer(battingTeam, outPlayer);
                return (battingTeam.getTotalWicketsFallen() == battingTeam.getNumberOfPlayers()-1);
            }
        }
        return false;
    }

    /*
        When a ball is delivered, choices can be, 1.. Wicket (C&B, B, Stumped, .., all except runout)
        2.. ball with runs possible, can be zero
        for 2nd, after generation of random run, we can randomly generate whether it was wide/no ball
            if it is, then increment one run and play again the same ball, till there is one legitimate ball thrown where pattern ends
                on a wide ball, any wicket can be possible, same as legitimate ball
                on a no ball, on that ball as well as on next ball, free hit, only runout is possible. this possibility of wicket (mentioned is 1st type)
                is handle by wicketPossible parameter.
            if not, we will call legitimateBall, where also we will generate a random number for possibility of run out.
     */
    private boolean playTheBall(Team battingTeam, Team bowlingTeam, int ballNumber, int over, boolean wicketPossible){
        if(ballNumber == 6){
            over++;
            ballNumber = 0;
        }
        int currentPlayer = strike.getCurrentStrike();
        int outcomeOfBallBowled = MatchUtil.generateRandomScore(battingTeam.getPlayerType(currentPlayer), wicketPossible);

        if(outcomeOfBallBowled == -1){
            battingTeam.incrementTotalBalls(currentPlayer);
            return outcomeOnWicketBall(battingTeam, bowlingTeam, ballNumber, over);
        }
        else{
            int possibilityOfUnFairBall = ThreadLocalRandom.current().nextInt(0,9);
            if(possibilityOfUnFairBall <= 1){
                battingTeam.incrementTeamScoreForUnfair();
                String typeOfUnFairBall;
                System.out.println((typeOfUnFairBall = (possibilityOfUnFairBall == 0) ?"WIDE" :"NO BALL") + " : 1 run");
                try {
                    BallEventsRepository.insertEvent(
                            matchId, battingTeam.getTeamId(), inning, over * 6 + ballNumber,
                            -1,
                            bowlingTeam.getPlayerId(strike.getCurrentBowler()), 1, typeOfUnFairBall, ""
                    );
                } catch (SQLException sqle){
                    System.out.println(sqle);
                } catch (Exception e){

                }
                boolean isAllOut = legitimateBall(battingTeam, bowlingTeam, ballNumber, over, outcomeOfBallBowled);
                if(isAllOut)
                    return true;
                System.out.println("-----------------------------------------------------------------");
                return playTheBall(battingTeam, bowlingTeam, ballNumber, over, (possibilityOfUnFairBall == 0));
            }
            else {
                battingTeam.incrementTotalBalls(currentPlayer);
                bowlingTeam.incrementBowlersNumberOfBalls(strike.getCurrentBowler());
                return legitimateBall(battingTeam, bowlingTeam, ballNumber, over, outcomeOfBallBowled);
            }
        }
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
