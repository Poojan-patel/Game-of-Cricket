package com.tekion.intern.game;

import java.util.concurrent.ThreadLocalRandom;

public class MatchUtil {
    public static Match.Winner decideWinner(int team1Score, int team2Score){
        Match.Winner winner = Match.Winner.STARTED;
        int diff = team1Score - team2Score;
        if(diff > 0){
            winner = Match.Winner.TEAM1;
        } else if(diff < 0){
            winner = Match.Winner.TEAM2;
        } else{
            winner = Match.Winner.TIE;
        }
        return winner;
    }
    public static int stimulateToss(){
        return ThreadLocalRandom.current().nextInt(0,2);
    }
}
