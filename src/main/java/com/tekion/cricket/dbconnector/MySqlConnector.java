package com.tekion.cricket.dbconnector;

import java.sql.*;

public class MySqlConnector {
    private static Connection conn = null;
//    private static Context ctx = null;
//    private static DataSource ds = null;
//
//    static {
//        try {
//            ctx = new InitialContext();
//            ds = (DataSource) ctx.lookup("")
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if(conn == null || conn.isClosed()) {
            initializeConnection();
        }
        return conn;
    }

    private static void initializeConnection() throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cricket","root","root");
    }
}
