package minigames.server.battleship;

/**
 * The Board Class contains all the information about the current state of a player's game board, including the player's name
 */
public class Board {
    private String playerName;
    private String[][] grid; // A two-dimensional array of Strings for drawing the game board
    private Ship[] vessels = new Ship[5];  // An array containing each of the five ship types for the current board
    private int turnNumber;  // The current turn number

    /**
     * The Constructor takes only the player's name as a parameter
     * @param playerName A String representing the name of the player
     */
    public Board(String playerName){
        this.playerName = playerName;
        this.turnNumber = 0;
        // Create a default grid
        this.grid = new String[][]{
            {" ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"},
            {"A", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"B", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"C", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"D", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"E", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"F", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"G", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"H", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"I", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
            {"J", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"}
        };
    }

    

    /**
     * Return the 2D string array to display in the client window
     * @return The 2D String Array in its current state
     */
    public String[][] getGrid(){return this.grid;}



    /**
     * Getter for the player name
     * @return The String of the player that owns the board
     */
    public String getPlayerName(){return this.playerName;}

    /**
     * Getter for the current turn number
     * @return An int for the current turn number
     */
    public int getTurnNumber(){return this.turnNumber;}

}
