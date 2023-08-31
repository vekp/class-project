package minigames.server.telepathy;

import io.vertx.core.json.JsonObject;
import minigames.telepathy.TelepathyCommandException;
import minigames.telepathy.TelepathyCommands;

import java.util.ArrayList;

/**
 * Stores player information amd state for use in Telepathy Minigame. Called a Player but
 * also logically represents a game client that connected to the server.
 */
public class Player {
    private String name;
    private Board board;

    private boolean ready;
    private ArrayList<JsonObject> pendingUpdates;

    /**
     * Construct a new player with a new board state.
     * 
     * Board state is random however the current construction uses the same seed for each 
     * board generated - therefore every board will start with the same layout of symbols/colours.
     * 
     * @param name String with the name of the Player to create.
     */
    public Player(String name) {
        this.name = name;
        this.ready = false;
        this.board = new Board();
        this.pendingUpdates = new ArrayList<>();
    }

    /**
     * Getter for the Player's name.
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get a reference to this Player's board state.
     * 
     * @return Board object representing this Player's board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Add a command to the queue of update commands. Only commands that are valid
     * TelepathyCommands can be added to the queue.
     * @param newCommand The new command to be added to the update list.
     */
    public void addUpdate(JsonObject newCommand){
        try{
            TelepathyCommands.valueOf(newCommand.getString("command"));
            this.pendingUpdates.add(newCommand);
        } catch(IllegalArgumentException e){
            throw new TelepathyCommandException(newCommand.getString("command"), "Not a valid Telepathy command.");
        }
            
    }

    /**
     * Get the updates pending for this Player. Clear the List so that a command is only
     * sent once.
     * @return Copy of the updateCommands List. 
     */
    public ArrayList<JsonObject> getUpdates(){
        ArrayList<JsonObject> updatesToSend = new ArrayList<>(this.pendingUpdates);
        // If there are no updates - send NOUPDATES command
        if(updatesToSend.size() == 0){
            updatesToSend.add(TelepathyGame.makeJsonCommand(TelepathyCommands.NOUPDATE));
        }
        
        this.pendingUpdates.clear();
        
        return updatesToSend;
    }

    /**
     * Toggles the player's current ready state.
     */
    public void toggleReady() {
        this.ready = !this.ready;
    }

    /**
     * Check if the player is ready to play Telepathy.
     * @return Boolean value with result of check.
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * Get a String that can represent this Player. Uses their name.
     */
    public String toString() {
        return this.name;
    }
}

