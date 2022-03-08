package com.tekion.intern.repository;

import com.tekion.intern.beans.Match;
import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class MatchRepository {

    public int save(Match match){
        Connection con = null;
        int matchId = 0;
        try {
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "createMatch"), Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, match.getTeam1Id());
            ps.setInt(2, match.getTeam2Id());
            ps.setInt(3, match.getOvers());
            ps.setInt(4, (int) Math.ceil(match.getOvers() / 5.0));
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next())
                matchId = rs.getInt(1);
            con.commit();
            con.close();
            return matchId;
        } catch(SQLException sqle){
            try{
                con.rollback();
                con.close();
            } catch (Exception ignored){}
        } catch (Exception ignored){
            ignored.printStackTrace();
        }
        return matchId;
    }

    public Match findByMatchId(Integer matchId){
        Connection con = null;
        Match match = null;
        try {
            con = MySqlConnector.getConnection();
            PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "getMatchByMatchId"));
            stmt.setInt(1, matchId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                match = new Match(
                        rs.getInt("match_id"), rs.getInt("team1"), rs.getInt("team2"),
                        rs.getInt("overs"), rs.getInt("maxovers"), rs.getString("winner")
                );
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.close();
            } catch (Exception ignored){}
        }
        return match;
    }

    public void update(Match match) {
        Connection con = null;
        try{
            con = MySqlConnector.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update MatchTable set team1 = ?, team2 = ?, winner = ? where match_id = ?");
            ps.setInt(1, match.getTeam1Id());
            ps.setInt(2, match.getTeam2Id());
            ps.setString(3, match.getMatchState().toString());
            ps.setInt(4, match.getMatchId());
            ps.executeUpdate();
            con.commit();
            con.close();
        } catch (SQLException sqle){
            try{
                con.rollback();
                con.close();
            } catch (Exception ignored){}
            sqle.printStackTrace();
        } catch(Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
