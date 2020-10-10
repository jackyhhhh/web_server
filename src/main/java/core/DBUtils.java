package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBUtils {
    private static final String DB_HOST;
    private static final String DB_PORT;
    private static final String DB_NAME;
    private static final String DB_USER;
    private static final String DB_PASSWORD;
    private static final String JDBC_DRIVER;
    private static final String JDBC_URL;

    static{
        Properties config = new Properties();
        try {
            config.load(new FileInputStream("conf"+ File.separator+"db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DB_HOST = config.getProperty("db_host");
        DB_PORT = config.getProperty("db_port");
        DB_NAME = config.getProperty("db_name");
        DB_USER = config.getProperty("db_user");
        DB_PASSWORD = config.getProperty("db_password");
        JDBC_DRIVER = config.getProperty("jdbc_driver");
        JDBC_URL = "jdbc:mysql://"+DB_HOST+":"+DB_PORT+"/"+DB_NAME+"?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    }

    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

}
