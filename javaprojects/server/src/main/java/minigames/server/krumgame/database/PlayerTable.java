package minigames.server.krumgame.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerTable {
    private Connection conn;

    public PlayerTable(Connection conn) {
        this.conn = conn;
    }
    // Method to create the player table
    public void create() {
        try (Statement stmt = conn.createStatement()) {
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "PLAYER", new String[] {"TABLE"});
            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE PLAYER (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "level INT NOT NULL, " +
                    "xp INT NOT NULL)";
                stmt.executeUpdate(createTableSQL);
                System.out.println("Table 'PLAYER' created successfully");
            } else {
                System.out.println("Table 'PLAYER' already exists");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
