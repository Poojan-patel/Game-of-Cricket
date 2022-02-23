package com.tekion.intern;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.game.Match;
import com.tekion.intern.game.MatchUtil;
import com.tekion.intern.game.Team;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class MatchController {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("1... Initialize Team");
        System.out.print("2... Initialize Match with Teams:");
        int choice = MatchUtil.getIntegerInputInRange(1,2);
        if(choice == 1){
            initializeTeam();
            return;
        }
        System.out.println("Enter Number of Overs:");
        try {
            int matchId = MySqlConnector.initializeMatch(MatchUtil.getIntegerInputInRange(1, 50));
            System.out.println("Note down MatchID:" + matchId);
            System.out.println("Use MatchID to play the game");
        } catch(SQLException sqle){
            System.out.println(sqle);
            System.out.println("Match Creation Unsuccessful");
        } catch(Exception e){
            System.out.println("DB Error");
        }
//        Match newMatch = initializeMatchData();
//
//        int headOrTail = MatchUtil.stimulateToss();
//        System.out.println("0.. Fielding, 1.. Batting:");
//        int choiceOfInning = MatchUtil.getIntegerInputInRange(0, 1);
//
//        newMatch.stimulateGame(headOrTail, choiceOfInning);
//        newMatch.showFinalScoreBoard();
    }

    private static void initializeTeam() {
        System.out.print("Enter Team Name:");
        String teamName = MatchUtil.getNonEmptyString();
        System.out.println("Total of 11 Players should be added");
        List<String> teamPlayerNames = new ArrayList<>();
        List<String> teamPlayerTypes = new ArrayList<>();
        initializeTeamPlayers(11,teamName,teamPlayerNames,teamPlayerTypes);
        Team team = new Team(teamName, teamPlayerNames, teamPlayerTypes);
        try{
            MySqlConnector.insertTeamData(team);
            System.out.println("Data Insertion Success");
        }
        catch(SQLException sqe){
            System.out.println(sqe);
            System.out.println("Data Insertion Unsuccessful");
        }
        catch(Exception e){
            System.out.println("DB Error");
        }
    }

    private static Match initializeMatchData() {
        System.out.print("Number Of Overs:");
        int numOfOvers = MatchUtil.getIntegerInputInRange(1, 50);

        System.out.print("Number Of Players:");
        int numOfPlayers = MatchUtil.getIntegerInputInRange(5, 11);

        System.out.print("Enter Team-1 Name:");
        String team1 = MatchUtil.getNonEmptyString().toUpperCase();
        List<String> team1PlayersTypes = new ArrayList<>();
        List<String> team1PlayersNames = new ArrayList<>();
        initializeTeamPlayers(numOfPlayers, team1, team1PlayersNames, team1PlayersTypes);

        System.out.print("Enter Team-2 Name:");
        String team2 = MatchUtil.getNonEmptyString().toUpperCase();
        List<String> team2PlayersTypes = new ArrayList<>();
        List<String> team2PlayersNames = new ArrayList<>();
        initializeTeamPlayers(numOfPlayers, team2, team2PlayersNames, team2PlayersTypes);

        Match newMatch = new Match(numOfOvers, team1, team1PlayersNames, team1PlayersTypes, team2, team2PlayersNames, team2PlayersTypes);
        return newMatch;
    }

    private static void initializeTeamPlayers(int numOfPlayers, String teamName, List<String> playerNames, List<String> playerTypes){
        List<String> acceptablePlayerTypes = new LinkedList<>(Arrays.asList("BATSMAN","BOWLER","ALLROUNDER"));
        List<String> bowlerTypes = new LinkedList<>(Arrays.asList("FAST","SPIN","MEDIUM"));

        System.out.println("You must have 5 bowlers and allrounders in total in the team");
        int numOfBatsman = numOfPlayers-5;
        String singlePlayerType;
        int i;
        for(i = 1; i <= numOfPlayers; i++){
            if(numOfBatsman == 0){
                numOfBatsman--;
                System.out.println("All remaining players must be either Bowler or Allrounder");
                acceptablePlayerTypes.remove("BATSMAN");
            }
            System.out.println("Player-" + i + " Name:" + (teamName + i));
            playerNames.add(teamName + i);
            //playerNames.add(MatchUtil.getNonEmptyString());
            System.out.print("Player-" + i  +" Type:");
            singlePlayerType = MatchUtil.getStringFromAcceptableValues(acceptablePlayerTypes);
            if(numOfBatsman > 0 && singlePlayerType.equals("BATSMAN"))
                numOfBatsman--;

            if(!singlePlayerType.equals("BATSMAN")){
                System.out.print("Enter pace of bowling:");
                singlePlayerType += "," + MatchUtil.getStringFromAcceptableValues(bowlerTypes);
            }
            playerTypes.add(singlePlayerType);
        }

    }

}
