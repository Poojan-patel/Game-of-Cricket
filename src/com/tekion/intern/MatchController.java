package com.tekion.intern;

import com.tekion.intern.game.Match;
import com.tekion.intern.game.MatchUtil;

import java.util.*;

public class MatchController {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Match newMatch = initializeMatchData();

        int headOrTail = MatchUtil.stimulateToss();
        System.out.println("0.. Fielding, 1.. Batting:");
        int choiceOfInning = getIntegerInputInRange(0, 1);

        newMatch.stimulateGame(headOrTail, choiceOfInning);
        newMatch.showFinalScoreBoard();
    }

    private static int getIntegerInputInRange(int lower){
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

    private static int getIntegerInputInRange(int lower, int upper){
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

    private static String getNonEmptyString(){
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

    private static String getStringFromAcceptableValues(List<String> acceptableValues){
        String input = "";
        while(true){
            input = sc.nextLine().toUpperCase();
            if(acceptableValues.contains(input)){
                return input;
            }
            System.out.println("Enter Value from:" + acceptableValues);
        }
    }

    private static Match initializeMatchData() {
        System.out.print("Number Of Overs:");
        int numOfOvers = getIntegerInputInRange(1, 50);

        System.out.print("Number Of Players:");
        int numOfPlayers = getIntegerInputInRange(2, 11);

        System.out.print("Enter Team-1 Name:");
        String team1 = getNonEmptyString().toUpperCase();
        List<String> team1PlayersTypes = new ArrayList<>();
        List<String> team1PlayersNames = new ArrayList<>();
        initializeTeamPlayers(numOfPlayers, team1PlayersNames, team1PlayersTypes);

        System.out.print("Enter Team-2 Name:");
        String team2 = getNonEmptyString().toUpperCase();
        List<String> team2PlayersTypes = new ArrayList<>();
        List<String> team2PlayersNames = new ArrayList<>();
        initializeTeamPlayers(numOfPlayers, team2PlayersNames, team2PlayersTypes);

        Match newMatch = new Match(numOfOvers, team1, team1PlayersNames, team1PlayersTypes, team2, team2PlayersNames, team2PlayersTypes);
        return newMatch;
    }

    private static void initializeTeamPlayers(int numOfPlayers, List<String> playerNames, List<String> playerTypes){
        List<String> acceptablePlayerTypes = Arrays.asList(new String[]{"BATSMAN","BOWLER"});
        for(int i = 1; i <= numOfPlayers; i++){
            System.out.print("Player-" + i + " Name:");
            playerNames.add(getNonEmptyString());

            System.out.print("Player-" + i  +" Type:");
            playerTypes.add(getStringFromAcceptableValues(acceptablePlayerTypes));
        }

    }
}
