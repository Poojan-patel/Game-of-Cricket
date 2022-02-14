package com.tekion.intern.game;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class MatchUtil {
    public static Map<Integer,Integer> batsmanRandomScore;
    public static Map<Integer,Integer> bowlerRandomScore;

    static{
        batsmanRandomScore = new TreeMap<Integer,Integer>(){{
            put(1,1); put(2,2); put(3,3); put(4,4);
            put(5,5); put(6,6); put(7,-1); put(0,0);
        }};
        bowlerRandomScore = new TreeMap<Integer,Integer>(){{
            put(1,1); put(2,1); put(3,2); put(4,2);
            put(5,3); put(6,3); put(7,4); put(8,5);
            put(9,6); put(10,-1); put(0,0); put(11,0);
        }};
    }

    public static Match.Winner decideWinner(int team1Score, int team2Score){
        Match.Winner winner;
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
        return ThreadLocalRandom.current().nextInt(0, 2);
    }

    public static int generateRandomScore(Player.PlayerType playerType){
        int randomNumber;
        int randomScore;
        if(playerType == Player.PlayerType.BATSMAN) {
            randomNumber = ThreadLocalRandom.current().nextInt(0, batsmanRandomScore.size());
            randomScore = batsmanRandomScore.get(randomNumber);
        }
        else {
            randomNumber = ThreadLocalRandom.current().nextInt(0, bowlerRandomScore.size());
            randomScore = bowlerRandomScore.get(randomNumber);
        }
        return randomScore;
    }
}
