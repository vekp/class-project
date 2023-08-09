package minigames.achievements;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public record PlayerAchievementRecord(
        String playerID,
        List<GameAchievementState> gameAchievements

) {
    public static PlayerAchievementRecord fromJSON(String json) {
        JsonObject obj = new JsonObject(json);
        String name = obj.getString("playerID");
        JsonArray arr = obj.getJsonArray("gameAchievements");
        List<GameAchievementState> states = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            String s = arr.getString(i);
            states.add(GameAchievementState.fromJSON(s));
        }

        return new PlayerAchievementRecord(name, states);
    }

    public String toJSON() {
        JsonObject obj = new JsonObject();
        obj.put("playerID", playerID);
        JsonArray gameList = new JsonArray();
        for (int i = 0; i < gameAchievements.size(); i++) {
            gameList.add(gameAchievements.get(i).toJSON());
        }
        obj.put("gameAchievements", gameList);
        return obj.toString();
    }
}