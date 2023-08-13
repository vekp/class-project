import java.sql.*;

//This code is run any time a new user enters their username. We use that username to source the XP on file and
//return it into our main algorithm. This code will be accessible by all developers.

public class MySQLConnector {
    

    
    public static void main(String[] args) {

        //Imported from start of game
        String UNEUserName = "tempUser";

        //Variables to access MySQL database - assuming there is a username and password - change appropriately
        
        String jdbcUrl = "jdbc:mysql://localhost:3306/database_name";
        String username = "your_username";
        String password = "your_password";

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the database connection
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute a query - Example only
            String query = "SELECT XP FROM This_Game_Table";
            ResultSet resultSet = statement.executeQuery(query);

            // Process the result set
            while (resultSet.next()) {
                //check columnLabels etc.
                int xp = resultSet.getInt("level");
                // ... Retrieve other columns as needed
                System.out.println("Username is" + UNEUserName + ", XP is: " + xp);
                //xp must also be returned to the xp value in LevelUp.java

            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
