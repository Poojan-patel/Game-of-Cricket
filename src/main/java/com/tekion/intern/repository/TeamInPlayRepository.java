package com.tekion.intern.repository;

import com.tekion.intern.beans.Strike;
import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class TeamInPlayRepository {

    public void updateBowlerByTeamAndMatchId(int bowlerId, int matchId, int teamId){
        Connection con = null;
        try {
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("teaminplay", "updateBowlerByTeamAndMatchId"));
            ps.setInt(1, bowlerId);
            ps.setInt(2, teamId);
            ps.setInt(3, matchId);
            ps.execute();
            con.commit();
            con.close();
        }
        catch(SQLException sqle){
            try {
                con.rollback();
                con.close();
            } catch (Exception ignored){}
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void updateStrikesByTeamAndMatchId(Strike strike){

        Connection con = null;
        try {
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("teaminplay", "updateStrikesByTeamAndMatchId"));
            if(strike.getStrike() != -1) {
                ps.setInt(1, strike.getStrike());
            }
            else {
                ps.setNull(1, Types.INTEGER);
            }

            if(strike.getNonStrike() != -1) {
                ps.setInt(2, strike.getNonStrike());
            }
            else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, strike.getCurrentWickets());
            ps.setInt(4, strike.getMatchId());
            ps.setInt(5, strike.getTeamId());
            ps.executeUpdate();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            try {
                con.rollback();
                con.close();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void insertStrike(int currentStrike, int currentNonStrike, int matchId, int teamId){
        Connection con = null;
        try {
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("teaminplay", "insertStrike"));
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, currentStrike);
            ps.setInt(4, currentNonStrike);

            ps.execute();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            try {
                con.rollback();
                con.close();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public int fetchTheLastOver(Integer matchId, Integer currentBowlTeamId) {
        Connection con = null;
        int bowlerId = 0;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("teaminplay", "findLastBowlerByTeamAndMatchId"));
            ps.setInt(1, matchId);
            ps.setInt(2, currentBowlTeamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                bowlerId = rs.getInt("bowler");
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored) {}
        }
        return bowlerId;
    }

    public Strike fetchStrikeDetails(int matchId, int currentBatTeamId) {
        Connection con = null;
        Strike strike = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("teaminplay", "fetchStrikeDetails"));
            ps.setInt(1, matchId);
            ps.setInt(2, currentBatTeamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                strike = new Strike(
                        rs.getInt("strike"), rs.getInt("nonstrike"), rs.getInt("bowler"),
                        matchId, rs.getInt("team"), rs.getInt("currentwickets")
                );
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored) {}
        }
        return strike;
    }
}
