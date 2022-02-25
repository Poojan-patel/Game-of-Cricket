package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;

import java.sql.*;

public class TeamInPlayRepository {
    public static void insertStrikeValues(int matchId, int teamId, int strike, int nonStrike) throws SQLException, ClassNotFoundException {
        Connection con = MySqlConnector.getConnection();
        con.setAutoCommit(false);
        try {
            PreparedStatement ps = con.prepareStatement("insert into team_in_play(match_id, team, strike, nonstrike) values(?,?,?,?)");
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, strike);
            ps.setInt(4, nonStrike);
            ps.execute();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            con.rollback();
            con.close();
            throw sqle;
        }
    }

    public static void updateBowler(int bowlerId, int matchId, int teamId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update team_in_play set bowler = ? where team = ? and match_id = ?");
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

    public static void updateStrikes(int currentStrike, int currentNonStrike, int matchId, int teamId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();

        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update team_in_play set strike = ?, nonstrike = ? where match_id = ? and team = ?");
            if(currentStrike != -1) ps.setInt(1,currentStrike);
            else ps.setNull(1, Types.INTEGER);

            if(currentNonStrike != -1) ps.setInt(2,currentNonStrike);
            else ps.setNull(2,Types.INTEGER);
            ps.setInt(3,matchId);
            ps.setInt(4,teamId);
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
