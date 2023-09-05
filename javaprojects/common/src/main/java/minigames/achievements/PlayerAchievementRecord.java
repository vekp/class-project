package minigames.achievements;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

/** A record of player achievements earned, by game. Used as a data package to send from the server to the client to
 * populate an achievements menu
 * @param playerID the player who has earned these achievements
 * @param gameAchievements a list of game states - locked and unlocked achievements by game
 */
public record PlayerAchievementRecord(
        String playerID,
        List<GameAchievementState> gameAchievements
) {

    //The below converters were mainly needed as there were some issues getting the built-in mapping working with
    //when using the jsonObject map functions - it ended up being simpler for the moment just to manually create the
    //JSON data here (and offers a bit more control and vision over what is actually happening in the conversion)


    /**
     * Creates a Player achievement record from a JSON string
     * @param json the string to attempt to create a record from
     * @return the resulting player record
     */
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

    /**
     * Converts a player record into a JSON string to pass along the network
     * @return a JSON string containing the data for this player record.
     */
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
