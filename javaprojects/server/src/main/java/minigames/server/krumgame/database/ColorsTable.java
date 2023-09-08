package minigames.server.krumgame.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ColorsTable {
    private Connection conn;

    public ColorsTable(Connection conn) {
        this.conn = conn;
    }
    // Method to create the colors table
    public void create() {
        try (Statement stmt = conn.createStatement()) {
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "COLORS", new String[] {"TABLE"});

            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE COLORS (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "player_id INT REFERENCES players(id), " +
                    "color_name VARCHAR(255) NOT NULL)";
                stmt.executeUpdate(createTableSQL);
                System.out.println("Table 'COLORS' created successfully");
            } else {
                System.out.println("Table 'COLORS' already exists");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
