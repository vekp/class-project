package minigames.commands;

import java.util.List;
import io.vertx.core.json.JsonObject;

/**
 * A package of commands sent from a player regarding their user accounts.
 * 
 * Commands are just given as a list of JSON objects. 
 * This lets the client and GameServers implement bespoke commands at will.
 * 
 * TODO: UserData should be loaded as a UserData object or a List of JSON Objects?
 */
public record UserCommand(
    String userName,
    //List<JsonObject> userData,
    List<JsonObject> commands
    
) {
    public static UserCommand fromJson(JsonObject json) {
        return new UserCommand(
            json.getString("userName"),
            //json.getJsonArray("userData").stream().map((o) -> (JsonObject)o).toList(),
            json.getJsonArray("commands").stream().map((o) -> (JsonObject)o).toList()
        );
    }
}
