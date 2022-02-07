package com.tekion.intern.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//class FinalScore{
//    public int score;
//    public int wickets;
//    public int balls;
//
//    public FinalScore(int score, int wickets, int balls){
//        this.score = score;
//        this.wickets = wickets;
//        this.balls = balls;
//    }
//}

class Team {
    private String teamName;
    private int teamScore;
    private int totalPlayedBalls;
    private List<Player> players;
    private int currentPlayer;
    //private FinalScore finalScore;
    private int totalAvailableBalls;

    public Team(String teamName, int balls) {
        this.teamName = teamName;
        teamScore = 0;
        totalPlayedBalls = 0;
        currentPlayer = 0;
        totalAvailableBalls = balls;
        players = new ArrayList<>();
        setPlayers();
    }

    public String getTeamName() {
        return teamName;
    }

    private void setPlayers(){
        Scanner sc = new Scanner(System.in);
        String name;
        for(int i = 0; i < 10; i++){
            System.out.print("Player-"+(i+1)+": ");
            name = sc.nextLine();
            players.add(new Player(name));
        }
    }

    public void getPlayerwiseScore(){
        int playersWhoPlayed = Integer.min(currentPlayer+1,10);
        Player p;
        for(int i = 0; i < playersWhoPlayed; i++){
            p = players.get(i);
            System.out.println(String.format("%s: %d runs in %d balls", p.getName(), p.getScore(), p.getBallsPlayed()));
        }
    }

//    public FinalScore getFinalScore(){
//        if(currentPlayer < 10 || totalPlayedBalls < totalAvailableBalls){
//            throw new IllegalStateException("Final Score will be available at the end of the inning");
//        }
//        return finalScore;
//    }

    public int getTeamScore() {
        return teamScore;
    }

    private void updatePlayerScore(int score){
        Player player = players.get(currentPlayer);
        player.incrementScore(score);
    }

    public void incrementTeamScore(int score) {
        this.teamScore += score;
        updatePlayerScore(score);
    }

    public int getTotalPlayedBalls() {
        return totalPlayedBalls;
    }

    public void incrementTotalBalls() {
        this.totalPlayedBalls++;
        updatePlayerBalls();
    }

    private void updatePlayerBalls() {
        Player player = players.get(currentPlayer);
        player.incrementBallsPlayed();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

//    public FinalScore showFinalScore(){
//        return finalScore;
//    }

//    public void declareFinalScore(){
//        finalScore = new FinalScore(teamScore, currentPlayer, totalPlayedBalls);
//    }

    public int wicketFallen() {
        return ++this.currentPlayer;
    }
}
