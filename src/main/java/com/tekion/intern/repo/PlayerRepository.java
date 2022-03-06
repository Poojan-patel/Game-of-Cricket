package com.tekion.intern.repo;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.beans.Player;
import com.tekion.intern.beans.Team;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class PlayerRepository {

//    public static Player getPlayerFromOffsetByTeamId(int teamId, int playerOffset) throws SQLException, ClassNotFoundException{
//        Connection con = MySqlConnector.getConnection();
//        try{
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "getPlayerFromOffsetByTeamId"));
//            ps.setInt(1, teamId);
//            ps.setInt(2,playerOffset);
//            ps.setInt(3,1);
//            ResultSet rs = ps.executeQuery();
//            String name = null;
//            int playerId = 0;
//            String playerType = null;
//            while(rs.next()){
//                name = rs.getString(2);
//                playerId = rs.getInt(1);
//                playerType = rs.getString(3);
//                if(!playerType.equals("BATSMAN")){
//                    playerType += "," + rs.getString(4);
//                }
//            }
//            Player newPlayer =  new Player(name, playerType, playerId);
//            con.close();
//            return newPlayer;
//        } catch (SQLException sqle){
//            con.close();
//            throw sqle;
//        }
//    }

    public List<Player> fetchBowlersForBowlingTeamByTeamId(Integer teamId){
        Connection con = null;
        List<Player> bowlers = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchBowlersForBowlingTeamByTeamId"));
            ps.setInt(1,teamId);
            ResultSet rs = ps.executeQuery();

            String name = null;
            int playerId = 0;
            String playerType = null;
            String bowlingPace = null;
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

    public List<Player> fetchOnFieldBatsmenData(int strike, int nonStrike, int matchId, int currentBatTeamId) {
        Connection con = null;
        List<Player> currentPlayers = new ArrayList<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "select * from (select * from Player where player_id in (?,?)) Player left outer join (select count(distinct ballnumber) Balls, sum(score) Score, batsman from BallEvents where batsman in (?,?) and match_id = ? group by batsman) as ScoreCard on Player.player_id = ScoreCard.batsman"
            );
            ps.setInt(1, strike);
            ps.setInt(2, nonStrike);
            ps.setInt(3, strike);
            ps.setInt(4, nonStrike);
            ps.setInt(5, matchId);
//            System.out.println(ps);
            ResultSet rs = ps.executeQuery();
            //System.out.println(rs.getFetchSize());
            while (rs.next()){
                currentPlayers.add(new Player(
                        rs.getString("name"), rs.getString("playertype"), rs.getString("bowling_pace"),
                        rs.getInt("player_id"), rs.getInt("Balls"), rs.getInt("Score"))
                );
            }
            con.close();
            if(currentPlayers.get(0).getPlayerId() != strike){
                Collections.swap(currentPlayers, 0,1);
            }
            System.out.println(currentPlayers.get(0).getPlayerId() == strike);
        } catch(SQLException sqle){
            try {
                con.close();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return currentPlayers;
    }

    public Player fetchNextBatsman(Integer teamId, int maxOrder) {
        Connection con = null;
        Player newPlayer = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from Player where team = ? and player_id > ? limit 1 offset 0");
            ps.setInt(1, teamId);
            ps.setInt(2, maxOrder);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                newPlayer = new Player(
                        rs.getString("name"), rs.getString("playertype"),
                        rs.getString("bowling_pace"), rs.getInt("player_id"), 0, 0);
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
        return newPlayer;
    }
}
