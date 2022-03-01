package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.beans.Player;
import com.tekion.intern.beans.Team;
import com.tekion.intern.util.ReaderUtil;

import java.sql.*;
import java.util.List;

public class TeamRepository {
    public static void insertTeamData(Team team) throws SQLException, ClassNotFoundException {
        String teamName = team.getTeamName();
        Connection con = MySqlConnector.getConnection();
        con.setAutoCommit(false);
        PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "insertTeamData"), Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, teamName);
        try {
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            int teamId = 0;
            if (rs.next())
                teamId = rs.getInt(1);

            insertTeamPlayers(team.getPlayers(), teamId, con);
            con.commit();
        } catch (SQLException sqle) {
            con.rollback();
            throw sqle;
        }
    }

    public static String getTeamNameFromTeamId(int teamId) throws SQLException, ClassNotFoundException {
        Connection con = MySqlConnector.getConnection();
        PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "getTeamNameFromTeamId"));
        ps.setInt(1, teamId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            return rs.getString(1);
        return "";
    }

    public static Team createTeamFromTeamID(int teamId) throws SQLException, ClassNotFoundException {
        String teamName = getTeamNameFromTeamId(teamId);
        Team team = new Team(teamName, teamId);
        return team;
    }

    private static void insertTeamPlayers(List<Player> players, int teamId, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(ReaderUtil.readSqlFromFile("team","insertTeamPlayers"), Statement.RETURN_GENERATED_KEYS);
        for (Player player : players) {
            stmt.setInt(1, teamId);
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getPlayerType().toString());
            stmt.setString(4, player.getTypeOfBowler());
            stmt.addBatch();
        }
        stmt.executeBatch();
    }
}