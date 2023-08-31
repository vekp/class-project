package minigames.telepathy;

import java.util.ArrayList;
import java.util.Arrays;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * A series of methods available to help parse and deal with TelepathyCommands
 */
public class TelepathyCommandHandler {
    

    /**
     * Get a List of the attributes stored in a renderingCommand.
     * @param renderingCommand: The renderingCommand portion of a RenderingPackage 
     *      received from the server.
     * @return ArrayList of Strings containing the attributes sent in the renderingCommand. 
     */
    public static ArrayList<String> getAttributes(JsonObject renderingCommand){
        System.out.println("Get attributes: " + renderingCommand.toString());
        
        ArrayList<String> strAttributes = new ArrayList<>();

        // JsonObject must have a TelepathyCommand
        try{
            TelepathyCommands.valueOf(renderingCommand.getString("command"));
        } catch(IllegalArgumentException e){
            throw new TelepathyCommandException(renderingCommand.getString("command"), "Not a valid Telepathy Command");
        }

        // Get each string stored in the JsonArray
        JsonArray jsonAttributes = renderingCommand.getJsonArray("attributes");
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
        System.out.println("Made command: " + jsonObject.toString());
        return jsonObject;
    }
}
