package com.tekion.intern.repository;

import com.tekion.intern.dbconnector.MySqlConnector;
import com.tekion.intern.util.ReaderUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BallEventsRepository{
    public static void insertEvent
            (int matchId, int teamId, int inning, int ballNumber, int batsmanId, int bowlerId, int score, String extras, String wicket)
                throws SQLException, ClassNotFoundException
    {
        Connection con = MySqlConnector.getConnection();
        try{
            PreparedStatement ps = con.prepareStatement(ReaderUtil.readSqlFromFile("ballevents", "insertEvent"));
            ps.setInt(1,matchId);
            ps.setInt(2,teamId);
            ps.setInt(3,inning);
            ps.setInt(4,ballNumber);
            ps.setInt(7,score);

            if(batsmanId != -1) ps.setInt(5, batsmanId);
            else                ps.setNull(5, Types.INTEGER);

            if(bowlerId != -1)  ps.setInt(6, bowlerId);
            else                ps.setNull(6, Types.INTEGER);

            if(extras.equals(""))   ps.setNull(8,Types.VARCHAR);
            else                    ps.setString(8,extras);

            if(wicket.equals(""))   ps.setNull(9, Types.VARCHAR);
            else                    ps.setString(9,wicket);

            ps.execute();
            con.close();
        } catch(SQLException sqle){
            con.close();
            throw sqle;
        }
    }
}
