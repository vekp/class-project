package minigames.telepathy;

import java.util.ArrayList;
import java.util.Arrays;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * A series of methods available to help parse and deal with TelepathyCommands
 */
public final class TelepathyCommandHandler {
    /**
     * Get a List of the attributes stored in a JsonObject command.
     * @param jsonCommand: The JSON portion of a network package with attributes
     *      to be acquired. 
     * @return ArrayList of Strings parsed from the 'attributes' key. If there are
     *      no attributes or the key does not exist an empty ArrayList is returned.
     */
    public static ArrayList<String> getAttributes(JsonObject jsonCommand){
        System.out.println("Get attributes: " + jsonCommand.toString());
        
        ArrayList<String> strAttributes = new ArrayList<>();

        // JsonObject must have a TelepathyCommand
        try{
            TelepathyCommands.valueOf(jsonCommand.getString("command"));
        } catch(IllegalArgumentException e){
            throw new TelepathyCommandException(jsonCommand.getString("command"), "Not a valid Telepathy Command");
        }

        // Get each string stored in the JsonArray
        JsonArray jsonAttributes = jsonCommand.getJsonArray("attributes");
        if(jsonAttributes == null){
            return strAttributes;
        }

        for(int i = 0; i < jsonAttributes.size(); i++){
            strAttributes.add(jsonAttributes.getString(i));
        }

        return strAttributes;
    }

    /**
     * Create a JsonObject that can be used for RenderingCommands.
     * @param command The command value to be used for the rendering command JsonObject.
     * @param attributes An optional list of attributes to append to the attributes field.
     * @return A JsonObject with a String mapped to a 'command' key, and an array of
     *      Strings mapped to an 'attributes' key.
     */
    public static JsonObject makeJsonCommand(TelepathyCommands command, String... attributes) {
        JsonObject jsonObject = new JsonObject().put("command", command.toString());
        if (attributes.length > 0) {
            JsonArray attributeArray = new JsonArray(Arrays.asList(attributes));
            jsonObject.put("attributes", attributeArray);
        }
        return jsonObject;
    }

    public static ArrayList<Tile> getTilesFromCommand(JsonObject command, Board board) {
        ArrayList<String> attributes = getAttributes(command);
        
        // There must be an even number of attributes
        if (attributes.size() % 2 != 0) {
            throw new TelepathyAttributeException(attributes.toString(),
                    "This command does not have the correct attributes to represent a Tile object on the board");
        }

        // Get each Tile
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < attributes.size() / 2; i++) {
            int x = Integer.parseInt(attributes.get(i));
            int y = Integer.parseInt(attributes.get(i + 1));
            tiles.add(board.getTile(x, y));
        }
        return tiles;
    }

}
