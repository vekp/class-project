package minigames.client.krumgame;

import java.sql.*;


public class MySQLConnector {

    // TODO: Update variables with actual values
    private static final String jdbcUrl = "jdbc:mysql://localhost:3306/database_name";
    private static final String dbUsername = "your_username";
    private static final String dbPassword = "your_password";

    /**
     * This method tries to retrieve xp associated with the username if it exists
     * else it inserts the username into the database and set xp as 0.
     */
    public static int getXpForUser(String username) {
        int xp = 0;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the database connection
            try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {

                // TODO: Create a prepared statement for the SELECT query
                String selectQuery = "SELECT XP FROM This_Game_Table WHERE Username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setString(1, username);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            xp = resultSet.getInt("XP");
                        } else {
                            // Insert new user with default XP of 0
                            String insertQuery = "INSERT INTO This_Game_Table (Username, XP) VALUES (?, 0)";
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                insertStatement.setString(1, username);
                                insertStatement.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return xp;
    }


    /**
     * This method is for updating the xp of a user.
     */
    public static void updateXpForUser(String username, int newXp) {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the database connection
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD)) {

                // Create a PreparedStatement
                String query = "UPDATE This_Game_Table SET XP = ? WHERE Username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    // Set the parameters
                    preparedStatement.setInt(1, newXp);
                    preparedStatement.setString(2, username);

                    // Execute the update
                    preparedStatement.executeUpdate();
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}

