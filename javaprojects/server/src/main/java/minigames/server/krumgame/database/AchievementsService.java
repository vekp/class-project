package minigames.server.krumgame.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class AchievementsService {
    private Connection conn;
    private PlayerService playerService;

    public AchievementsService(Connection conn, PlayerService playerService) {
        this.conn = conn;
        this.playerService = playerService;
    }

    // Method to add a new achievement
    public JsonObject addAchievement(String username, String achievementName) throws SQLException {
        int playerId = playerService.getPlayerId(username);
        String insertSQL = "INSERT INTO ACHIEVEMENTS (player_id, achievement_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, playerId);
            pstmt.setString(2, achievementName);
            pstmt.executeUpdate();
        }
        return new JsonObject().put("status", "success");
    }

    // Method to retrieve all achievements for a player
    public JsonObject getAchievements(String username) throws SQLException {
        int playerId = playerService.getPlayerId(username);
        List<String> achievements = new ArrayList<>();
        String selectSQL = "SELECT achievement_name FROM ACHIEVEMENTS WHERE player_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(rs.getString("achievement_name"));
                }
            }
        }
        return new JsonObject().put("achievements", new JsonArray(achievements));
    }
}

