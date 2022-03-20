package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.constants.Common;
import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MatchRepositoryImpl implements MatchRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String save(Match match){
        try {
            jdbcTemplate.update(ReaderUtil.readSqlFromFile("matchtable", "createMatch"),
                    match.getMatchId(), match.getTeam1Id(), match.getTeam2Id(), match.getOvers(), (int) Math.ceil((double) match.getOvers() / Common.MIN_BOWLERS));
            return match.getMatchId();
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
        return null;
//        Connection con = null;
//        //String matchId = null;
//        try {
//            con = MySqlConnector.getConnection();
//            con.setAutoCommit(false);
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "createMatch"), Statement.RETURN_GENERATED_KEYS);
//            ps.setString(1, match.getMatchId());
//            ps.setString(2, match.getTeam1Id());
//            ps.setString(3, match.getTeam2Id());
//            ps.setInt(4, match.getOvers());
//            ps.setInt(5, (int) Math.ceil((double) match.getOvers() / Common.MIN_BOWLERS));
//            ps.execute();
////            ResultSet rs = ps.getGeneratedKeys();
////            while (rs.next())
////                matchId = rs.getString(1);
//            con.commit();
//            con.close();
//            return match.getMatchId();
//        } catch(SQLException sqle){
//            try{
//                con.rollback();
//                con.close();
//            } catch (Exception ignored){}
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
    }

    @Override
    public Match findByMatchId(String matchId){
        Connection con = null;
        Match match = null;
        try{
            match = jdbcTemplate.queryForObject(ReaderUtil.readSqlFromFile("matchtable", "getMatchByMatchId"), BeanPropertyRowMapper.newInstance(Match.class), matchId);
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        try {
//            con = MySqlConnector.getConnection();
//            PreparedStatement stmt = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "getMatchByMatchId"));
//            stmt.setString(1, matchId);
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()){
//                match = new Match(
//                        rs.getString("match_id"), rs.getString("team1_id"), rs.getString("team2_id"),
//                        rs.getInt("overs"), rs.getInt("max_overs"), rs.getString("match_state")
//                );
//            }
//        } catch (SQLException sqle){
//            sqle.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            try{
//                con.close();
//            } catch (Exception ignored){}
//        }
        return match;
    }

    @Override
    public void update(Match match) {
        try {
            jdbcTemplate.update(ReaderUtil.readSqlFromFile("matchtable", "update"),
                    match.getTeam1Id(), match.getTeam2Id(), match.getMatchState(), match.getMatchId());
            List<Object> a = new ArrayList<>();
        } catch (DataAccessException dae){
            dae.printStackTrace();
        }
//        Connection con = null;
//        try{
//            con = MySqlConnector.getConnection();
//            con.setAutoCommit(false);
//            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("matchtable", "update"));
//            ps.setString(1, match.getTeam1Id());
//            ps.setString(2, match.getTeam2Id());
//            ps.setString(3, match.getMatchState());
//            ps.setString(4, match.getMatchId());
//            ps.executeUpdate();
//            con.commit();
//            con.close();
//        } catch (SQLException sqle){
//            try{
//                con.rollback();
//                con.close();
//            } catch (Exception ignored){}
//            sqle.printStackTrace();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }
}
