package com.tekion.cricket.repository;

import com.tekion.cricket.beans.BallEvent;
import com.tekion.cricket.constants.Common;
import com.tekion.cricket.models.MatchResult;
import com.tekion.cricket.util.MatchUtil;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BallEventsRepositoryImpl implements BallEventsRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(BallEvent ballEvent) {
        try{
            jdbcTemplate.update(ReaderUtil.readSqlFromFile("ballevents", "insertEvent"),
                    ballEvent.getMatchId(), ballEvent.getBallNumber(), ballEvent.getBattingTeam(), (ballEvent.getBatsman() != -1) ?ballEvent.getBatsman() :null,
                    (ballEvent.getBowler() != -1) ?ballEvent.getBowler() :null, ballEvent.getScore(),
                    Common.EMPTYSTRING.equals(ballEvent.getUnfairBallType()) ?null :ballEvent.getUnfairBallType(),
                    Common.EMPTYSTRING.equals(ballEvent.getWicketType()) ?null :ballEvent.getWicketType()
            );
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        Connection con = null;
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "insertEvent"));
//            ps.setString(1, ballEvent.getMatchId());
//            ps.setInt(2, ballEvent.getBallNumber());
//            ps.setString(3, ballEvent.getBattingTeam());
//            ps.setString(5, ballEvent.getBowlingTeam());
//            ps.setInt(7, ballEvent.getScore());
//
//            if(ballEvent.getBatsman() != -1) {
//                ps.setInt(4, ballEvent.getBatsman());
//            }
//            else {
//                ps.setNull(4, Types.INTEGER);
//            }
//
//            if(ballEvent.getBowler() != -1) {
//                ps.setInt(6, ballEvent.getBowler());
//            }
//            else {
//                ps.setNull(6, Types.INTEGER);
//            }
//
//            if(Common.EMPTYSTRING.equals(ballEvent.getUnfairBallType())) {
//                ps.setNull(8, Types.VARCHAR);
//            }
//            else {
//                ps.setString(8, ballEvent.getUnfairBallType());
//            }
//
//            if(Common.EMPTYSTRING.equals(ballEvent.getWicketType())) {
//                ps.setNull(9, Types.VARCHAR);
//            }
//            else {
//                ps.setString(9, ballEvent.getWicketType());
//            }
//
//            ps.execute();
//        } catch(SQLException sqle){
//            sqle.printStackTrace();
//        } catch(Exception e){
//            e.printStackTrace();
//        } finally {
//            try {
//                con.close();
//            } catch (Exception ignored) {}
//        }
    }

    @Override
    public MatchResult generateFinalScoreBoard(String matchId, String team1Id, String team2Id) {
        //Connection con = null;
        MatchResult matchResult = new MatchResult();
        try{
            //con = MySqlConnector.getConnection();
            getTeamScore(matchId, matchResult);
            getBatsmanStats(matchId, matchResult);
            getBowlingStats(matchId, matchResult, team1Id);
            getBowlingStats(matchId, matchResult, team2Id);
        } catch (DataAccessException | SQLException e){
            e.printStackTrace();
        }
//        catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try {
//                con.close();
//            } catch (Exception ignored) {}
//        }
        return matchResult;
    }

    @Override
    public Map<Integer, Integer> fetchBowlersWithThrownOversByTeamAndMatchId(String matchId, String currentBowlTeamId) {
        //Connection con = null;
        Map<Integer, Integer> bowlerWithThrownOvers = new HashMap<>();
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "fetchBowlersWithThrownOversByTeamAndMatchId"));
//            ps.setString(1, matchId);
//            ps.setString(2, currentBowlTeamId);
//            ResultSet rs = ps.executeQuery();
//            bowlerWithThrownOvers = new HashMap<>();
//            while(rs.next()){
//                bowlerWithThrownOvers.put(rs.getInt("bowler"), (int)Math.ceil(rs.getDouble("overs")));
//            }
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored){}
//        }
        try {
            jdbcTemplate.query(ReaderUtil.readSqlFromFile("ballevents", "fetchBowlersWithThrownOversByTeamAndMatchId"), rs -> {
                bowlerWithThrownOvers.put(rs.getInt("bowler"), (int)Math.ceil(rs.getDouble("overs")));
            } , matchId, currentBowlTeamId);
        } catch (DataAccessException dae){
            dae.printStackTrace();
            return null;
        }

        return bowlerWithThrownOvers;
    }

    @Override
    public int fetchScoreToChase(String matchId, String currentBowlTeamId) {
        //Connection con = null;
        Integer scoreToChase = null;
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "fetchScoreToChase"));
//            ps.setString(1, matchId);
//            ps.setString(2, currentBowlTeamId);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next())
//                scoreToChase = rs.getInt(1);
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored){}
//        }
        try {
            scoreToChase = jdbcTemplate.queryForObject(ReaderUtil.readSqlFromFile("ballevents", "fetchScoreToChase"), Integer.class, matchId, currentBowlTeamId);
        } catch (DataAccessException | NullPointerException e){
            e.printStackTrace();
        }

        return ((scoreToChase == null) ?-1 :scoreToChase);
    }

    @Override
    public List<BallEvent> fetchAllEventsByMatchAndTeamId(String matchId, String teamId, int batsmanOffset) {
        //Connection con = null;
        List<BallEvent> ballEvents = new ArrayList<>();
        int bowlerOffset = Common.NUM_OF_PLAYERS - batsmanOffset;
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "fetchAllEventsByMatchAndTeamId"));
//            ps.setString(1, matchId);
//            ps.setString(2, teamId);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next()){
//                ballEvents.add(new BallEvent(rs.getString("event_id"), rs.getString("match_id"), rs.getString("batting_team"),
//                        rs.getInt("batsman") + batsmanOffset, rs.getInt("ball_number"),
//                        rs.getInt("bowler") + bowlerOffset, rs.getInt("score"), rs.getString("unfair_ball_type"), rs.getString("wicket_type"))
//                );
//            }
//        } catch(SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored){}
//        }
        try{
            jdbcTemplate.query(ReaderUtil.readSqlFromFile("ballevents", "fetchAllEventsByMatchAndTeamId"), rs -> {
                ballEvents.add(new BallEvent(rs.getString("event_id"), rs.getString("match_id"), rs.getString("batting_team"),
                        rs.getInt("batsman") + batsmanOffset, rs.getInt("ball_number"),
                        rs.getInt("bowler") + bowlerOffset, rs.getInt("score"), rs.getString("unfair_ball_type"), rs.getString("wicket_type"))
                );
            }, matchId, teamId);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
        return ballEvents;
    }

    private void getBowlingStats(String matchId, MatchResult matchResult, String bowlTeamId) throws DataAccessException, SQLException {
//        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getBowlingStats"));
//        ps.setString(1, matchId);
//        ResultSet rs = ps.executeQuery();
//        while(rs.next()){
//            matchResult.appendBowlingStats(
//                    rs.getString("name") + ": " +
//                            Common.WICKETS + ":" + rs.getInt("Wicket") +
//                            Common.SINGLE_SPACE + Common.OVERS + ":" + rs.getInt("Balls Thrown")/Common.BALLS_IN_ONE_OVER + "." +
//                            rs.getInt("Balls Thrown")%Common.BALLS_IN_ONE_OVER + Common.SINGLE_SPACE + Common.EXTRAS + ": " + rs.getInt("unfairBallType")
//            );
//        }
        jdbcTemplate.query(ReaderUtil.readSqlFromFile("ballevents", "getBowlingStats"), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                matchResult.appendBowlingStats(
                    rs.getString("name") + ": " +
                            Common.WICKETS + ":" + rs.getInt("Wicket") +
                            Common.SINGLE_SPACE + Common.OVERS + ":" + rs.getInt("Balls Thrown")/Common.BALLS_IN_ONE_OVER + "." +
                            rs.getInt("Balls Thrown")%Common.BALLS_IN_ONE_OVER + Common.SINGLE_SPACE + Common.EXTRAS + ": " + rs.getInt("unfairBallType")
            );
            }
        }, matchId, bowlTeamId, bowlTeamId);
    }

    private void getBatsmanStats(String matchId, MatchResult matchResult) throws DataAccessException, SQLException{
//        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getBatsmanStats"));
//        ps.setString(1, matchId);
//        ResultSet rs = ps.executeQuery();
//        while(rs.next()){
//            matchResult.appendBattingStats(
//                    rs.getString("name") + ": " +
//                            rs.getInt("Score") + ", " + Common.BALLS + ": " + rs.getInt("Balls Played")
//            );
//        }
        jdbcTemplate.query(ReaderUtil.readSqlFromFile("ballevents", "getBatsmanStats"), rs -> {
            matchResult.appendBattingStats(
                rs.getString("name") + ": " +
                        rs.getInt("Score") + ", " + Common.BALLS + ": " + rs.getInt("Balls Played")
            );
        }, matchId);
    }

    private void getTeamScore(String matchId, MatchResult matchResult) throws DataAccessException, SQLException{
//        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "getTeamScore"));
//        ps.setString(1, matchId);
//        ResultSet rs = ps.executeQuery();
//        List<String> teamName = new ArrayList<>();
//        List<Integer> teamScore = new ArrayList<>();
//        while(rs.next()){
//            matchResult.appendTeamScores(
//                    rs.getString("name") + ": " +
//                            rs.getInt("Total Score") + "/" + rs.getInt("Total Wickets") + Common.SINGLE_SPACE +
//                            Common.OVERS + ": " + rs.getInt("Total Balls")/Common.BALLS_IN_ONE_OVER + "." +
//                            rs.getInt("Total Balls")%Common.BALLS_IN_ONE_OVER + Common.SINGLE_SPACE + Common.EXTRAS + ": " + rs.getInt("unfairBallType")
//            );
//            teamName.add(rs.getString("name"));
//            teamScore.add(rs.getInt("Total Score"));
//        }
        List<String> teamName = new ArrayList<>();
        List<Integer> teamScore = new ArrayList<>();
        jdbcTemplate.query(ReaderUtil.readSqlFromFile("ballevents", "getTeamScore"), rs -> {
            matchResult.appendTeamScores(
                    rs.getString("name") + ": " +
                            rs.getInt("Total Score") + "/" + rs.getInt("Total Wickets") + Common.SINGLE_SPACE +
                            Common.OVERS + ": " + rs.getInt("Total Balls")/Common.BALLS_IN_ONE_OVER + "." +
                            rs.getInt("Total Balls")%Common.BALLS_IN_ONE_OVER + Common.SINGLE_SPACE + Common.EXTRAS + ": " + rs.getInt("unfairBallType")
            );
            teamName.add(rs.getString("name"));
            teamScore.add(rs.getInt("Total Score"));
        } , matchId);
        matchResult.setWinner(MatchUtil.decideWinner(teamName, teamScore));
    }

}
