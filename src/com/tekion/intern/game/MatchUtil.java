package com.tekion.intern.game;

import com.tekion.intern.MatchController;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MatchUtil {
    private static final Map<Integer,Integer> batsmanRandomScore;
    private static final Map<Integer,Integer> bowlerRandomScore;
    private static final String os = System.getProperty("os.name").toLowerCase();
    private static Scanner sc = new Scanner(System.in);

    static{
        batsmanRandomScore = new HashMap<Integer,Integer>(){{
            put(0,0); put(1,1); put(2,2); put(3,3);
            put(4,4); put(5,5); put(6,6); put(7,-1);
        }};
        bowlerRandomScore = new HashMap<Integer,Integer>(){{
            put(0,0); put(1,0); put(2,1); put(3,1);
            put(4,2); put(5,2); put(6,3); put(7,4);
            put(8,5); put(9,6); put(10,-1); put(11,-1);
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
        if(playerType != Player.PlayerType.BOWLER) {
            randomNumber = ThreadLocalRandom.current().nextInt(0, batsmanRandomScore.size());
            randomScore = batsmanRandomScore.get(randomNumber);
        }
        else {
            randomNumber = ThreadLocalRandom.current().nextInt(0, bowlerRandomScore.size());
            randomScore = bowlerRandomScore.get(randomNumber);
        }
        return randomScore;
    }

    public static int selectBowler(Team bowlingTeam, List<Integer> availableBowlers) {
        System.out.println("Select your Bowler:\nPress");
        for(int i = 0; i < availableBowlers.size(); i++){
            System.out.println((i+1) + ": " + bowlingTeam.getNameOfPlayer(availableBowlers.get(i)));
        }
        int choiceOfBowlerPosition = getIntegerInputInRange(1, availableBowlers.size());
        return availableBowlers.get(choiceOfBowlerPosition-1);
    }

    public static String getStringFromAcceptableValues(List<String> acceptableValues){
        String input = "";
        while(true){
            input = sc.nextLine().toUpperCase();
            if(acceptableValues.contains(input)){
                return input;
            }
            System.out.println("Enter Value from:" + acceptableValues);
        }
    }

    public static String getNonEmptyString(){
        String input = "";
        while(true){
            input = sc.nextLine();
            if(input.isEmpty()){
                System.out.println("Enter Non-empty Value");
            }
            else
                break;
        }
        return input;
    }

    public static int getIntegerInputInRange(int lower){
        int input;
        while(true){
            try{
                input = Integer.parseInt(sc.nextLine());
                if(input < lower)
                    System.out.println("Value should not be less than " + lower);
                else
                    break;
            } catch(NumberFormatException nfe){
                System.out.println("Enter an integer");
            }
        }
        return input;
    }

    public static int getIntegerInputInRange(int lower, int upper){
        int input;
        while(true){
            try{
                input = Integer.parseInt(sc.nextLine());
                if(input < lower || input > upper){
                    System.out.println(String.format("Value should be between %d and %d", lower, upper));
                }
                else
                    break;
            } catch(NumberFormatException nfe){
                System.out.println("Enter an integer");
            }
        }
        return input;
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
}
