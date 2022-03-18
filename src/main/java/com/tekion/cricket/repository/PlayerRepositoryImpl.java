package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Player;
import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.models.BatsmanStats;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository{

    @Override
    public List<Player> fetchBowlersForBowlingTeamByTeamId(String teamId){
        Connection con = null;
        List<Player> bowlers = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchBowlersForBowlingTeamByTeamId"));
            ps.setString(1, teamId);
            ResultSet rs = ps.executeQuery();

            bowlers = new ArrayList<>();
            while(rs.next()){
                bowlers.add(new Player(
                        rs.getString("name"), rs.getString("player_type"), rs.getString("bowling_pace"), rs.getInt("player_order"))
                );
            }
            con.close();
        } catch(SQLException sqle){
            try {
                con.close();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return bowlers;
    }

    @Override
    public List<BatsmanStats> fetchOnFieldBatsmenData(int strike, int nonStrike, String matchId, String battingTeam) {
        Connection con = null;
        List<BatsmanStats> currentPlayers = new ArrayList<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchOnFieldBatsmenData"));
            ps.setInt(1, strike);
            ps.setInt(2, nonStrike);
            ps.setString(3, battingTeam);
            ps.setInt(4, strike);
            ps.setInt(5, nonStrike);
            ps.setString(6, matchId);
            ps.setString(7, battingTeam);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                currentPlayers.add(new BatsmanStats(
                        rs.getString("name"), rs.getInt("player_order"), rs.getInt("Score"), rs.getInt("Balls"))
                );
            }
            if(currentPlayers.size() > 1 && currentPlayers.get(0).getPlayerId() != strike){
                Collections.swap(currentPlayers, 0,1);
            }
        } catch(SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ignored) {}
        }

        return currentPlayers;
    }

    @Override
    public int fetchNextBatsman(String teamId, int maxOrder) {
        Connection con = null;
        int newPlayer = -1;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchNextBatsman"));
            ps.setString(1, teamId);
            ps.setInt(2, maxOrder);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                newPlayer = rs.getInt(1);
            }
        } catch(SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ignored) {}
        }
        return newPlayer;
    }

    @Override
    public PlayerType fetchPlayerType(int playerOrder, String team) {
        Connection con = null;
        PlayerType playerType = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerType"));
            ps.setInt(1, playerOrder);
            ps.setString(2, team);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                playerType = PlayerType.fromStringToEnum(rs.getString(1));
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored) {}
        }
        return playerType;
    }

    @Override
    public String fetchPlayerNameByPlayerId(int playerOrder, String teamId) {
        Connection con = null;
        String name = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerNameByPlayerId"));
            ps.setInt(1, playerOrder);
            ps.setString(2, teamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                name = rs.getString(1);
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored) {}
        }

        return name;
    }

    @Override
    public Map<Integer, String> fetchPlayerNamesByTeamId(String teamId, int offset) {
        Connection con = null;
        Map<Integer, String> playerNamesFromIds = new HashMap<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerNamesByTeamId"));
            ps.setString(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                playerNamesFromIds.put(rs.getInt(2) + offset, rs.getString(1));
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored) {}
        }

        return playerNamesFromIds;
    }

    @Override
    public void saveBatch(List<Player> players, String teamId) {
        Connection con = null;
        try {
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "insertTeamPlayers"), Statement.RETURN_GENERATED_KEYS);
            int order = 0;
            for (Player player : players) {
                stmt.setString(1, teamId);
                stmt.setInt(2, order++);
                stmt.setString(3, player.getName());
                stmt.setString(4, player.getPlayerType());
                stmt.setString(5, player.getTypeOfBowler());
                stmt.addBatch();
            }
            stmt.executeBatch();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            try {
                con.rollback();
                con.close();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
