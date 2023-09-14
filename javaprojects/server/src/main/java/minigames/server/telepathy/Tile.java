package minigames.server.telepathy;

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
     * Get the String representation for this Tile. Can be used to transmit the Tile attributes to the Client.
     * @return String containing this Tile's position, symbol and colour. 
     */
    public String toString() {
        String outString = this.getHorizontalPos() + "," + this.getVerticalPos() + "," + this.getTileColour().toString()
                + "," + this.getTileSymbol();
                
        return outString;
    }

    // Set the eliminated and guessed fields - can never be unset
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