package com.tekion.intern.repo;

import com.tekion.intern.beans.Match;
import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.dto.Team;
import com.tekion.intern.repository.TeamRepository;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Repository
public class MatchRepository {

    public static int save(Match match){
        Connection con = null;
        int matchId = 0;
        try {
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "createMatch"), Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, match.getTeam1Id());
            ps.setInt(2, match.getTeam2Id());
            ps.setInt(3, match.getOvers());
            ps.setInt(4, (int) Math.ceil(match.getOvers() / 5.0));
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next())
                matchId = rs.getInt(1);
            con.commit();
            con.close();
            return matchId;
        } catch(SQLException sqle){
            try{
                con.rollback();
                con.close();
            } catch (Exception ignored){}
        } catch (Exception ignored){
            ignored.printStackTrace();
        }
        return matchId;
    }

    public Match findByMatchId(Integer matchId){
        Connection con = null;
        Match match = null;
        try {
            con = MySqlConnector.getConnection();
            PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "getMatchByMatchId"));
            stmt.setInt(1, matchId);
            ResultSet rs = stmt.executeQuery();
            //isExists = rs.next();
            while (rs.next()){
                match = new Match(
                        rs.getInt("match_id"), rs.getInt("team1"), rs.getInt("team2"),
                        rs.getInt("overs"), rs.getInt("maxovers"), rs.getString("winner")
                );
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception e){

            }
        }
        return match;
    }

    public void update(Match match) {
        Connection con = null;
        try{
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update MatchTable set team1 = ?, team2 = ?, winner = ? where match_id = ?");
            ps.setInt(1, match.getTeam1Id());
            ps.setInt(2, match.getTeam2Id());
            ps.setString(3, match.getMatchState().toString());
            ps.setInt(4, match.getMatchId());
            ps.executeUpdate();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            try{
                con.close();
            } catch (Exception ignored){}
            sqle.printStackTrace();
        } catch(Exception ignored) {
            ignored.printStackTrace();
        }
    }

//    public static boolean getMatchByMatchId(int matchId) throws SQLException, ClassNotFoundException{
//        Connection con = MySqlConnector.getConnection();
//        PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "getMatchByMatchId"));
//        stmt.setInt(1,matchId);
//        ResultSet rs = stmt.executeQuery();
//        return rs.next();
//    }
//
//    public static void updateTeamOrderByMatchId(int matchId, int whichTeamToBatFirst) throws SQLException, ClassNotFoundException {
//        Connection con = MySqlConnector.getConnection();
//        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "getBothTeamByMatchId"));
//        ps.setInt(1,matchId);
//        ResultSet rs = ps.executeQuery();
//        int team1 = 0, team2 = 0;
//        while(rs.next()){
//            team1 = rs.getInt(1);
//            team2 = rs.getInt(2);
//        }
//
//        String teamName = com.tekion.intern.repository.TeamRepository.getTeamNameFromTeamId((whichTeamToBatFirst == 1) ?team1 :team2);
//        System.out.println(teamName + " is Going to Bat First");
//        try {
//            con.setAutoCommit(false);
//            if (whichTeamToBatFirst != 1) {
//                ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "updateTeamOrderByMatchId"));
//                ps.setInt(1, team2);
//                ps.setInt(2, team1);
//                ps.setString(3,"STARTED");
//                ps.setInt(4, matchId);
//                ps.execute();
//            }
//            con.commit();
//            con.close();
//        } catch(SQLException sqle){
//            con.rollback();
//            con.close();
//            throw sqle;
//        }
//    }
//
//    public static Match createMatchByMatchId(int matchId) throws SQLException, ClassNotFoundException{
//        Connection con = MySqlConnector.getConnection();
//        try {
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "getMatchByMatchId"));
//            ps.setInt(1, matchId);
//            ResultSet rs = ps.executeQuery();
//
//            int team1Id = 0;
//            int team2Id = 0;
//            int overs = 0;
//            int maxOvers = 0;
//            while (rs.next()) {
//                team1Id = rs.getInt("team1");
//                team2Id = rs.getInt("team2");
//                overs = rs.getInt("overs");
//                maxOvers = rs.getInt("maxovers");
//            }
//            Team team1 = com.tekion.intern.repository.TeamRepository.createTeamFromTeamID(team1Id);
//            Team team2 = TeamRepository.createTeamFromTeamID(team2Id);
//            //con.commit();
//            con.close();
//            return new Match(overs, maxOvers, team1, team2, matchId);
//        } catch(SQLException sqle){
//            con.close();
//            throw sqle;
//        } catch(Exception e){
//            System.out.println("DB Error");
//        }
//        return null;
//    }
//
//    public static void updateWinnerByMatchId(int matchId, String winner) throws SQLException, ClassNotFoundException{
//        Connection con = MySqlConnector.getConnection();
//        try{
//            con.setAutoCommit(false);
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "updateWinnerByMatchId"));
//            ps.setString(1,winner);
//            ps.setInt(2,matchId);
//            ps.execute();
//            con.commit();
//            con.close();
//        } catch (SQLException sqle){
//            con.rollback();
//            con.close();
//            throw sqle;
//        } catch (Exception e){
//
//        }
//    }
//
//    private static List<Integer> selectTeams(Connection con) throws SQLException{
//        Statement stmt = con.createStatement();
//        ResultSet rs = stmt.executeQuery(ReaderUtil.readSqlFromFile("team", "getAll"));
//        List<Integer> teamIds = new LinkedList<>();
//        int cnt = 0;
//        while(rs.next()){
//            teamIds.add(rs.getInt("team_id"));
//            System.out.println((++cnt) + ".." + rs.getString("name"));
//        }
//        int team1, team2;
//        do{
//            team1 = ReaderUtil.getIntegerInputInRange(1,cnt);
//            team2 = ReaderUtil.getIntegerInputInRange(1,cnt);
//            if(team1 == team2)
//                System.out.println("Both Teams should be different");
//        } while(team1 == team2);
//
//        return Arrays.asList(teamIds.get(team1-1), teamIds.get(team2-1));
//    }
}
