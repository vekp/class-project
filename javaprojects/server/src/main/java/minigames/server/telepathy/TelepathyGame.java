package minigames.server.telepathy;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.RenderingCommand;
import minigames.rendering.RenderingPackage;
import minigames.rendering.NativeCommands.QuitToMenu;
import minigames.telepathy.TelepathyCommandException;
import minigames.telepathy.TelepathyCommands;
import minigames.telepathy.TelepathyCommandHandler;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private boolean joinable; 

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
        
        this.joinable = true;
        this.state = State.INITIALISE;
    }

    /**
     * Provides metadata about this TelepathyGame.
     * 
     * @return GameMetaData object with name of the game and players, and if the
     *         game can be joined.
     */
    public GameMetadata telepathyGameMetadata() {
        return new GameMetadata("Telepathy", name, this.players.keySet().toArray(new String[this.players.size()]), this.joinable);
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
        try {
            command = TelepathyCommands.valueOf(commandPackage.commands().get(0).getString("command"));
        } catch (IllegalArgumentException e) {
            // The command is invalid and handled by the default case - respond with INVALIDCOMMAND
            throw new TelepathyCommandException(commandPackage.commands().get(0).getString("command"), "Not a defined TelepathyCommands");
        }

        // Switch case to choose which function to call - default case returns INVALIDCOMMAND
        switch (command) {
            case QUIT -> response = quitGame(commandPackage);
            case SYSTEMQUIT -> response = fullQuitGame(commandPackage);
            case TOGGLEREADY -> response = toggleReadyState(commandPackage);
            case REQUESTUPDATE -> response = updateClient(commandPackage);
            default -> {
                ArrayList<JsonObject> renderingCommands = new ArrayList<>();
                renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.INVALIDCOMMAND));
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
                    TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.JOINGAMEFAIL, "Not a valid name!"));
        } else if(this.players.size() >= this.maxPlayers){
            renderingCommands.add(
                    TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.JOINGAMEFAIL, "Game is full!"));
        } else if(this.players.keySet().contains(playerName)){
            renderingCommands.add(
                    TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.JOINGAMEFAIL, "Name is already taken!"));
        } else {
            // Add the player to the game
            this.players.put(playerName, new Player(playerName));
            renderingCommands
                    .add(new NativeCommands.LoadClient("Telepathy", "Telepathy", this.name, playerName).toJson());
            
            // Extra commands to initialise the client window
            
            // Initialise ready button colour
            renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.BUTTONUPDATE, "readyButton", String.valueOf(this.players.get(playerName).isReady())));

            // TODO: Send initial board state - assign Symbols/Colours that are transparent and disabled?

            // Inform other players
            for(String p : this.players.keySet()){
                if (!p.equals(name)) {this.players.get(p).addUpdate(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.MODIFYPLAYER, playerName, "joined"));}
            }
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
    private RenderingPackage quitGame(CommandPackage commandPackage){
        ArrayList<JsonObject> renderingCommands =  new ArrayList<>();
        
        String leavingPlayer = commandPackage.player();
        playerLeaveGame(leavingPlayer);

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
    private RenderingPackage fullQuitGame(CommandPackage commandPackage) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        String leavingPlayer = commandPackage.player();
        playerLeaveGame(leavingPlayer);

        renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.QUIT));
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    /**
     * Used for adding commands to the update queue for all players on the server.
     * @param renderingCommand The command to add to all players for their next REQUESTUPDATE
     *      tick.
     */
    private void updateAllPlayers(JsonObject renderingCommand){
        for(String player: this.players.keySet()){
            this.players.get(player).addUpdate(renderingCommand);
        }
    }
    
    /**
     * Get the pending updates for a client that has requested them.
     * @param command CommandPackage with client information
     * @return The RenderingPackage with any renderingCommands to be sent to the client.
     */
    private RenderingPackage updateClient(CommandPackage command){
        Player player = this.players.get(command.player());
    
        ArrayList<JsonObject> renderingCommands = player.getUpdates();
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    /**
     * Handler for TOGGLEREADY CommandPackages. Toggle the player's ready state and respond
     * back to the client with the current state.
     * @param commandPackage CommandPackage received from the client containing a TOGGLEREADY command.
     * @return RenderingPackage responding with the player's current ready state.
     */
    private RenderingPackage toggleReadyState(CommandPackage commandPackage) {
        // toggle ready can only occur during game startup
        if(this.state != State.INITIALISE){
            return new RenderingPackage(this.telepathyGameMetadata(), 
                Collections.singletonList(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.INVALIDCOMMAND)));
        }
        
        this.players.get(commandPackage.player()).toggleReady();
        
        // Check if all players are ready 
        boolean allReady = true;
        for (String player : this.players.keySet()) {
            if (!this.players.get(player).isReady()) {
                allReady = false;
                break;
            }
        }
        
        // Start game if game full and players are ready
        if (this.players.size() == this.maxPlayers && allReady) { beginGame(); }

        // Make response for client - update their ready button
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.BUTTONUPDATE,
                "readyButton",
                String.valueOf(this.players.get(commandPackage.player()).isReady())));

        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    /**
     * Decide what happens when a player leaves the game. If the game is running and a player
     * leaves then the game is over.
     * @param name Name of the player leaving the game.
     */
    private void playerLeaveGame(String name){
        this.players.remove(name);

        updateAllPlayers(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.MODIFYPLAYER, name, "leaving"));

        // End the game if a player leaves while game is RUNNING
        if(this.state != State.INITIALISE){
            gameOver();
        }
    }

    /**
     * Handle gameOver sequence. Currently just informs client that the game is over and 
     * sends a QUIT command.
     * 
     * TODO - Finish implementation
     *  The GAMEOVER state should keep the game running until all players leave on
     *  their own volition. 
     *  The CLIENTUPDATE packets in GAMEOVER state should show who has won, who lost, etc...
     */
    private void gameOver(){
        logger.info("\n\n*******Game has ended!*********\n");

        this.state = State.GAMEOVER;
        for(String p : this.players.keySet()){
            this.players.get(p).addUpdate(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.GAMEOVER));
        }
    }

    /**
     * Transition the game from the INITIALISE state to RUNNING state.
     */
    private void beginGame() {
        logger.info("\n\n********Game has started!********\n");

        this.state = State.RUNNING;
        this.joinable = false;
        
    }

    /**
     * Create a JsonObject that can be used for RenderingCommands.
     * @param command The command value to be used for the rendering command JsonObject.
     * @param attributes An optional list of attributes to append to the attributes field.
     * @return A JsonObject with a String mapped to a 'command' key, and an array of
     *      Strings mapped to an 'attributes' key.
     
    public static JsonObject makeJsonCommand(TelepathyCommands command, String... attributes) {
        JsonObject jsonObject = new JsonObject().put("command", command.toString());
        if (attributes.length > 0) {
            jsonObject.put("attributes", new JsonArray().add(attributes));
        }
            
        return jsonObject;
    }
    */

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

    /**
     * Getter for the current game state.
     * @return State enum for the current state.
     */
    public State getState(){
        return this.state;
    }
}
