package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.util.ReaderUtil;

import java.sql.*;

public class TeamInPlayRepository {

    public static void updateBowlerByTeamAndMatchId(int bowlerId, int matchId, int teamId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try {
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
            con.rollback();
            con.close();
            throw sqle;
        }
    }

    public static void updateStrikesByTeamAndMatchId(int currentStrike, int currentNonStrike, int matchId, int teamId, int curWickets) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("teaminplay", "updateStrikesByTeamAndMatchId"));
            if(currentStrike != -1) ps.setInt(1,currentStrike);
            else ps.setNull(1, Types.INTEGER);

            if(currentNonStrike != -1) ps.setInt(2,currentNonStrike);
            else ps.setNull(2,Types.INTEGER);
            ps.setInt(3,curWickets);
            ps.setInt(4,matchId);
            ps.setInt(5,teamId);

            ps.executeUpdate();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            con.rollback();
            con.close();
            throw sqle;
        }
    }

    public static void insertStrike(int currentStrike, int currentNonStrike, int matchId, int teamId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try {
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
            con.rollback();
            con.close();
            throw sqle;
        }
    }
}
