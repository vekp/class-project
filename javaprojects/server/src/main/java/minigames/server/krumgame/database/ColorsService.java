package minigames.server.krumgame.database;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColorsService {
    private Connection conn;
    private PlayerService playerService;

    public ColorsService(Connection conn, PlayerService playerService) {
        this.conn = conn;
        this.playerService = playerService;
    }

    // Method to add a new color
    public JsonObject addColor(String username, String colorName) throws SQLException {
        int playerId = playerService.getPlayerId(username);
        String insertSQL = "INSERT INTO COLORS (player_id, color_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, playerId);
            pstmt.setString(2, colorName);
            pstmt.executeUpdate();
        }
        return new JsonObject().put("status", "success");
    }

    // Method to retrieve all colors for a player
    public JsonObject getColors(String username) throws SQLException {
        int playerId = playerService.getPlayerId(username);
        List<String> colors = new ArrayList<>();
        String selectSQL = "SELECT color_name FROM COLORS WHERE player_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    colors.add(rs.getString("color_name"));
                }
            }
        }
        return new JsonObject().put("colors", new JsonArray(colors));
    }
}
