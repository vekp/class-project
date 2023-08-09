package minigames.achievements;

import io.vertx.core.json.JsonObject;

public record Achievement(
        String name, //Achievement ID / Name - Must be unique per game server
        String description, //Description - shown to players (describe how to unlock, or add flavour text)
        String type, //Indicates type of achievement, to determine what images to present to user
        boolean hidden   //Hidden/Secret achievements do not show up in UI lists until unlocked.
) {

    public String toJSON() {
        JsonObject obj = new JsonObject();
        obj.put("name", name);
        obj.put("description", description);
        obj.put("type", type);
        obj.put("hidden", hidden);
        return obj.toString();
    }

    public static Achievement fromJSON(String json) {
        JsonObject obj = new JsonObject(json);
        return new Achievement(
                obj.getString("name"),
                obj.getString("description"),
                obj.getString("type"),
                obj.getBoolean("hidden"));
    }
}
