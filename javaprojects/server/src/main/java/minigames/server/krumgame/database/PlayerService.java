package minigames.server.krumgame.database;

import io.vertx.core.json.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerService {
    private Connection conn;

    public PlayerService(Connection conn) {
        this.conn = conn;
    }

    // Method to add a new player
    public void addPlayer(String username) throws SQLException {
        String insertSQL = "INSERT INTO PLAYER (username, level, xp) VALUES (?, 0, 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }

    // Method to retrieve player level and XP by username
    public JsonObject getPlayerInfo(String username) throws SQLException {
        String selectSQL = "SELECT level, xp FROM PLAYER WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new JsonObject()
                        .put("level", rs.getInt("level"))
                        .put("xp", rs.getInt("xp"));
                } else {
                    // Insert the new player and return default values
                    addPlayer(username);
                    return new JsonObject()
                        .put("level", 0)
                        .put("xp", 0);
                }
            }
        }
    }

    // Method to update player level and XP
    public JsonObject updatePlayerInfo(String username, int newLevel, int newXP) throws SQLException {
        String updateSQL = "UPDATE PLAYER SET level = ?, xp = ? WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, newLevel);
            pstmt.setInt(2, newXP);
            pstmt.setString(3, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Player not found, could not update");
            }
        }
        return new JsonObject().put("status", "success");
    }

    // Method to retrieve player ID by username
    public int getPlayerId(String username) throws SQLException {
        String selectSQL = "SELECT id FROM PLAYER WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Player not found");
                }
            }
        }
    }

}
