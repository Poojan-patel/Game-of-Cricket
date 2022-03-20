package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Player;
import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.models.BatsmanStats;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class PlayerRepositoryImpl implements PlayerRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Player> fetchBowlersForBowlingTeamByTeamId(String teamId){
//        Connection con = null;
        List<Player> bowlers = null;
        try{
            bowlers = jdbcTemplate.query(ReaderUtil.readSqlFromFile("player", "fetchBowlersForBowlingTeamByTeamId"), BeanPropertyRowMapper.newInstance(Player.class), teamId);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchBowlersForBowlingTeamByTeamId"));
//            ps.setString(1, teamId);
//            ResultSet rs = ps.executeQuery();
//
//            bowlers = new ArrayList<>();
//            while(rs.next()){
//                bowlers.add(new Player(
//                        rs.getString("name"), rs.getString("player_type"), rs.getString("bowling_pace"), rs.getInt("player_order"))
//                );
//            }
//            con.close();
//        } catch(SQLException sqle){
//            try {
//                con.close();
//            } catch (Exception ignored) {}
//            sqle.printStackTrace();
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        }

        return bowlers;
    }

    @Override
    public List<BatsmanStats> fetchOnFieldBatsmenData(int strike, int nonStrike, String matchId, String battingTeam) {
        //Connection con = null;
        List<BatsmanStats> currentPlayers = new ArrayList<>();
        try{
            jdbcTemplate.query(ReaderUtil.readSqlFromFile("player", "fetchOnFieldBatsmenData"), rs->{
                currentPlayers.add(new BatsmanStats(
                        rs.getString("name"), rs.getInt("player_order"), rs.getInt("Score"), rs.getInt("Balls"))
                );
            } , strike, nonStrike, battingTeam, strike, nonStrike, matchId, battingTeam);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
        if(currentPlayers.size() > 1 && currentPlayers.get(0).getPlayerId() != strike){
            Collections.swap(currentPlayers, 0,1);
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchOnFieldBatsmenData"));
//            ps.setInt(1, strike);
//            ps.setInt(2, nonStrike);
//            ps.setString(3, battingTeam);
//            ps.setInt(4, strike);
//            ps.setInt(5, nonStrike);
//            ps.setString(6, matchId);
//            ps.setString(7, battingTeam);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                currentPlayers.add(new BatsmanStats(
//                        rs.getString("name"), rs.getInt("player_order"), rs.getInt("Score"), rs.getInt("Balls"))
//                );
//            }
//            if(currentPlayers.size() > 1 && currentPlayers.get(0).getPlayerId() != strike){
//                Collections.swap(currentPlayers, 0,1);
//            }
//        } catch(SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        } finally {
//            try {
//                con.close();
//            } catch (Exception ignored) {}
//        }

        return currentPlayers;
    }

    @Override
    public int fetchNextBatsman(String teamId, int maxOrder) {
        Connection con = null;
        int newPlayer = -1;
        try{
            newPlayer = jdbcTemplate.queryForObject(ReaderUtil.readSqlFromFile("player", "fetchNextBatsman"), Integer.class, teamId, maxOrder);
        } catch (DataAccessException | NullPointerException e){
            e.printStackTrace();
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchNextBatsman"));
//            ps.setString(1, teamId);
//            ps.setInt(2, maxOrder);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next()){
//                newPlayer = rs.getInt(1);
//            }
//        } catch(SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                con.close();
//            } catch (Exception ignored) {}
//        }
        return newPlayer;
    }

    @Override
    public PlayerType fetchPlayerType(int playerOrder, String team) {
        //Connection con = null;
        PlayerType playerType = null;
        try{
            String type = jdbcTemplate.queryForObject(ReaderUtil.readSqlFromFile("player", "fetchPlayerType"), String.class, playerOrder, team);
            playerType = PlayerType.fromStringToEnum(type);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerType"));
//            ps.setInt(1, playerOrder);
//            ps.setString(2, team);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next())
//                playerType = PlayerType.fromStringToEnum(rs.getString(1));
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored) {}
//        }
        return playerType;
    }

    @Override
    public String fetchPlayerNameByPlayerId(int playerOrder, String teamId) {
        //Connection con = null;
        String name = null;
        try {
            name = jdbcTemplate.queryForObject(ReaderUtil.readSqlFromFile("player", "fetchPlayerNameByPlayerId"), String.class, playerOrder, teamId);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerNameByPlayerId"));
//            ps.setInt(1, playerOrder);
//            ps.setString(2, teamId);
//            ResultSet rs = ps.executeQuery();
//            while(rs.next())
//                name = rs.getString(1);
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored) {}
//        }

        return name;
    }

    @Override
    public Map<Integer, String> fetchPlayerNamesByTeamId(String teamId, int offset) {
        //Connection con = null;
        Map<Integer, String> playerNamesFromIds = new HashMap<>();
        try {
            jdbcTemplate.query(ReaderUtil.readSqlFromFile("player", "fetchPlayerNamesByTeamId"), rs -> {
                playerNamesFromIds.put(rs.getInt(2) + offset, rs.getString(1));
            }, teamId);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        try{
//            con = MySqlConnector.getConnection();
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchPlayerNamesByTeamId"));
//            ps.setString(1, teamId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()){
//                playerNamesFromIds.put(rs.getInt(2) + offset, rs.getString(1));
//            }
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored) {}
//        }

        return playerNamesFromIds;
    }

    @Override
    public void saveBatch(List<Player> players, String teamId) {
        try{
            jdbcTemplate.batchUpdate(ReaderUtil.readSqlFromFile("player", "insertTeamPlayers"), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement stmt, int i) throws SQLException {
                    stmt.setString(1, teamId);
                    stmt.setInt(2, i);
                    stmt.setString(3, players.get(i).getName());
                    stmt.setString(4, players.get(i).getPlayerType());
                    stmt.setString(5, players.get(i).getBowlingPace());
                }

                @Override
                public int getBatchSize() {
                    return players.size();
                }
            });
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        Connection con = null;
//        try {
//            con = MySqlConnector.getConnection();
//            con.setAutoCommit(false);
//            PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "insertTeamPlayers"), Statement.RETURN_GENERATED_KEYS);
//            int order = 0;
//            for (Player player : players) {
//                stmt.setString(1, teamId);
//                stmt.setInt(2, order++);
//                stmt.setString(3, player.getName());
//                stmt.setString(4, player.getPlayerType());
//                stmt.setString(5, player.getBowlingPace());
//                stmt.addBatch();
//            }
//            stmt.executeBatch();
//            con.commit();
//            con.close();
//        } catch (SQLException sqle){
//            try {
//                con.rollback();
//                con.close();
//            } catch (Exception ignored) {}
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
