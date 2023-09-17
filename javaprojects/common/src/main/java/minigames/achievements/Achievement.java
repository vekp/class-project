package minigames.achievements;

import io.vertx.core.json.JsonObject;

/**
 * Main data record for a game achievement
 *
 * @param name          The achievement ID/name - must be unique within a game server (e.g no game server can have 2
 *                      achievements with the same ID
 * @param description   A player-friendly description of the achievement, will be shown to players on the achievement UI
 * @param xpValue       experience-point value for unlocking this achievement, for use with player level-up system
 * @param mediaFileName optional filename for custom images and sound files
 * @param hidden        if true, this achievement will not display its name or description in the achievement menu until it is
 *                      unlocked, instead it will be replaced by a 'secret achievement' placeholder panel.
 */
public record Achievement(
        String name,
        String description,
        int xpValue,
        String mediaFileName,
        boolean hidden
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
