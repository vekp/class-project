package minigames.server.telepathy;

import minigames.telepathy.Symbols; 
import minigames.telepathy.Colours;

/**
 * A class representing a tile in the Telepathy board.
 */
public class Tile{

    private Colours tileColour; 
    private Symbols tileSymbol;
    private int horizontalPosition;
    private int verticalPosition;

    private boolean eliminated;
    private boolean guessed;

    /**
     * constructor takes parameters to initialse a tile object
     * 
     * @param horizontalPosition int representing the tile's horizontal position on the board
     * @param verticalPosition int representing the tile's vertical position on the board
     * @param tileColour String represeting the tile's colour
     * @param tileSymbol String representing the tile's symbol
     */
    public Tile(int horizontalPosition, int verticalPosition, Colours tileColour, Symbols tileSymbol) {

        this.horizontalPosition = horizontalPosition;
        this.verticalPosition = verticalPosition;
        this.tileColour = tileColour;
        this.tileSymbol = tileSymbol;

        // Tiles start as not eliminated and not guessed
        this.eliminated = false;
        this.guessed = false; 
    }

    /**
     * compares chosentile against a real tile elements
     * @param tile a Tile object representing a tile
     * @return true if the tile features does not completely match this chosen tile elements
     */
    public boolean isPartialMatch(Tile tile) {
        if(this.getHorizontalPos() == (tile.getHorizontalPos())) return true;
        if(this.getVerticalPos() == (tile.getVerticalPos())) return true;
        if(this.getTileColour().equals(tile.getTileColour())) return true;
        if(this.getTileSymbol().equals(tile.getTileSymbol())) return true;
        return false;
    }

    /**
     * compares chosentile against a real tile elements
     * @param tile a Tile object representing a tile
     * @return true if the tile features completely match the chosentile elements
     */
    public boolean isFullMatch(Tile tile) {
        if(!(this.getHorizontalPos() == tile.getHorizontalPos())) return false;
        if(!(this.getVerticalPos() == tile.getVerticalPos())) return false;
        if(!this.getTileColour().equals(tile.getTileColour())) return false;
        if(!this.getTileSymbol().equals(tile.getTileSymbol())) return false;
        return true;
    }

    /**
     * Get the String representation for this Tile. Can be used to transmit the Tile attributes to the Client.
     * @return String containing this Tile's position, symbol and colour. 
     */
    public String toString() {
        String outString = this.getHorizontalPos() + "," + this.getVerticalPos() + "," + this.getTileColour().toString()
                + "," + this.getTileSymbol();
                
        return outString;
    }

    /**
     * Eliminate this Tile from possible future guesses. Used when a no response
     * is the result from a 'question'.
     */
    public void eliminate(){
        this.eliminated = false;
    }

    //getters

    public int getHorizontalPos() {return horizontalPosition;}

    public int getVerticalPos() {return verticalPosition;}

    public Colours getTileColour() {return tileColour;}

    public Symbols getTileSymbol() {return tileSymbol;}

    public boolean isEliminated() { return this.eliminated; }

    public boolean hasBeenGuessed() { return this.guessed; }

       

}