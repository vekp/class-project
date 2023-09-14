package minigames.server.krumgame.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AchievementsTable {
    private Connection conn;

    public AchievementsTable(Connection conn) {
        this.conn = conn;
    }

    // Method to create the achievements table
    public void create() {
        try (Statement stmt = conn.createStatement()) {
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "ACHIEVEMENTS", new String[] {"TABLE"});

            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE ACHIEVEMENTS (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "player_id INT REFERENCES players(id), " +
                    "achievement_name VARCHAR(255) NOT NULL)";
                stmt.executeUpdate(createTableSQL);
                System.out.println("Table 'ACHIEVEMENTS' created successfully");
            } else {
                System.out.println("Table 'ACHIEVEMENTS' already exists");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
