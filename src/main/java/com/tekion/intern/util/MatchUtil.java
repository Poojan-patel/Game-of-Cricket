package com.tekion.intern.util;

import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.enums.Winner;
import com.tekion.intern.game.Team;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MatchUtil {
    private static final Map<Integer,Integer> batsmanRandomScore;
    private static final Map<Integer,Integer> bowlerRandomScore;
    private static List<String> typeOfWicketFallen;
    private static final String os = System.getProperty("os.name").toLowerCase();

    static{
        batsmanRandomScore = new HashMap<Integer,Integer>(){{
            put(0,0); put(1,1); put(2,2); put(3,3);
            put(4,4); put(5,5); put(6,6);
            put(7,0); put(8,1); put(9,2); put(10,3);
            put(11,4); put(12,6); put(13,-1);
        }};
        bowlerRandomScore = new HashMap<Integer,Integer>(){{
            put(0,0); put(1,0); put(2,1); put(3,1);
            put(4,2); put(5,2); put(6,3); put(7,4);
            put(8,5); put(9,6); put(10,-1); put(11,-1);
        }};
        typeOfWicketFallen = Arrays.asList("BOLD", "CAUGHT AND BOLD", "STUMPED", "HIT WICKET", "LBW", "DOUBLE HIT", "BALL OBSTRUCTION");
    }

    public static Winner decideWinner(int team1Score, int team2Score){
        Winner winner;
        int diff = team1Score - team2Score;
        if(diff > 0){
            winner = Winner.TEAM1;
        } else if(diff < 0){
            winner = Winner.TEAM2;
        } else{
            winner = Winner.TIE;
        }
        return winner;
    }

    public static int stimulateToss(){
        return ThreadLocalRandom.current().nextInt(0, 2);
    }

    public static int generateRandomScore(PlayerType playerType, boolean wicketPossible){
        int randomNumber;
        int randomScore;
        if(playerType != PlayerType.BOWLER) {
            randomNumber = ThreadLocalRandom.current().nextInt(0, batsmanRandomScore.size() - ((wicketPossible) ?0 :1));
            randomScore = batsmanRandomScore.get(randomNumber);
        }
        else {
            randomNumber = ThreadLocalRandom.current().nextInt(0, bowlerRandomScore.size() - ((wicketPossible) ?0 :2));
            randomScore = bowlerRandomScore.get(randomNumber);
        }
        return randomScore;
        //return -1;
    }

    public static String getRandomTypeOfWicket(){
        return typeOfWicketFallen.get(ThreadLocalRandom.current().nextInt(0,7));
    }

    public static int selectBowler(Team bowlingTeam, Set<Integer> availableBowlers) {
        System.out.println("Select your Bowler:\nPress");
        List<Integer> bowlerSelectionList = new ArrayList<>();
        int cnt = 1;
        for(int i:availableBowlers){
            System.out.println((cnt++) + ": " + bowlingTeam.getNameOfPlayer(i) + '-' + bowlingTeam.getTypeOfBowler(i));
            bowlerSelectionList.add(i);
        }

        int choiceOfBowlerPosition = ReaderUtil.getIntegerInputInRange(1, availableBowlers.size());
        return bowlerSelectionList.get(choiceOfBowlerPosition-1);
    }

    public static void clearConsole() throws IOException, InterruptedException {
        if(os.contains("windows"))
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        else
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        //Runtime.getRuntime().exec("ls");
//            System.out.print("\033\143");
//            System.out.flush();
    }

    public static int decideBatterFirst(int headOrTail, int choiceOfInning) {
        if(headOrTail + choiceOfInning == 1){
            return 1;
        } else
            return 2;
    }
}
