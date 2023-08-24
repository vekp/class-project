package minigames.server.telepathy;

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


    //getters

    public int getHorizontalPos() {return horizontalPosition;};

    public int getVerticalPos() {return verticalPosition;};

    public Colours getTileColour() {return tileColour;};

    public Symbols getTileSymbol() {return tileSymbol;};

  

}