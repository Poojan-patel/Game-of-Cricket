package com.tekion.intern;

import com.tekion.intern.game.Match;
import com.tekion.intern.game.MatchUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatchController {
    private static List<String> getTeamPlayers(int numOfPlayers){
        Scanner sc = new Scanner(System.in);
        List<String> players = new ArrayList<>();
        for(int i = 1; i <= numOfPlayers; i++){
            System.out.print("Player-"+i+" Name:");
            players.add(sc.nextLine());
            System.out.print("Player-"+i+" Type:");
            players.add(sc.nextLine());
        }
        return players;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Number Of Overs:");
        int numOfOvers = sc.nextInt();
        sc.nextLine();

        System.out.print("Number Of Players:");
        int numOfPlayers = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Team-1 Name:");
        String team1 = sc.nextLine();
        List<String> team1Players = getTeamPlayers(numOfPlayers);

        System.out.print("Enter Team-2 Name:");
        String team2 = sc.nextLine();
        List<String> team2Players = getTeamPlayers(numOfPlayers);

        Match m = new Match(numOfOvers, team1, team1Players, team2, team2Players);

        int headOrTail = MatchUtil.stimulateToss();
        System.out.print("0.. Fielding, 1.. Batting:");
        int choiceOfInning = sc.nextInt();
        sc.nextLine();

        m.stimulateGame(headOrTail, choiceOfInning);
        m.showFinalScoreBoard();
    }
}
