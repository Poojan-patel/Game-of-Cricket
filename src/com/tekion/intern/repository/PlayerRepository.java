package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.game.Player;
import com.tekion.intern.game.Team;
import com.tekion.intern.util.ReaderUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {

    public static Player getPlayerFromOffsetByTeamId(int teamId, int playerOffset) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try{
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "getPlayerFromOffsetByTeamId"));
            ps.setInt(1, teamId);
            ps.setInt(2,playerOffset);
            ps.setInt(3,1);
            ResultSet rs = ps.executeQuery();
            String name = null;
            int playerId = 0;
            String playerType = null;
            while(rs.next()){
                name = rs.getString(2);
                playerId = rs.getInt(1);
                playerType = rs.getString(3);
                if(!playerType.equals("BATSMAN")){
                    playerType += "," + rs.getString(4);
                }
            }
            Player newPlayer =  new Player(name, playerType, playerId);
            con.close();
            return newPlayer;
        } catch (SQLException sqle){
            con.close();
            throw sqle;
        }
    }

    public static void fetchBowlersForBowlingTeamByTeamId(Team team) throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        try{
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("player", "fetchBowlersForBowlingTeamByTeamId"));
            ps.setInt(1,team.getTeamId());
            ResultSet rs = ps.executeQuery();
            String name = null;
            int playerId = 0;
            String playerType = null;
            List<Player> bowlers = new ArrayList<>();
            while(rs.next()){
                playerType = rs.getString(3);
                if(playerType.equals("BATSMAN")){
                    bowlers.add(null);
                    continue;
                }
                name = rs.getString(2);
                playerId = rs.getInt(1);

                playerType += "," + rs.getString(4);
                Player newPlayer =  new Player(name, playerType, playerId);
                bowlers.add(newPlayer);
            }
            con.close();
            team.setPlayers(bowlers);
        }
        catch(SQLException sqle){
            con.close();
            throw sqle;
        }
    }
}
