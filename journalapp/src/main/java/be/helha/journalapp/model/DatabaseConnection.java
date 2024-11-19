package be.helha.journalapp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Attributes for the connection
    private String url;         // Database URL
    private String username;    // Username
    private String password;    // Password
    private Connection connection;

    // Constructor
    public DatabaseConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Method to establish a connection to the database.
     *
     * @return Connection the established Connection object, or null in case of error
     */
    public Connection connect() {
        try {
            // Load JDBC driver (example for MySQL)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Class.forName("org.postgresql.Driver"); // Uncomment for PostgreSQL if needed

            // Establish the connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to the database!");
            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
        return null; // Returns null if connection fails
    }

    /**
     * Method to close the database connection.
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Method to check the connection status.
     *
     * @return boolean true if connected, false otherwise.
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking connection: " + e.getMessage());
        }
        return false;
    }
}
