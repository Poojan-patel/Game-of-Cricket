package com.tekion.cricket.repository;

import com.tekion.cricket.beans.BallEvent;
import com.tekion.cricket.constants.Common;
import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.models.MatchResult;
import com.tekion.cricket.util.MatchUtil;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BallEventsRepositoryImpl implements BallEventsRepository{
    public void save(BallEvent ballEvent) {
        Connection con = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "insertEvent"));
            ps.setInt(1,ballEvent.getMatchId());
            ps.setInt(2,ballEvent.getTeam());
            ps.setInt(3,ballEvent.getBallNumber());
            ps.setInt(6,ballEvent.getScore());

            if(ballEvent.getBatsman() != -1) {
                ps.setInt(4, ballEvent.getBatsman());
            }
            else {
                ps.setNull(4, Types.INTEGER);
            }

            if(ballEvent.getBowler() != -1) {
                ps.setInt(5, ballEvent.getBowler());
            }
            else {
                ps.setNull(5, Types.INTEGER);
            }

            if("".equals(ballEvent.getUnfairBallType())) {
                ps.setNull(7, Types.VARCHAR);
            }
            else {
                ps.setString(7, ballEvent.getUnfairBallType());
            }

            if("".equals(ballEvent.getWicketType())) {
                ps.setNull(8, Types.VARCHAR);
            }
            else {
                ps.setString(8, ballEvent.getWicketType());
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

    @Override
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

    @Override
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
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored){}
        }

        return bowlerWithThrownOvers;
    }

    @Override
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
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored){}
        }

        return scoreToChase;
    }

    @Override
    public List<BallEvent> fetchAllEventsByMatchAndTeamId(int matchId, int teamId) {
        Connection con = null;
        List<BallEvent> ballEvents = new ArrayList<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "fetchAllEventsByMatchAndTeamId"));
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ballEvents.add(new BallEvent(rs.getInt("event_id"), rs.getInt("match_id"), rs.getInt("team"),
                        rs.getInt("ballnumber"), rs.getInt("batsman"), rs.getInt("bowler"), rs.getInt("score"),
                        rs.getString("extra"), rs.getString("wicket"))
                );
            }
        } catch(SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored){}
        }
        return ballEvents;
    }

    private void getBowlingStats(int matchId, Connection con, MatchResult matchResult) throws SQLException {
        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getBowlingStats"));
        ps.setInt(1,matchId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            matchResult.appendBowlingStats(
                    rs.getString("name") + ": " +
                            Common.WICKETS + ":" + rs.getInt("Wicket") +
                            Common.SINGLE_SPACE + Common.OVERS + ":" + rs.getInt("Balls Thrown")/Common.BALLS_IN_ONE_OVER + "." +
                            rs.getInt("Balls Thrown")%Common.BALLS_IN_ONE_OVER + Common.SINGLE_SPACE + Common.EXTRAS + ": " + rs.getInt("Extra")
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
                            rs.getInt("Score") + ", " + Common.BALLS + ": " + rs.getInt("Balls Played")
            );
        }
    }

    private void getTeamScore(int matchId, Connection con, MatchResult matchResult) throws SQLException{
        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getTeamScore"));
        ps.setInt(1,matchId);
        ResultSet rs = ps.executeQuery();
        List<String> teamName = new ArrayList<>();
        List<Integer> teamScore = new ArrayList<>();
        while(rs.next()){
            matchResult.appendTeamScores(
                    rs.getString("name") + ": " +
                            rs.getInt("Total Score") + "/" + rs.getInt("Total Wickets") + Common.SINGLE_SPACE +
                            Common.OVERS + ": " + rs.getInt("Total Balls")/Common.BALLS_IN_ONE_OVER + "." +
                            rs.getInt("Total Balls")%Common.BALLS_IN_ONE_OVER + Common.SINGLE_SPACE + Common.EXTRAS + ": " + rs.getInt("Extras")
            );
            teamName.add(rs.getString("name"));
            teamScore.add(rs.getInt("Total Score"));
        }
        matchResult.setWinner(MatchUtil.decideWinner(teamName, teamScore));
    }

}
