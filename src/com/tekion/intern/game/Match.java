package com.tekion.intern.game;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Strike{
    private int currentWickets;
    private int[] strikeHolders;
    private int currentStrike;

    public Strike(){
        this.currentStrike = 0;
        this.strikeHolders = new int[]{0, 1};
        currentWickets = 0;
    }

    public void reInit() {
        this.currentStrike = 0;
        this.strikeHolders = new int[]{0, 1};
        currentWickets = 0;
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

    public int updateOnWicket(){
        currentWickets++;
        //System.out.println("|||WICKET|||");
        int low = Integer.min(strikeHolders[0], strikeHolders[1]);
        int high = Integer.max(strikeHolders[0], strikeHolders[1]);
        int wicketOf = strikeHolders[currentStrike];
        if(wicketOf == high){
            //if(currentStrike == 0)
            strikeHolders[currentStrike]++;
        }
        else
            strikeHolders[currentStrike] = strikeHolders[1-currentStrike]+1;
        return wicketOf;
    }

    public int totalWickets(){
        return currentWickets;
    }
}

public class Match {
    private final Team team1;
    private final Team team2;
    private final int NUM_OF_PLAYERS = 5;
    enum Winner{
        TEAM1,
        TEAM2,
        TIE,
        STARTED
    }
    private Winner winner;
    private int totalAvailableBalls;
    private Strike strike;

    public Match(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Total Number of Overs:");
        totalAvailableBalls = sc.nextInt()*6;
        sc.nextLine();
        System.out.print("Enter Team-1 name: ");
        team1 = new Team(sc.nextLine(),totalAvailableBalls,NUM_OF_PLAYERS);
        System.out.print("Enter Team-2 name: ");
        team2 = new Team(sc.nextLine(),totalAvailableBalls,NUM_OF_PLAYERS);
        winner = Winner.STARTED;
    }

    private int stimulateToss(){
        return ThreadLocalRandom.current().nextInt(0,2);
    }

    public void declareTheWinner(){
        int diff = team1.getTeamScore() - team2.getTeamScore();
        if(diff > 0){
            winner = Winner.TEAM1;
        } else if(diff < 0){
            winner = Winner.TEAM2;
        } else{
            winner = Winner.TIE;
        }
        System.out.println("Game Ended");
    }

    public void showFinalScoreBoard(){
        System.out.println(
                String.format("%s: %d/%d (%d.%d Overs)",
                        team1.getTeamName(),
                        team1.getTeamScore(),
                        team1.getCurrentPlayer(),
                        team1.getTotalPlayedBalls()/6,
                        team1.getTotalPlayedBalls()%6
                )
        );
        System.out.println(
                String.format("%s: %d/%d (%d.%d Overs)",
                        team2.getTeamName(),
                        team2.getTeamScore(),
                        team2.getCurrentPlayer(),
                        team2.getTotalPlayedBalls()/6,
                        team2.getTotalPlayedBalls()%6
                )
        );
        if(winner == Winner.TEAM1)
            System.out.println(team1.getTeamName() + " Won the Game");
        else if(winner == Winner.TEAM2)
            System.out.println(team2.getTeamName() + " Won the Game");
        else
            System.out.println("Game Tied");
        System.out.println("Team: " + team1.getTeamName());
        team1.getPlayerwiseScore();
        System.out.println("Team: " + team2.getTeamName());
        team2.getPlayerwiseScore();
    }

    public int choiceOfTossWinner(int tossWinner){
        Scanner sc = new Scanner(System.in);
        System.out.print("0.. Bowl, 1.. Bat: ");
        int choice = sc.nextInt();
        while(choice < 0 || choice > 1){
            System.out.print("Bad Choice\n0.. Bowl, 1.. Bat:");
            choice = sc.nextInt();
        }
        System.out.println(((tossWinner == 0)?team1.getTeamName() :team2.getTeamName()) + " has won the toss and opted for " +
                ((choice == 0)?"Fielding" :"Batting"));

        // if tossWinner is 1 and choice is 0 or tossWinner is 0 and choice is 1, in both the case team1 will bat
        return tossWinner+choice;
    }

    private void stimulateInnings(Team first, Team second){
        strike = new Strike();
        System.out.println(first.getTeamName() + " Will Start Batting");
        startInningOne(first);
        int scoreToChase = first.getTeamScore();
        strike.reInit();
        System.out.println(second.getTeamName() + " Will Start Batting");
        startInningTwo(second,scoreToChase);
    }

    public void stimulateGame(){
        int tossWinner = stimulateToss();
        int choiceOfInning = choiceOfTossWinner(tossWinner);
        //if((tossWinner == 0 && choiceOfInning == 1) || (tossWinner == 1 && choiceOfInning == 0)){
        if(choiceOfInning == 1){
            stimulateInnings(team1, team2);
        } else{
            stimulateInnings(team2, team1);
        }
        declareTheWinner();
    }

    private int playTheBall(Team team, int ball){
        int strikeIndex = strike.getCurrentStrike();
        int randomNum = ThreadLocalRandom.current().nextInt(0,8);
        team.incrementTotalBalls(strikeIndex);
        if(randomNum < 7){
            System.out.println(ball + ": " + randomNum + " of Player: "+ strikeIndex);
            team.incrementTeamScore(randomNum,strikeIndex);
            strike.changeStrike(randomNum);
            return 0;
        }
        int wicketOf = strike.updateOnWicket();
        team.wicketFallen();
        System.out.println(ball + ": Wicket-" + strike.totalWickets() + " of Player: "+ wicketOf);
        return strike.totalWickets();
    }

    private void startInningOne(Team team) {
        int overs = totalAvailableBalls/6;
        int isWicket = 0;
        Scanner sc = new Scanner(System.in);
        for(int i = 0; i < overs; i++) {
            System.out.println("Over: " + (i + 1));
            for (int j = 0; j < 6; j++) {
                sc.nextLine();
                isWicket = playTheBall(team, j + 1);
                if (isWicket == NUM_OF_PLAYERS-1) {
                    break;
                }
            }
            if (isWicket == NUM_OF_PLAYERS-1)
                break;
            strike.overChanged();
        }

    }

    private void startInningTwo(Team team, int scoreToChase) {
        int overs = totalAvailableBalls/6;
        int isWicket = 0;
        Scanner sc = new Scanner(System.in);
        for(int i = 0; i < overs; i++) {
            System.out.println("Over: " + (i + 1));
            for (int j = 0; j < 6; j++) {
                sc.nextLine();
                isWicket = playTheBall(team, j + 1);
                if (isWicket == NUM_OF_PLAYERS-1 || team.getTeamScore() > scoreToChase)
                    break;
            }
            if (isWicket == NUM_OF_PLAYERS-1 || team.getTeamScore() > scoreToChase)
                break;
            strike.overChanged();
        }
    }
}
