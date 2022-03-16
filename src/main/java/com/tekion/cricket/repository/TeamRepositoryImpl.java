package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Player;
import com.tekion.cricket.beans.Team;
import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.models.TeamDTO;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TeamRepositoryImpl implements TeamRepository{
    @Override
    public String getTeamNameByTeamId(int teamId) {
        Connection con = null;
        String teamName = "";
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "getTeamNameFromTeamId"));
            ps.setInt(1, teamId);
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
    public Integer save(Team team){
        Connection con = null;
        try{
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "insertTeamData"), Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, team.getTeamName());
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            int teamId = 0;
            if (rs.next()) {
                teamId = rs.getInt(1);
            }
            con.commit();
            return teamId;
        } catch (SQLException sqle) {
            try{
                con.rollback();
            } catch (Exception ignored) {}
        } catch (Exception ignored){
            ignored.printStackTrace();
        }

        return 0;
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
                teams.add(new TeamDTO(rs.getString(2), rs.getInt(1)));
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
    public BattingTeam fetchTeamScoreFromMatchId(int matchId, int battingTeamId) {
        Connection con = null;
        BattingTeam team = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "fetchTeamScoreFromMatchId"));
            ps.setInt(1, matchId);
            ps.setInt(2, battingTeamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                team = new BattingTeam(rs.getString("name"), rs.getInt("Score"), rs.getInt("Balls"), battingTeamId);
            }
            if(team == null){
                ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "getTeamNameFromTeamId"));
                ps.setInt(1, battingTeamId);
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
    public List<Integer> fetchFirstTwoPlayers(int team1Id) {
        Connection con = null;
        List<Integer> players = new ArrayList<>();
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("team", "fetchFirstTwoPlayers"));
            ps.setInt(1, team1Id);
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