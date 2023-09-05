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

    private String gameName;
    private int maxPlayers;
    private boolean joinable; 

    // Currently connected players
    private HashMap<String, Player> players = new HashMap<>();
    
    private State state;
    private String winner;

    /**
     * Constructs a new game of Telepathy with a given name.
     * 
     * @param name: String with the name to use for this game.
     */
    public TelepathyGame(String name) {
        this.gameName = name;
        this.maxPlayers = 2;
        this.winner = " ";
        
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
        return new GameMetadata("Telepathy", this.gameName, this.players.keySet().toArray(new String[this.players.size()]), this.joinable);
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

        // List of reponse commands generated
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        for(JsonObject commandObject : commandPackage.commands()){
            // The TelepathyCommand used to specify how to handle the package
            TelepathyCommands command;
            try {
                command = TelepathyCommands.valueOf(commandObject.getString("command"));
            } catch (IllegalArgumentException e) {
                throw new TelepathyCommandException(commandObject.getString("command"), "Not a defined TelepathyCommands");
            }

            // Switch case to choose which function to call
            switch (command) {
                case QUIT -> renderingCommands.addAll(quitGame(commandPackage.player()));
                case SYSTEMQUIT -> renderingCommands.addAll(fullQuitGame(commandPackage.player()));
                case TOGGLEREADY -> renderingCommands.addAll(toggleReadyState(commandPackage.player()));
                case ASKQUESTION -> renderingCommands.addAll(takeQuestion(commandObject, commandPackage.player()));
                case CHOOSETILE -> renderingCommands.addAll(chooseTile(commandObject, commandPackage.player()));
                case FINALGUESS -> renderingCommands.addAll(takeQuestion(commandObject, commandPackage.player())); 
                case REQUESTUPDATE -> renderingCommands.addAll(updateClient(commandPackage.player()));
                default -> {
                    renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.INVALIDCOMMAND));
                }
            }    
        }

        RenderingPackage responsePackage = new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
        return responsePackage;
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
        logger.info(playerName + " wants to join Telepathy game"+ this.gameName);
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
                    .add(new NativeCommands.LoadClient("Telepathy", "Telepathy", this.gameName, playerName).toJson());
            
            // Extra commands to initialise the client window
            
            // Initialise ready button colour
            renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.BUTTONUPDATE, "readyButton", String.valueOf(this.players.get(playerName).isReady())));

            // TODO: Send initial board state - assign Symbols/Colours that are transparent and disabled?

            // Inform other players
            for(String p : this.players.keySet()){
                if (!p.equals(this.gameName)) {this.players.get(p).addUpdate(
                    TelepathyCommandHandler.makeJsonCommand(
                        TelepathyCommands.MODIFYPLAYER, 
                        playerName, 
                        "joined"));}
            }
        }
           
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    /* *********************************
     * CommandPackage handling methods
     * *********************************/

    /**
     * Handle a question from the client. The question can be a regular question 
     * asking for more information or the final question, specified by the command
     * value used.
     * 
     * @param commandObject: The JsonObject with the command from the client.
     * @param playerName: Name of the player asking the question.
     * @return ArrayList of the commands to be sent back to the client with the response.
     */
    private ArrayList<JsonObject> takeQuestion(JsonObject commandObject, String playerName){
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        if(this.state != State.RUNNING){
            renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(
                TelepathyCommands.INVALIDCOMMAND,
                "Cannot take question. Game not in RUNNING state"));
            return renderingCommands;
        }

        TelepathyCommands commandValue = TelepathyCommands.valueOf(commandObject.getString("command"));
        if(commandValue == TelepathyCommands.ASKQUESTION){
            // TODO: Tile comparison
            // TODO: Make partial comparison between guess and chosenTile

            // Check for partial match
        } else if(commandValue == TelepathyCommands.FINALGUESS){
            // TODO Make full comparison between guess and chosenTile
            
            // Check for exact match
        }

        return renderingCommands;
    }

    /**
     * Handler for the player selecting the Tile for their opponent to guess.
     * @param commandObject: JsonObject with the CHOOSETILE command value and tile
     *      coordinates.
     * @param playerName: Name of the player that is selecting their tile.
     * @return RenderingCommands indicating any changes the client needs to render.
     */
    private ArrayList<JsonObject> chooseTile(JsonObject commandObject, String playerName){
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        if(this.state != State.TILESELECTION){
            renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(
                TelepathyCommands.INVALIDCOMMAND, 
                "ERROR: Attempting to choose Tile while in " + State.TILESELECTION + " state."));
        }
        
        // Tile coordinates
        int x = Integer.parseInt(TelepathyCommandHandler.getAttributes(commandObject).get(0));
        int y = Integer.parseInt(TelepathyCommandHandler.getAttributes(commandObject).get(1));

        // Get the Tile then set the Player's chosen tile.
        Tile chosenTile = this.players.get(playerName).getBoard().getTile(x, y);        
        boolean chooseSuccess = this.players.get(playerName).setChosenTile(chosenTile);

        // TODO Determine how client should update to represent chosen tile

        if(chooseSuccess){
            renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(
                TelepathyCommands.POPUP,
                "Selected tile set!"));
        } else{
            renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(
                TelepathyCommands.INVALIDCOMMAND, 
                "Player tile has already been set"));
        }

        // Check for state transition from TILESELECTION to RUNNING
        transitionToRunning();
        return renderingCommands;
    }

    /**
     * Called when a player wants to leave the game. The response tells the client to go
     * to the menu screen.
     * 
     * @param leavingPlayer: String containing the name of the player leaving the game.
     * @return renderingCommand response to send with a QuitToMenu NativeCommand.
     */
    private ArrayList<JsonObject> quitGame(String leavingPlayer){
        ArrayList<JsonObject> renderingCommands =  new ArrayList<>();
        
        playerLeaveGame(leavingPlayer);

        renderingCommands.add(new NativeCommands.QuitToMenu().toJson());
        return renderingCommands;
    }

    /**
     * Called when the window of a client is closed while connected to the game. This method
     * removes the player from the game and then sends back a QUIT command. Sending back QUIT
     * instead of the QuitToMenu command prevents the client from hanging in gradlew if the 
     * game window is closed but still correctly removes the player from the game.
     * 
     * @param leavingPlayer: String containing the name of the player leaving the game.
     * @return RenderingPackage response to send with a QUIT command.
     */
    private ArrayList<JsonObject> fullQuitGame(String leavingPlayer) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        playerLeaveGame(leavingPlayer);

        renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.QUIT));
        return renderingCommands;
    }

    /**
     * Get the pending updates for a client that has requested them.
     * @param playerString: String containing the player's name
     * @return The RenderingPackage with any renderingCommands to be sent to the client.
     */
    private ArrayList<JsonObject> updateClient(String playerString){
        Player player = this.players.get(playerString);    
        return player.getUpdates();
    }

    /**
     * Handler for TOGGLEREADY CommandPackages. Toggle the player's ready state and respond
     * back to the client with the current state.
     * @param playerToToggle: String with the name of the player.
     * @return RenderingPackage responding with the player's current ready state.
     */
    private ArrayList<JsonObject> toggleReadyState(String playerToToggle) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        // toggle ready can only occur during game startup
        if(this.state != State.INITIALISE){
            renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.INVALIDCOMMAND));
            return renderingCommands;
        }
        
        this.players.get(playerToToggle).toggleReady();
        
        transitionToTileSelection();
        
        // Make response for client - update their ready button
        renderingCommands.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.BUTTONUPDATE,
                "readyButton",
                String.valueOf(this.players.get(playerToToggle).isReady())));

        return renderingCommands;
    }

    

    /* ******************************
     * State transition methods
     * ******************************/

    /**
     * Method for transitioning from INITIALISE to TILESELECTION. Checks that the 
     * game is in the correct state, the game is full and that all players are
     * ready.
     */
    private void transitionToTileSelection() {
        if(this.state != State.INITIALISE){ return;}
        if(this.players.size() < this.maxPlayers){ return;}

        // Check if all players are ready 
        for (String player : this.players.keySet()) {
            if (!this.players.get(player).isReady()) {
                return; 
            }
        }

        logger.info("\n\n********Moving to tile select...!********\n");

        this.state = State.TILESELECTION;
        this.joinable = false;

        // Send game start message
        updateAllPlayers(TelepathyCommandHandler.makeJsonCommand(
            TelepathyCommands.POPUP,
            "Game is starting! Choose your tile..."));
        
        // TODO: enable the game board
    }

    /**
     * Method for transitioning from the TILESELECTION to RUNNING state. This method
     * checks if the conditions for changing states have occured and then changes
     * state or does nothing.
     */
    private void transitionToRunning(){
        // Must be in TILESELECTION state
        if(this.state != State.TILESELECTION){
            return;
        }

        // All players must have selected a Tile
        for(String p : this.players.keySet()){
            if(this.players.get(p).getChosenTile() == null){
                return;
            }
        }

        this.state = State.RUNNING;
        updateAllPlayers(TelepathyCommandHandler.makeJsonCommand(
            TelepathyCommands.POPUP, "Tiles are selected. It is not player 1's turn"));
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
    private void transitionToGameOver(){
        // Check for game over conditions
        // If player leaves while game is running
        // If player gets correct final guess
        // If player gets incorrect final guess

        logger.info("\n\n*******Game has ended!*********\n");

        this.state = State.GAMEOVER;
        for(String p : this.players.keySet()){
            this.players.get(p).addUpdate(TelepathyCommandHandler.makeJsonCommand(
                TelepathyCommands.GAMEOVER, 
                this.winner));
        }
    }

    /* *********************************
     * Private helper methods
     * *********************************/

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
     * Decide what happens when a player leaves the game. If the game is running and a player
     * leaves then the game is over.
     * @param name Name of the player leaving the game.
     */
    private void playerLeaveGame(String name){
        this.players.remove(name);

        updateAllPlayers(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.MODIFYPLAYER, name, "leaving"));

        // End the game if a player leaves while game is RUNNING
        if(this.state != State.INITIALISE){
            transitionToGameOver();
        }
    }

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
        return this.gameName;
    }

    /**
     * Getter for the current game state.
     * @return State enum for the current state.
     */
    public State getState(){
        return this.state;
    }
}
