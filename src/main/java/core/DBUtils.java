package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    private static final String JDBC_DRVIER = "com.mysql.cj.jdbc.DRIVER";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/my_server";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName(JDBC_DRVIER);
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
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
