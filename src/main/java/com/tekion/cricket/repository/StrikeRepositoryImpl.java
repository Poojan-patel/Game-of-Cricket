package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Strike;
import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.plaf.basic.BasicDesktopIconUI;
import java.sql.*;

@Repository
public class StrikeRepositoryImpl implements StrikeRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateBowlerByTeamAndMatchId(int bowlerOrder, String matchId, String bowlingTeam){
        try{
            jdbcTemplate.update(ReaderUtil.readSqlFromFile("strike", "updateBowlerByTeamAndMatchId"), bowlerOrder, bowlingTeam, matchId);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        Connection con = null;
//        try {
//            con = MySqlConnector.getConnection();
//            con.setAutoCommit(false);
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("strike", "updateBowlerByTeamAndMatchId"));
//            ps.setInt(1, bowlerOrder);
//            ps.setString(2, bowlingTeam);
//            ps.setString(3, matchId);
//            ps.execute();
//            con.commit();
//            con.close();
//        }
//        catch(SQLException sqle){
//            try {
//                con.rollback();
//                con.close();
//            } catch (Exception ignored){}
//            sqle.printStackTrace();
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        }
    }

    @Override
    public void updateStrikesByTeamAndMatchId(Strike strike){
        try{
            jdbcTemplate.update(ReaderUtil.readSqlFromFile("strike", "updateStrikesByTeamAndMatchId"),
                    (strike.getStrike() != -1) ?strike.getStrike() :null,  (strike.getNonStrike() != -1) ?strike.getNonStrike() :null,
                    strike.getCurrentWickets(), strike.getMatchId(), strike.getBattingTeam()
            );
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        Connection con = null;
//        try {
//            con = MySqlConnector.getConnection();
//            con.setAutoCommit(false);
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("strike", "updateStrikesByTeamAndMatchId"));
//            if(strike.getStrike() != -1) {
//                ps.setInt(1, strike.getStrike());
//            }
//            else {
//                ps.setNull(1, Types.INTEGER);
//            }
//
//            if(strike.getNonStrike() != -1) {
//                ps.setInt(2, strike.getNonStrike());
//            }
//            else {
//                ps.setNull(2, Types.INTEGER);
//            }
//            ps.setInt(3, strike.getCurrentWickets());
//            ps.setString(4, strike.getMatchId());
//            ps.setString(5, strike.getBattingTeam());
//            ps.executeUpdate();
//            con.commit();
//            con.close();
//        } catch (SQLException sqle){
//            try {
//                con.rollback();
//                con.close();
//            } catch (Exception ignored) {}
//            sqle.printStackTrace();
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        }
    }

    @Override
    public void save(Strike strike){
        try{
            jdbcTemplate.update(ReaderUtil.readSqlFromFile("strike", "insertStrike"),
                    strike.getMatchId(), strike.getBattingTeam(), strike.getBowlingTeam(), strike.getStrike(), strike.getNonStrike()
            );
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        Connection con = null;
//        try {
//            con = MySqlConnector.getConnection();
//            con.setAutoCommit(false);
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("strike", "insertStrike"));
//            ps.setString(1, strike.getMatchId());
//            ps.setString(2, strike.getBattingTeam());
//            ps.setString(3, strike.getBowlingTeam());
//            ps.setInt(4, strike.getStrike());
//            ps.setInt(5, strike.getNonStrike());
//
//            ps.execute();
//            con.commit();
//            con.close();
//        } catch (SQLException sqle){
//            try {
//                con.rollback();
//                con.close();
//            } catch (Exception ignored) {}
//            sqle.printStackTrace();
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        }
    }

    @Override
    public int fetchTheLastOver(String matchId, String currentBowlTeamId) {
        //Connection con = null;
        int bowlerOrder = 0;
        try{
            bowlerOrder = jdbcTemplate.queryForObject(ReaderUtil.readSqlFromFile("strike", "findLastBowlerByTeamAndMatchId"), Integer.class, matchId, currentBowlTeamId);
        } catch (DataAccessException | NullPointerException e){
            e.printStackTrace();
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("strike", "findLastBowlerByTeamAndMatchId"));
//            ps.setString(1, matchId);
//            ps.setString(2, currentBowlTeamId);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next())
//                bowlerOrder = rs.getInt("bowler");
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored) {}
//        }
        return bowlerOrder;
    }

    @Override
    public Strike fetchStrikeDetails(String matchId, String currentBatTeamId) {
        //Connection con = null;
        Strike strike = null;
        try{
            strike = jdbcTemplate.queryForObject(ReaderUtil.readSqlFromFile("strike", "fetchStrikeDetails"), BeanPropertyRowMapper.newInstance(Strike.class),
                    matchId, currentBatTeamId
            );
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("strike", "fetchStrikeDetails"));
//            ps.setString(1, matchId);
//            ps.setString(2, currentBatTeamId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()){
//                strike = new Strike(
//                        rs.getInt("strike"), rs.getInt("nonstrike"), rs.getInt("bowler"),
//                        matchId, rs.getString("batting_team"), rs.getString("bowling_team"), rs.getInt("current_wickets")
//                );
//            }
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored) {}
//        }
        return strike;
    }
}
