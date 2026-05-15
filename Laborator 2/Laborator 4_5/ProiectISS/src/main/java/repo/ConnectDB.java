package repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectDB {
    private Properties jdbcProps;
    private Connection connection = null;

    public ConnectDB(Properties props) {
        this.jdbcProps = props;
    }

    private Connection getNewConnection() {
        String url = jdbcProps.getProperty("jdbc.url");

        Connection con = null;
        try {
            con = DriverManager.getConnection(url);
            try (Statement st = con.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }
       } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return con;
    }

    public Connection getConnection() {
       try {
            if (connection == null || connection.isClosed()) {
                connection = getNewConnection();
            }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("error while closing connection: " + e.getMessage());
        }
    }
}