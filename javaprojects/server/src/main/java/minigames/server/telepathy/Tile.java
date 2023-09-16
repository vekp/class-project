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


    //getters

    public int getHorizontalPos() {return horizontalPosition;};

    public int getVerticalPos() {return verticalPosition;};

    public Colours getTileColour() {return tileColour;};

    public Symbols getTileSymbol() {return tileSymbol;};

  

}