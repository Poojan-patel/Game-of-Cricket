package com.tekion.intern;

import com.tekion.intern.game.Match;
import com.tekion.intern.game.MatchUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatchController {
    private static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.print("Number Of Overs:");
        int numOfOvers = sc.nextInt();
        sc.nextLine();

        System.out.print("Number Of Players:");
        int numOfPlayers = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Team-1 Name:");
        String team1 = sc.nextLine();
        List<String> team1PlayersTypes = new ArrayList<>();
        List<String> team1PlayersNames = new ArrayList<>();
        getTeamPlayers(numOfPlayers, team1PlayersNames, team1PlayersTypes);

        System.out.print("Enter Team-2 Name:");
        String team2 = sc.nextLine();
        List<String> team2PlayersTypes = new ArrayList<>();
        List<String> team2PlayersNames = new ArrayList<>();
        getTeamPlayers(numOfPlayers, team2PlayersNames, team2PlayersTypes);

        Match newMatch = new Match(numOfOvers, team1, team1PlayersNames, team1PlayersTypes, team2, team2PlayersNames, team2PlayersTypes);

        int headOrTail = MatchUtil.stimulateToss();
        System.out.print("0.. Fielding, 1.. Batting:");
        int choiceOfInning = sc.nextInt();
        sc.nextLine();

        newMatch.stimulateGame(headOrTail, choiceOfInning);
        newMatch.showFinalScoreBoard();
    }

    private static void getTeamPlayers(int numOfPlayers, List<String> playerNames, List<String> playerTypes){
        for(int i = 1; i <= numOfPlayers; i++){
            System.out.print("Player-"+i+" Name:");
            playerNames.add(sc.nextLine());
            System.out.print("Player-"+i+" Type:");
            playerTypes.add(sc.nextLine());
        }

    }
}
