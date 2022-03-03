package com.tekion.intern.repo;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.beans.Player;
import com.tekion.intern.beans.Team;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        } catch (Exception ignored) {}

        return bowlers;
    }
}
