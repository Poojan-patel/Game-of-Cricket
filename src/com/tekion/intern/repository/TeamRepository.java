package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.game.Player;
import com.tekion.intern.game.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamRepository {
    public static void insertTeamData(Team team) throws SQLException, ClassNotFoundException{
        String teamName = team.getTeamName();
        Connection con = MySqlConnector.getConnection();
        con.setAutoCommit(false);
        PreparedStatement stmt = con.prepareStatement("insert into team(name) values(?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,teamName);
        try {
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            int teamId = 0;
            if (rs.next())
                teamId = rs.getInt(1);

            insertTeamPlayers(team.getPlayers(), teamId, con);
            con.commit();
        }
        catch (SQLException sqle){
            con.rollback();
            throw sqle;
        }
    }

    private static void insertTeamPlayers(List<Player> players, int teamId, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into player(team, name, playertype, bowling_pace) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        for(Player player:players){
            stmt.setInt(1,teamId);
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getPlayerType().toString());
            stmt.setString(4,player.getTypeOfBowler());
            stmt.addBatch();
        }
        stmt.executeBatch();
        ResultSet rs = stmt.getGeneratedKeys();

    }

    public static String getTeamNameFromTeamId(int teamId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        PreparedStatement ps = con.prepareStatement("select name from team where team_id = ?");
        ps.setInt(1,teamId);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
            return rs.getString(1);
        return "";
    }

    public static Team createTeamFromTeamID(int teamId, int matchId) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        String teamName = getTeamNameFromTeamId(teamId);
        try {
            PreparedStatement ps = con.prepareStatement("select name, playertype, bowling_pace, player_id from player where team = ?");
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            List<String> playerNames = new ArrayList<>();
            List<String> playerTypes = new ArrayList<>();
            List<Integer> playerIds = new ArrayList<>();
            String type;
            int strike = 0;
            int nonstrike = 1;
            int cnt = 0;
            while (rs.next()) {
                playerNames.add(rs.getString("name"));
                type = rs.getString("playertype");
                playerIds.add(rs.getInt("player_id"));
                if (type.equals("BATSMAN"))
                    playerTypes.add("BATSMAN");
                else
                    playerTypes.add(type + "," + rs.getString("bowling_pace"));
                if(cnt == 0)
                    strike = rs.getInt("player_id");
                else if(cnt == 1)
                    nonstrike = rs.getInt("player_id");
                cnt++;
            }
            Team team = new Team(teamName, playerNames, playerTypes, playerIds, teamId);
            TeamInPlayRepository.insertStrikeValues(matchId, teamId, strike, nonstrike);
            //con.close();
            return team;
        } catch(SQLException sqle){
            //con.close();
            throw sqle;
        } catch(Exception e){
            System.out.println("DB Error");
        }
        return null;
    }
}
