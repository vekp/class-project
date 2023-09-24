package minigames.server.telepathy;

import io.vertx.core.json.JsonObject;
import minigames.telepathy.TelepathyCommandException;
import minigames.telepathy.TelepathyCommandHandler;
import minigames.telepathy.TelepathyCommands;
import minigames.telepathy.Tile;
import minigames.telepathy.Board;

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

    private Tile chosenTile;
    private Tile targetTile;
    private int turns;

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

        this.chosenTile = null;
        this.targetTile = null;
        this.turns = 0;
    }

    /**
     * Increment this Player's turn counter by 1.
     */
    public void incrementTurns(){
        this.turns++;
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
     * Toggles the player's current ready state.
     */
    public void toggleReady() {
        this.ready = !this.ready;
    }

    /**
     * Set the Player's chosen tile. This field can only be set once at the start
     * of the game. Once set the field is locked.
     * @param tile: The Tile object to set as this Player's chosen tile.
     * @return boolean value with the result of setting.
     */
    public boolean setChosenTile(Tile tile){
        if(this.chosenTile == null){
            this.chosenTile = tile;
            return true;
        } else{
            return false;
        }
    }

    /**
     * Set the target tile for this Player. The target tile represents the tile
     * that this Player's opponent has selected and is the tile that this Player
     * is trying to guess.
     * @param tile Tile to set as the target tile.
     * @return
     */
    public boolean setTargetTile(Tile tile){
        if(this.targetTile == null){
            this.targetTile = tile;
            return true;
        } else{
            return false;
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
            updatesToSend.add(TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.NOUPDATE));
        }
        
        this.pendingUpdates.clear();
        
        return updatesToSend;
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
     * Get the Player's chosen Tile.
     * @return A Tile object representing the Player's chosen Tile.
     */
    public Tile getChosenTile(){
        return this.chosenTile;
    }

    public Tile getTargetTile(){
        return this.targetTile;
    }

    /**
     * Check if the player is ready to play Telepathy.
     * @return Boolean value with result of check.
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * Get the number of turns this player has taken.
     * @return intger value with this player's turn count.
     */
    public int getTurnCounter(){
        return this.turns;
    }
    /**
     * Get a String that can represent this Player. Uses their name.
     */
    public String toString() {
        return this.name;
    }
}

