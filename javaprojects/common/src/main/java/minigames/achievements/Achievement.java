package minigames.achievements;

import io.vertx.core.json.JsonObject;

public record Achievement(
        String name, //Achievement ID / Name - Must be unique per game server
        String description, //Description - shown to players (describe how to unlock, or add flavour text)
        String mediaFileName, //a file name (no extension) to search for custom media for icons/popup sounds, etc
        boolean hidden   //Hidden/Secret achievements - can prevent users from seeing hidden achievements until unlocked
) {

    public String toJSON() {
        JsonObject obj = new JsonObject();
        obj.put("name", name);
        obj.put("description", description);
        obj.put("mediaFileName", mediaFileName);
        obj.put("hidden", hidden);
        return obj.toString();
    }

    public static Achievement fromJSON(String json) {
        JsonObject obj = new JsonObject(json);
        return new Achievement(
                obj.getString("name"),
                obj.getString("description"),
                obj.getString("mediaFileName"),
                obj.getBoolean("hidden"));
    }


}
