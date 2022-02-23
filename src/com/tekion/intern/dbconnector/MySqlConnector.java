package com.tekion.intern.dbconnector;

import com.tekion.intern.game.Match;
import com.tekion.intern.game.MatchUtil;
import com.tekion.intern.game.Player;
import com.tekion.intern.game.Team;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MySqlConnector {
    private static Connection conn = null;
    private static void initializeConnection() throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cricket","root","root");
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if(conn == null || conn.isClosed()) {
            initializeConnection();
        }
        return conn;
    }

    public static void insertTeamData(Team team) throws SQLException, ClassNotFoundException{
        String teamName = team.getTeamName();
        Connection con = getConnection();
        con.setAutoCommit(false);
        PreparedStatement stmt = con.prepareStatement("insert into team(name) values(?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,teamName);
        try {
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            int teamId = 0;
            if (rs.next())
                teamId = rs.getInt(1);

            insertTeamPlayers(team.getPlayers(), teamId);
            con.commit();
        }
        catch (SQLException sqle){
            con.rollback();
            throw sqle;
        }
    }

    private static void insertTeamPlayers(List<Player> players, int teamId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into player(team, name, playertype, bowling_pace) values (?,?,?,?)");
        for(Player player:players){
            stmt.setInt(1,teamId);
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getPlayerType().toString());
            stmt.setString(4,player.getTypeOfBowler());
            stmt.addBatch();
        }
        stmt.executeBatch();
    }

    public static int initializeMatch(int numOfOvers) throws SQLException, ClassNotFoundException{
        List<Integer> selectedTeams = selectTeams();
        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("insert into MatchTable(team1, team2, overs, maxovers) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, selectedTeams.get(0));
            ps.setInt(2, selectedTeams.get(1));
            ps.setInt(3, numOfOvers);
            ps.setInt(4, (int) Math.ceil(numOfOvers / 5.0));
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();

            int matchId = 0;
            while (rs.next())
                matchId = rs.getInt(1);
            con.commit();
            System.out.println("Match Creation Successful");
            return matchId;
        } catch(SQLException sqle){
            conn.rollback();
            throw sqle;
        }
    }

    private static List<Integer> selectTeams() throws SQLException, ClassNotFoundException{
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from team");
        List<Integer> teamIds = new LinkedList<>();
        int cnt = 0;
        while(rs.next()){
            teamIds.add(rs.getInt("team_id"));
            System.out.println((++cnt) + ".." + rs.getString("name"));
        }
        int team1, team2;
        do{
            team1 = MatchUtil.getIntegerInputInRange(1,cnt);
            team2 = MatchUtil.getIntegerInputInRange(1,cnt);
            if(team1 == team2)
                System.out.println("Both Teams should be different");
        }while(team1 == team2);

        return Arrays.asList(teamIds.get(team1-1), teamIds.get(team2-1));
    }
}
