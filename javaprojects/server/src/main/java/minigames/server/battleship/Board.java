package minigames.server.battleship;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.round;

/**
 * The Board Class contains all the information about the current state of a player's game board, including the player's name
 */
public class Board {
    private String playerName;
    private String boardTitle;
    private Grid grid; // A two-dimensional array of Cells for drawing the game board
    private HashMap<String, Ship> vessels;  // A hashmap containing each of the five ship types for the current board
    private int turnNumber;  // The current turn number
    private String messageHistory;  // String of all valid messages, both game and player
    private GameState gameState;


    /**
     * The Constructor takes only the player's name as a parameter
     * @param playerName A String representing the name of the player
     */
    public Board(String playerName, String message){
        this.playerName = playerName;
        this.turnNumber = 0;
        this.vessels = new HashMap<>();
        this.grid = new Grid(); // Create a default grid
        defaultGrid();  // Set the grid to default ship positions
        this.messageHistory = message;
        this.gameState = GameState.SHIP_PLACEMENT;
        // Set the player to be the owner for all ships on this board
        this.setPlayerOwner();

    }


    // Getters
    /**
     * Return the 2D string array to display in the client window
     * @return The 2D String Array in its current state
     */
    public Cell[][] getGrid() {
        // There is probably a smarter way to do this too, but for now it stays
        return this.grid.getGrid();
    }

    /**
     * Getter for the player name
     * @return The String of the player that owns the board
     */
    public String getPlayerName() {return this.playerName;}

    /**
     * Getter for the current turn number
     * @return An int for the current turn number
     */
    public int getTurnNumber() {return this.turnNumber;}

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

    public HashMap<String, Ship> getVessels() {
        return vessels;
    }

    /**
     * Return the ship object of the specified class on the current game board
     * @param shipClass A String containing the class of the ship
     * @return
     */
    public Ship getShip(String shipClass){return this.vessels.get(shipClass);}

    // Setters
    /**
     * Sets the current game state for the player
     * @param gameState enum value
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Updates / sets the Ship objects within the vessels HashMap
     * @param vessels the Hashmap containing all Ship objects
     */
    public void setVessels(HashMap<String, Ship> vessels){
        this.vessels = vessels;
    }

    /**
     * Sets the player to be the owner for all ships on this board
     */
    public void setPlayerOwner(){
        this.vessels.forEach((key, value) ->{
            Ship current = value;
            current.setOwner(this.playerName);
            vessels.replace(key, current);
        });
    }

    /**
     * Sets the CellType for a given coordinate in the grid
     * @param x horizontal position
     * @param y vertical position
     * @param cellType enum value representing the cell state changing to
     */
    public void setGridCell(int x, int y, CellType cellType){
        this.grid.setCellType(x, y, cellType);
        // if the cell is being changed to a "hit" or "miss" cell, update the cell's shotAt boolean to be true
        // This function is also used to update the grid when placing ships, it was causing issues because all parts of the
        // ships were being read as "shotAt"
        if(cellType.toString().equals("X") || cellType.toString().equals(".")) {
            this.grid.shootCell(x, y);
        }
    }


    /**
     * Adds the user's input to the message history
     * @param input user's input
     */
    public void updateMessageHistory(String input) {
        this.messageHistory = getMessageHistory() + input;
    }

    /**
     * Increments the turn number
     */
    public void incrementTurnNumber() {
        this.turnNumber = getTurnNumber() + 1;
    }

    /**
     * Function to create a grid of strings to be displayed
     * @param boardTitle String value for the board title
     * @param grid 2D cell array values are retrieved from
     * @return formatted string to be displayed
     */
    public static String generateBoard(String boardTitle, Cell[][] grid) {
        StringBuilder gridStrings = new StringBuilder();
        String chars = "ABCDEFGHIJ";

        // Character width of the board
        int strLength = 24;
        int titleSpace = round((float) (strLength - boardTitle.length()) / 2);

        // Adds space to the start of the title to centre the text
        for (int i = 0; i < titleSpace; i++) {
            gridStrings.append(" ");
        }
        gridStrings.append(boardTitle).append("\n").append(" ---------------------\n");

        for (int i=0; i<11; i++) {
            for (int j = 0; j < 10; j++) {
                if (i==0 && j==0) gridStrings.append("   ");
                if (i==0) gridStrings.append(j).append(" ");
                if (j==0 && i!=0) gridStrings.append(" ").append(chars.charAt(i-1)).append(" ");
                if (i>0) {
                    gridStrings.append(grid[i-1][j].getCellTypeString()).append(" ");
                    //System.out.print(grid[i-1][j].getCellTypeString());
                }
            }
            if (i<10) gridStrings.append("\n");
        }
        return gridStrings.toString();
    }

    /**
     * Call the grid's default grid creator
     * @return A 2D array of cells
     */
    public Cell[][] defaultGridCreator() {return this.grid.defaultGridCreator();}

    //TODO: Fix - This is probably a really bad way of doing it

    // It calls the defaultShips() method, which will add ships to a hashmap, by adding a string key and calling the
    // placeShip() method, which puts a ship on the grid and then returns a ship object which is added to the hashmap
    // and finally returns the players grid after all this is complete (in defaultGrid() below)

    /**
     * Set the vessels map, and place ships on the grid in a default position
     * @return a 2D cell array
     */
    public Cell[][] defaultGrid() {
        this.vessels = new HashMap<>(this.grid.defaultShips());
        return this.getGrid();
    }

}
