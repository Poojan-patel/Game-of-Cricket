package com.tekion.intern.repo;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.beans.Player;
import com.tekion.intern.beans.Team;
import com.tekion.intern.models.TeamDTO;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TeamRepository {
    public Integer save(Team team) throws SQLException, ClassNotFoundException {
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
            return teamId;
        } catch (SQLException sqle) {
            con.rollback();
            throw sqle;
        }
    }

    private void insertTeamPlayers(List<Player> players, int teamId, Connection conn) throws SQLException {
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

    public List<TeamDTO> findAll() throws SQLException, ClassNotFoundException{
        Connection con = MySqlConnector.getConnection();
        List<TeamDTO> teams = new ArrayList<>();
        try{
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select team_id, name from team");
            while(rs.next()){
                teams.add(new TeamDTO(rs.getString(2), rs.getInt(1)));
            }
            con.close();
        } catch (SQLException sql){
            con.close();
        }
        return teams;
    }


    public Team fetchTeamScoreFromMatchId(int matchId, int battingTeamId) {
        Connection con = null;
        Team team = null;
        try{
            con = MySqlConnector.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from team inner join\n" +
                    "(select count(distinct ballnumber) Balls, sum(score) Score, team from BallEvents where match_id = ? and team = ?) as ScoreBoard\n" +
                    "on ScoreBoard.team = team.team_id");
            ps.setInt(1, matchId);
            ps.setInt(2, battingTeamId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                team = new Team(rs.getString("name"), rs.getInt("Score"), rs.getInt("Balls"), battingTeamId);
            }
        } catch (SQLException sqle){
            try{
                con.close();
            } catch (Exception ignored) {}
            sqle.printStackTrace();
        } catch (Exception ignored) {}
        return team;
    }
}