package com.tekion.cricket.repository;

import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.models.BatsmanStats;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class PlayerRepository {

    public List<Player> fetchBowlersForBowlingTeamByTeamId(Integer teamId){
        Connection con = null;
        List<Player> bowlers = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchBowlersForBowlingTeamByTeamId"));
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            String name;
            int playerId;
            String playerType;
            String bowlingPace;
            bowlers = new ArrayList<>();
            while(rs.next()){
                playerType = rs.getString("playertype");
                name = rs.getString("name");
                playerId = rs.getInt("player_id");
                bowlingPace = rs.getString("bowling_pace");

                Player newPlayer =  new Player(name, playerType, bowlingPace, playerId);
                bowlers.add(newPlayer);
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

    public List<BatsmanStats> fetchOnFieldBatsmenData(int strike, int nonStrike, int matchId) {
        Connection con = null;
        List<BatsmanStats> currentPlayers = new ArrayList<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchOnFieldBatsmenData"));
            ps.setInt(1, strike);
            ps.setInt(2, nonStrike);
            ps.setInt(3, strike);
            ps.setInt(4, nonStrike);
            ps.setInt(5, matchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                currentPlayers.add(new BatsmanStats(
                        rs.getString("name"), rs.getInt("player_id"), rs.getInt("Score"), rs.getInt("Balls"))
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

    public int fetchNextBatsman(Integer teamId, int maxOrder) {
        Connection con = null;
        int newPlayer = -1;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchNextBatsman"));
            ps.setInt(1, teamId);
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

    public PlayerType fetchPlayerType(int playerId) {
        Connection con = null;
        PlayerType playerType = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerType"));
            ps.setInt(1, playerId);
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

    public String fetchPlayerNameByPlayerId(int bowlerId) {
        Connection con = null;
        String name = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerNameByPlayerId"));
            ps.setInt(1, bowlerId);
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

    public Map<Integer, String> fetchPlayerNamesByTeamId(int teamId) {
        Connection con = null;
        Map<Integer, String> playerNamesFromIds = new HashMap<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerNamesByTeamId"));
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                playerNamesFromIds.put(rs.getInt(2), rs.getString(1));
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
}
