package com.tekion.intern.repo;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.models.MatchResult;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BallEventsRepository{
    public void insertEvent
            (int matchId, int teamId, int inning, int ballNumber, int batsmanId, int bowlerId, int score, String extras, String wicket)
    {
        Connection con = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "insertEvent"));
            ps.setInt(1,matchId);
            ps.setInt(2,teamId);
            ps.setInt(3,inning);
            ps.setInt(4,ballNumber);
            ps.setInt(7,score);

            if(batsmanId != -1) ps.setInt(5, batsmanId);
            else                ps.setNull(5, Types.INTEGER);

            if(bowlerId != -1)  ps.setInt(6, bowlerId);
            else                ps.setNull(6, Types.INTEGER);

            if(extras.equals(""))   ps.setNull(8,Types.VARCHAR);
            else                    ps.setString(8,extras);

            if(wicket.equals(""))   ps.setNull(9, Types.VARCHAR);
            else                    ps.setString(9,wicket);

            ps.execute();
            con.close();
        } catch(SQLException sqle){
            try {
                con.close();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch(Exception ignored){
            ignored.printStackTrace();
        }
    }

    public MatchResult generateFinalScoreBoard(int matchId) {
        Connection con = null;
        MatchResult matchResult = new MatchResult();
        try{
            con = MySqlConnector.getConnection();
            //System.out.println("Team Scores");
            getTeamScore(matchId, con, matchResult);
            //System.out.println("---------------------------------------------------");
            //System.out.println("Batsman Scores");
            getBatsmanStats(matchId, con, matchResult);
            //System.out.println("---------------------------------------------------");
            //System.out.println("Bowling Stats");
            getBowlingStats(matchId, con, matchResult);
            con.close();
        } catch (SQLException sqle){
            try {
                con.close();
            } catch (Exception ignored) {}
        } catch (Exception e){
            e.printStackTrace();
        }
        return matchResult;
    }

    private static void getBowlingStats(int matchId, Connection con, MatchResult matchResult) throws SQLException {
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

    private static void getBatsmanStats(int matchId, Connection con, MatchResult matchResult) throws SQLException{
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

    private static void getTeamScore(int matchId, Connection con, MatchResult matchResult) throws SQLException{
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
            try{
                con.close();
            } catch (Exception ignored){}
            sqle.printStackTrace();
        } catch (Exception ignored){
            ignored.printStackTrace();
        }

        return bowlerWithThrownOvers;
    }

    public int fetchScoreToChase(int matchId, int currentBowlTeamId) {
        Connection con = null;
        int scoreToChase = -1;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement("select sum(score) from BallEvents where match_id = ? and team = ?");
            ps.setInt(1, matchId);
            ps.setInt(2, currentBowlTeamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                scoreToChase = rs.getInt(1);
            con.close();
        } catch (SQLException sqle){
            try{
                con.close();
            } catch (Exception ignored){}
            sqle.printStackTrace();
        } catch (Exception ignored){
            ignored.printStackTrace();
        }

        return scoreToChase;
    }
}
