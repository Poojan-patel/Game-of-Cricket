package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.models.MatchResult;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BallEventsRepository{
    public void insertEvent
            (int matchId, int teamId, int ballNumber, int batsmanId, int bowlerId, int score, String extras, String wicket)
    {
        Connection con = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "insertEvent"));
            ps.setInt(1,matchId);
            ps.setInt(2,teamId);
            ps.setInt(3,ballNumber);
            ps.setInt(6,score);

            if(batsmanId != -1) {
                ps.setInt(4, batsmanId);
            }
            else {
                ps.setNull(4, Types.INTEGER);
            }

            if(bowlerId != -1) {
                ps.setInt(5, bowlerId);
            }
            else {
                ps.setNull(5, Types.INTEGER);
            }

            if(extras.equals("")) {
                ps.setNull(7, Types.VARCHAR);
            }
            else {
                ps.setString(7, extras);
            }

            if(wicket.equals("")) {
                ps.setNull(8, Types.VARCHAR);
            }
            else {
                ps.setString(8, wicket);
            }

            ps.execute();
        } catch(SQLException sqle){
            sqle.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ignored) {}
        }
    }

    public MatchResult generateFinalScoreBoard(int matchId) {
        Connection con = null;
        MatchResult matchResult = new MatchResult();
        try{
            con = MySqlConnector.getConnection();
            getTeamScore(matchId, con, matchResult);
            getBatsmanStats(matchId, con, matchResult);
            getBowlingStats(matchId, con, matchResult);
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ignored) {}
        }
        return matchResult;
    }

    public Map<Integer, Integer> fetchBowlersWithThrownOversByTeamAndMatchId(Integer matchId, Integer currentBowlTeamId) {
        Connection con = null;
        Map<Integer, Integer> bowlerWithThrownOvers = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "fetchBowlersWithThrownOversByTeamAndMatchId"));
            ps.setInt(1, matchId);
            ps.setInt(2, currentBowlTeamId);
            ResultSet rs = ps.executeQuery();
            bowlerWithThrownOvers = new HashMap<>();
            while(rs.next()){
                bowlerWithThrownOvers.put(rs.getInt("bowler"), (int)Math.ceil(rs.getDouble("overs")));
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception ignored){
            ignored.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored){}
        }

        return bowlerWithThrownOvers;
    }

    public int fetchScoreToChase(int matchId, int currentBowlTeamId) {
        Connection con = null;
        int scoreToChase = -1;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "fetchScoreToChase"));
            ps.setInt(1, matchId);
            ps.setInt(2, currentBowlTeamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                scoreToChase = rs.getInt(1);
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception ignored){
            ignored.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored){}
        }

        return scoreToChase;
    }

    private void getBowlingStats(int matchId, Connection con, MatchResult matchResult) throws SQLException {
        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getBowlingStats"));
        ps.setInt(1,matchId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            matchResult.appendBowlingStats(
                    rs.getString("name") + ": " +
                            "Wickets:" + rs.getInt("Wicket") +
                            " Overs:" + rs.getInt("Balls Thrown")/6 + "." + rs.getInt("Balls Thrown")%6 +
                            " Extras: " + rs.getInt("Extra")
            );
        }
    }

    private void getBatsmanStats(int matchId, Connection con, MatchResult matchResult) throws SQLException{
        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getBatsmanStats"));
        ps.setInt(1,matchId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            matchResult.appendBattingStats(
                    rs.getString("name") + ": " +
                            rs.getInt("Score") + ", Balls:" + rs.getInt("Balls Played")
            );
        }
    }

    private void getTeamScore(int matchId, Connection con, MatchResult matchResult) throws SQLException{
        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getTeamScore"));
        ps.setInt(1,matchId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            matchResult.appendTeamScores(
                    rs.getString("name") + ": " +
                            rs.getInt("Total Score") + "/" + rs.getInt("Total Wickets") +
                            " Overs: " + rs.getInt("Total Balls")/6 + "." + rs.getInt("Total Balls")%6 +
                            " Extras: " + rs.getInt("Extras")
            );
        }
    }

}
