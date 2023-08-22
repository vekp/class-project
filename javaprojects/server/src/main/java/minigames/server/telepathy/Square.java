package minigames.server.telepathy;

/**
 * A class representing a square in the Telepathy board.
 */

public class Square{

    private Colours squareColour; 
    private Symbols squareSymbol;
    private int horizontalPosition;
    private int verticalPosition;


    /**
     * constructor takes parameters to initialse a square object
     * @param horizontalPosition int representing the square's horizontal position on the board
     * @param verticalPosition int representing the square's vertical position on the board
     * @param squareColour String represeting the sqaure's colour
     * @param squareSymbol String representing the sqaure's symbol
     */

    public Square(int horizontalPosition, int verticalPosition, Colours squareColour, Symbols squareSymbol) {

        this.horizontalPosition = horizontalPosition;
        this.verticalPosition = verticalPosition;
        this.squareColour = squareColour;
        this.squareSymbol = squareSymbol;

    }


    //getters

    public int getHorizontalPos() {return horizontalPosition;};

    public int getVerticalPos() {return verticalPosition;};

    public Colours getSquareColour() {return squareColour;};

    public Symbols getSquareSymbol() {return squareSymbol;};

  

}