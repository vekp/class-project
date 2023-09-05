package minigames.achievements;

import io.vertx.core.json.JsonObject;

public record Achievement(
        String name, //Achievement ID / Name - Must be unique per game server
        String description, //Description - shown to players (describe how to unlock, or add flavour text)
        int xpValue,
        String mediaFileName,//a file name (no extension) to search for custom media for icons/popup sounds, etc
        boolean hidden   //Hidden/Secret achievements - can prevent users from seeing hidden achievements until unlocked
) {

    //The below converters were mainly needed as there were some issues getting the built-in mapping working with
    //when using the jsonObject map functions - it ended up being simpler for the moment just to manually create the
    //JSON data here (and offers a bit more control and vision over what is actually happening in the conversion)

    /**
     * Takes in a JSON string for an achievement and converts it to an achievement object
     *
     * @param json the string to convert to an achievement
     * @return the resulting achievement object
     */
    public static Achievement fromJSON(String json) {
        JsonObject obj = new JsonObject(json);
        return new Achievement(
                obj.getString("name"),
                obj.getString("description"),
                obj.getInteger("xpValue"),
                obj.getString("mediaFileName"),
                obj.getBoolean("hidden"));
    }

    /**
     * Converter to create a JSON string from this achievement.
     *
     * @return the JSON string representing this achievement
     */
    public String toJSON() {
        JsonObject obj = new JsonObject();
        obj.put("name", name);
        obj.put("description", description);
        obj.put("xpValue", xpValue);
        obj.put("mediaFileName", mediaFileName);
        obj.put("hidden", hidden);
        return obj.toString();
    }


}
