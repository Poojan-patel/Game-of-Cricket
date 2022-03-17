package com.tekion.cricket.dbconnector;

import java.sql.*;

public class MySqlConnector {
    private static String PASSWORD = "root";
    private static String USERNAME = "root";
    private static String DB_URL = "jdbc:mysql://localhost:3306/cricket";
    private static String MYSQL_CLASS = "com.mysql.cj.jdbc.Driver";
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
        Class.forName(MYSQL_CLASS);
        conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
}
