package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.game.Match;
import com.tekion.intern.game.MatchUtil;
import com.tekion.intern.game.Team;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MatchRepository {

    public static int createMatch(int numOfOvers) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        List<Integer> selectedTeams = selectTeams(con);
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("insert into MatchTable(team1, team2, overs, maxovers) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, selectedTeams.get(0));
            ps.setInt(2, selectedTeams.get(1));
            ps.setInt(3, numOfOvers);
            ps.setInt(4, (int) Math.ceil(numOfOvers / 5.0));
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();

            int matchId = 0;
            while (rs.next())
                matchId = rs.getInt(1);
            con.commit();
            con.close();
            return matchId;
        } catch(SQLException sqle){
            con.rollback();
            con.close();
            throw sqle;
        }
    }

    public static boolean getMatchFromMatchId(int matchId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        PreparedStatement stmt = con.prepareStatement("select * from MatchTable where match_id = ?");
        stmt.setInt(1,matchId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public static void updateTeamOrder(int matchId, int whichTeamToBatFirst) throws SQLException, ClassNotFoundException {
        Connection con = MySqlConnector.getConnection();
        PreparedStatement ps = con.prepareStatement("select team1,team2 from MatchTable where match_id = ?");
        ps.setInt(1,matchId);
        ResultSet rs = ps.executeQuery();
        int team1 = 0, team2 = 0;
        while(rs.next()){
            team1 = rs.getInt(1);
            team2 = rs.getInt(2);
        }

        String teamName = TeamRepository.getTeamNameFromTeamId((whichTeamToBatFirst == 1) ?team1 :team2);
        System.out.println(teamName + " is Going to Bat First");
        try {
            con.setAutoCommit(false);
            if (whichTeamToBatFirst != 1) {
                ps = con.prepareStatement("update MatchTable set team1 = ?, team2 = ?, winner = ? where match_id = ?");
                ps.setInt(1, team2);
                ps.setInt(2, team1);
                ps.setString(3,"STARTED");
                ps.setInt(4, matchId);
                ps.execute();
            }
            con.commit();
            con.close();
        } catch(SQLException sqle){
            con.rollback();
            con.close();
            throw sqle;
        }
    }

    public static Match createMatchFromDB(int matchId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("select * from MatchTable where match_id = ?");
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();

            int team1Id = 0;
            int team2Id = 0;
            int overs = 0;
            int maxOvers = 0;
            while (rs.next()) {
                team1Id = rs.getInt("team1");
                team2Id = rs.getInt("team2");
                overs = rs.getInt("overs");
                maxOvers = rs.getInt("maxovers");
            }
            Team team1 = TeamRepository.createTeamFromTeamID(team1Id);
            Team team2 = TeamRepository.createTeamFromTeamID(team2Id);
            //con.commit();
            con.close();
            return new Match(overs, maxOvers, team1, team2, matchId);
        } catch(SQLException sqle){
            con.close();
            throw sqle;
        } catch(Exception e){
            System.out.println("DB Error");
        }
        return null;
    }

    public static void updateWinner(int matchId, String winner) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try{
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update MatchTable set winner = ? where match_id = ?");
            ps.setString(1,winner);
            ps.setInt(2,matchId);
            ps.execute();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            con.rollback();
            con.close();
            throw sqle;
        } catch (Exception e){

        }
    }

    private static List<Integer> selectTeams(Connection con) throws SQLException{
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from team");
        List<Integer> teamIds = new LinkedList<>();
        int cnt = 0;
        while(rs.next()){
            teamIds.add(rs.getInt("team_id"));
            System.out.println((++cnt) + ".." + rs.getString("name"));
        }
        int team1, team2;
        do{
            team1 = MatchUtil.getIntegerInputInRange(1,cnt);
            team2 = MatchUtil.getIntegerInputInRange(1,cnt);
            if(team1 == team2)
                System.out.println("Both Teams should be different");
        } while(team1 == team2);

        return Arrays.asList(teamIds.get(team1-1), teamIds.get(team2-1));
    }
}
