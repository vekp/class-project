package minigames.server.battleship;

import static java.lang.Math.round;

/**
 * The Board Class contains all the information about the current state of a player's game board, including the player's name
 */
public class Board {
    private String playerName;
    private String boardTitle;
    private Grid grid; // A two-dimensional array of Cells for drawing the game board
    private Ship[] vessels = new Ship[5];  // An array containing each of the five ship types for the current board
    private int turnNumber;  // The current turn number
    private String messageHistory;
    private GameState gameState;

    /**
     * The Constructor takes only the player's name as a parameter
     * @param playerName A String representing the name of the player
     */
    public Board(String playerName, String message){
        this.playerName = playerName;
        this.turnNumber = 0;
        // Create a default grid
        this.grid = new Grid();
        this.messageHistory = message;
        this.gameState = GameState.SHIP_PLACEMENT;
    }




    // Getters
    /**
     * Return the 2D string array to display in the client window
     * @return The 2D String Array in its current state
     */
    public Cell[][] getGrid(){
        // There is probably a smarter way to do this too, but for now it stays
        return this.grid.getGrid();
    }

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

    /**
     * Getter for the player's message history
     * @return the message history string
     */
    public String getMessageHistory() {
        return messageHistory;
    }

    /**
     * Getter for the player's current game state
     * @return enum game state value
     */
    public GameState getGameState() {
        return gameState;
    }

    // Setters

    /**
     * Sets the current game state for the player
     * @param gameState enum value
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void updateMessageHistory(String input) {
        this.messageHistory = getMessageHistory() + input;
    }
    public void incrementTurnNumber() {
        this.turnNumber = getTurnNumber() + 1;
    }

    /**
     * Function to create a grid of strings to be displayed
     * @param boardTitle
     * @param grid
     * @return
     */
    public static String generateBoard(String boardTitle, Cell[][] grid) {
        StringBuilder gridStrings = new StringBuilder();
        String chars = "ABCDEFGHI";

        // Character width of the board
        int strLength = 22;
        int titleSpace = round((strLength - boardTitle.length()) / 2);

        // Adds space to the start of the title to centre the text
        for (int i = 0; i < titleSpace; i++) {
            gridStrings.append(" ");
        }
        gridStrings.append(boardTitle).append("\n").append(" ---------------------\n");

        for (int i=0; i<10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i==0 && j==0) gridStrings.append("   ");
                if (i==0) gridStrings.append(j).append(" ");
                if (j==0 && i!=0) gridStrings.append(" ").append(chars.charAt(i-1)).append(" ");
                if (i>0) gridStrings.append(grid[i][j].getCellType()).append(" ");
            }
            gridStrings.append("\n");
        }
        return gridStrings.toString();
    }

    /**
     * Call the grid's default grid creator
     * @return A 2D array of cells
     */
    public Cell[][] defaultGridCreator() {return this.grid.defaultGridCreator();}

}
