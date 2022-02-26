package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;

import java.sql.*;

public class TeamInPlayRepository {

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

    public static void updateStrikes(int currentStrike, int currentNonStrike, int matchId, int teamId, int curWickets) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update team_in_play set strike = ?, nonstrike = ?, currentwickets = ? where match_id = ? and team = ?");
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

    public static void insertStrikeData(int currentStrike, int currentNonStrike, int matchId, int teamId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("insert into team_in_play(match_id, team, strike, nonstrike) values(?,?,?,?)");
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, currentStrike);
            ps.setInt(4, currentNonStrike);

            ps.execute();
            con.commit();
            con.close();
            System.out.println("Here");
        } catch (SQLException sqle){
            con.rollback();
            con.close();
            throw sqle;
        }
    }
}
