package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Team;
import com.tekion.cricket.constants.Common;
import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.TeamDTO;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TeamRepositoryImpl implements TeamRepository{
    @Override
    public String getTeamNameByTeamId(String teamId) {
        Connection con = null;
        String teamName = "";
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "getTeamNameFromTeamId"));
            ps.setString(1, teamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                teamName = rs.getString(1);
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
        return teamName;
    }

    @Override
    public String save(Team team){
        Connection con = null;
        try{
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "insertTeamData"), Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, team.getTeamId());
            stmt.setString(2, team.getTeamName());
            stmt.execute();
//            ResultSet rs = stmt.getGeneratedKeys();
//            String teamId = null;
//            //System.out.println("before loop");
//            if (rs.next()) {
//                //System.out.println("In loop");
//                teamId = rs.getString(1);
//            }
            con.commit();
            //System.out.println("After loop");
            return team.getTeamId();
        } catch (SQLException sqle) {
            try{
                con.rollback();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<TeamDTO> findAll(){
        Connection con = null;
        List<TeamDTO> teams = new ArrayList<>();
        try{
            con = MySqlConnector.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(ReaderUtil.readSqlFromFile("team", "getAll"));
            while(rs.next()){
                teams.add(new TeamDTO(rs.getString(2), rs.getString(1)));
            }
        } catch (SQLException sql){
            sql.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ignored) {}
        }
        return teams;
    }

    @Override
    public BattingTeam fetchTeamScoreFromMatchId(String matchId, String battingTeamId) {
        Connection con = null;
        BattingTeam team = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "fetchTeamScoreFromMatchId"));
            ps.setString(1, matchId);
            ps.setString(2, battingTeamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                team = new BattingTeam(rs.getString("name"), rs.getInt("Score"), rs.getInt("Balls"), battingTeamId);
            }
            if(team == null){
                ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "getTeamNameFromTeamId"));
                ps.setString(1, battingTeamId);
                rs = ps.executeQuery();
                while(rs.next()){
                    team = new BattingTeam(rs.getString(1), 0, 0, battingTeamId);
                }
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored) {}
        }
        return team;
    }

    @Override
    public List<Integer> fetchFirstTwoPlayers(String team1Id) {
        Connection con = null;
        List<Integer> players = new ArrayList<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "fetchFirstTwoPlayers"));
            ps.setString(1, team1Id);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                players.add(rs.getInt(1));

        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored) {}
        }

        return players;
    }
}