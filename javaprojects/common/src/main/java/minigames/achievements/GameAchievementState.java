package minigames.achievements;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;


public record GameAchievementState(
        String gameID,
        List<Achievement> unlocked,
        List<Achievement> locked
) {
    public static GameAchievementState fromJSON(String json) {
        JsonObject obj = new JsonObject(json);
        String id = obj.getString("gameID");
        JsonArray unlockedList = obj.getJsonArray("unlocked");
        JsonArray lockedList = obj.getJsonArray("locked");

        List<Achievement> unlocked = new ArrayList<>();
        List<Achievement> locked = new ArrayList<>();
        for (int i = 0; i < unlockedList.size(); i++) {
            unlocked.add(Achievement.fromJSON(unlockedList.getString(i)));
        }
        for (int i = 0; i < lockedList.size(); i++) {
            locked.add(Achievement.fromJSON(lockedList.getString(i)));
        }
        return new GameAchievementState(id, unlocked, locked);
    }

    public String toJSON() {
        JsonObject obj = new JsonObject();
        obj.put("gameID", gameID);
        JsonArray unlockedList = new JsonArray();
        JsonArray lockedList = new JsonArray();
        for (Achievement achievement : unlocked) {
            unlockedList.add(achievement.toJSON());
        }
        for (Achievement achievement : locked) {
            lockedList.add(achievement.toJSON());
        }
        obj.put("unlocked", unlockedList);
        obj.put("locked", lockedList);

        return obj.toString();
    }
}
