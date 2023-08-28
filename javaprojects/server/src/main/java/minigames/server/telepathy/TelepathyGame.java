package minigames.server.telepathy;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.RenderingCommand;
import minigames.rendering.RenderingPackage;
import minigames.rendering.NativeCommands.QuitToMenu;
import minigames.telepathy.TelepathyCommands;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents a game of Telepathy that can be played. Stores and manages the current running state
 * of the game by tracking players, board, etc, and deciding how commands received from clients 
 * need to be handled.
 * 
 * Users can create a new game or connect to an existing game using the minigame network interface.  
 */
public class TelepathyGame {

    // Logs output
    private static final Logger logger = LogManager.getLogger(TelepathyGame.class);

    // Name assigned to the game on creation
    private String name;
    private int maxPlayers;

    // Currently connected players
    private HashMap<String, Player> players = new HashMap<>();
    
    private State state;

    /**
     * Constructs a new game of Telepathy with a given name.
     * 
     * @param name: String with the name to use for this game.
     */
    public TelepathyGame(String name) {
        this.name = name;
        this.maxPlayers = 2;
        
        this.state = State.INITIALISE;
    }

    /**
     * Provides metadata about this TelepathyGame.
     * 
     * @return GameMetaData object with name of the game and players, and if the
     *         game can be joined.
     */
    public GameMetadata telepathyGameMetadata() {
        return new GameMetadata("Telepathy", name, this.players.keySet().toArray(new String[this.players.size()]), true);
    }

    /**
     * Run commands that are sent by clients connected to this game. During handling of the command
     * a response is generated and returned in a RenderingPackage.
     * 
     * This game expects packets to begin with a command field that contains a string representing
     * a TelepathyCommands enum constant. It can then be followed by a list of attributes if any 
     * other data is required for the execution of the command. The command string determines what 
     * method to send the package to. 
     *  
     * @param commandPackage: The CommmandPackage object to be run. It contains data in a JSON format 
     *      that the client has sent to this game.
     * @return A RenderingPackage object containing a response for the client. This response is 
     *      constructed by the method that handles the CommandPackage. 
     */
    public RenderingPackage runCommands(CommandPackage commandPackage) {
        logger.info("Received command package {}", commandPackage);

        // The response that is to be sent back to client
        RenderingPackage response;

        // The TelepathyCommand used to specify how to handle the package
        TelepathyCommands command;
        try{
            command = TelepathyCommands.valueOf(commandPackage.commands().get(0).getString("command"));
        } catch (IllegalArgumentException e) {
            // The command is invalid and handled by the default case - respond with INVALIDCOMMAND
            command = TelepathyCommands.INVALIDCOMMAND;
        }
        
        // Switch case to choose which function to call - default case returns empty RenderingPackage
        switch(command){
            case QUIT -> response = quitGame(commandPackage);
            case SYSTEMQUIT -> response = fullQuitGame(commandPackage);
            default -> {
                ArrayList<JsonObject> renderingCommands = new ArrayList<>();
                renderingCommands.add(new JsonObject().put("command", TelepathyCommands.INVALIDCOMMAND.toString()));
                response = new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
            }
        }

        return response;
    }

    /**
     * Adds a player to the game and makes up a RenderPackage with instructions for the client.
     * Before being able to join the name of the player is validated to ensure no problems occur.
     * Duplicate names or names containing invalid characters are not allowed.
     * 
     * @param playerName: Name of the player wanting to join.
     * @return RenderingPackage with instructions for the client.
     */
    public RenderingPackage joinGame(String playerName) {
        logger.info(playerName + " wants to join Telepathy game"+ this.name);
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        if (!validName(playerName)) {
            renderingCommands.add(
                    new JsonObject().put("command", TelepathyCommands.JOINGAMEFAIL).put("message", "Not a valid name!"));
        } else if(this.players.size() >= this.maxPlayers){
            renderingCommands.add(
                    new JsonObject().put("command", TelepathyCommands.JOINGAMEFAIL).put("message", "Game is full!"));
        } else if(this.players.keySet().contains(playerName)){
            renderingCommands.add(
                    new JsonObject().put("command", TelepathyCommands.JOINGAMEFAIL).put("message", "Name is already taken!"));
        } else {
            // Add the player to the game
            this.players.put(playerName, new Player(playerName));
            renderingCommands
                    .add(new NativeCommands.LoadClient("Telepathy", "Telepathy", this.name, playerName).toJson());
        }
           
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    /**
     * Called when a player wants to leave the game. The response tells the client to go
     * to the menu screen.
     * 
     * @param commandPackage CommandPackage received with a QUIT command.
     * @return RenderingPackage response to send with a QuitToMenu NativeCommand.
     */
    public RenderingPackage quitGame(CommandPackage commandPackage){
        ArrayList<JsonObject> renderingCommands =  new ArrayList<>();
        
        String leavingPlayer = commandPackage.player();
        this.players.remove(leavingPlayer);
        
        renderingCommands.add(new NativeCommands.QuitToMenu().toJson());
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    /**
     * Called when the window of a client is closed while connected to the game. This method
     * removes the player from the game and then sends back a QUIT command. Sending back QUIT
     * instead of the QuitToMenu command prevents the client from hanging in gradlew if the 
     * game window is closed but still correctly removes the player from the game.
     * 
     * @param commandPackage CommandPackage received with a SYSTEMQUIT command.
     * @return RenderingPackage response to send with a QUIT command.
     */
    public RenderingPackage fullQuitGame(CommandPackage commandPackage){
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        String leavingPlayer = commandPackage.player();
        this.players.remove(leavingPlayer);

        renderingCommands.add(new JsonObject().put("command", TelepathyCommands.QUIT));
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    /**
     * Remove a player from the game by setting their spot to in this.players to null.
     * 
     * @param playerName The player to remove from the game.
     
    private void removePlayer(String playerName){
        for(int i = 0; i < this.players.length; i++){
            if(this.players[i] == null) continue;
            if(this.players[i].getName().equals(playerName)){
                this.players[i] = null;
                continue;
            }
        }
    }*/

    /**
     * Checks if a name is valid for use.
     * 
     * Invalid names include:
     *  Empty strings
     *  Names only containing white space
     *  Names containing spaces
     * @param name String containing name to be validated
     * @return Boolean value with result of validation
     */
    private boolean validName(String name){
        if(name == null) return false;
        if(name.isBlank()) return false;
        if(name.contains(" ")) return false;

        return true;
    }

    // Accessor methods

    /**
     * Getter for current players in the game.
     * @return copy of the HashSet of players.
     */
    public HashMap<String, Player> getPlayers(){
        return new HashMap<>(this.players);
    }

    /**
     * Getter for the name assigned to this game on creation.
     * @return String value with the name.
     */
    public String getName(){
        return this.name;
    }
}
