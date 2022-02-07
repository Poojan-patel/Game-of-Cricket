package com.tekion.intern.game;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Match {
    private final Team team1;
    private final Team team2;
    enum Winner{
        TEAM1,
        TEAM2,
        TIE,
        STARTED
    }
    //private FinalScore scoreChased;
    //private FinalScore scoreToChase;
    private Winner winner;
    private int totalAvailableBalls;

    public Match(){
        Scanner sc = new Scanner(System.in);
        String name;
        System.out.print("Enter Total Number of Overs:");
        totalAvailableBalls = sc.nextInt()*6;
        sc.nextLine();
        System.out.print("Enter Team-1 name: ");
        name = sc.nextLine();
        team1 = new Team(name,totalAvailableBalls);
        System.out.print("Enter Team-2 name: ");
        team2 = new Team(sc.nextLine(),totalAvailableBalls);
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

    public void stimulateGame(){
        int tossWinner = stimulateToss();
        int scoreToChase;
        if(tossWinner == 0){
            System.out.println(team1.getTeamName() + " Will Start Batting");
            startInningOne(team1);
//            scoreToChase = team1.showFinalScore();
            scoreToChase = team1.getTeamScore();
            System.out.println(team2.getTeamName() + " Will Start Batting");
            startInningTwo(team2,scoreToChase);
//            scoreChased = team2.showFinalScore();
        } else{
            System.out.println(team2.getTeamName() + " Will Start Batting");
            startInningOne(team2);
//            scoreToChase = team2.showFinalScore();
            scoreToChase = team2.getTeamScore();
            System.out.println(team1.getTeamName() + " Will Start Batting");
            startInningTwo(team1,scoreToChase);
//            scoreChased = team1.showFinalScore();
        }
        declareTheWinner();
    }

    private int playTheBall(Team team, int ball){
        int randomNum = ThreadLocalRandom.current().nextInt(0,8);
        team.incrementTotalBalls();
        if(randomNum < 7){
            System.out.println(ball + ": " + randomNum);
            team.incrementTeamScore(randomNum);
            return 0;
        }
        int totalWicks = team.wicketFallen();
        System.out.println(ball + ": Wicket-" + totalWicks);
        return totalWicks;
    }

    private void startInningOne(Team team) {
        int overs = totalAvailableBalls/6;
        int isWicket = 0;
        Scanner sc = new Scanner(System.in);
        for(int i = 0; i < overs; i++){
            System.out.println("Over: "+(i+1));
            for(int j = 0; j < 6; j++){
                sc.nextLine();
                isWicket = playTheBall(team, j+1);
                if(isWicket == 10){
                    break;
                }
            }
            if(isWicket == 10)
                break;
        }
        //team.declareFinalScore();

    }

    private void startInningTwo(Team team, int scoreToChase) {
        int overs = totalAvailableBalls/6;
        int isWicket = 0;
        Scanner sc = new Scanner(System.in);
        for(int i = 0; i < overs; i++){
            System.out.println("Over: "+(i+1));
            for(int j = 0; j < 6; j++){
                sc.nextLine();
                isWicket = playTheBall(team, j+1);
                if(isWicket == 10 || team.getTeamScore() > scoreToChase)
                    break;
            }
            if(isWicket == 10 || team.getTeamScore() > scoreToChase)
                break;
        }
        //team.declareFinalScore();
    }
}
