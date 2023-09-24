package minigames.server.battleship;


import java.util.*;

import minigames.server.achievements.AchievementHandler;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.round;
import static minigames.server.battleship.achievements.MISSION_COMPLETE;

/**
 * The Board Class contains all the information about the current state of a player's game board, including the player's name
 */
public class Board {

    // Fields
    private Grid grid; // A two-dimensional array of Cells for drawing the game board
    private HashMap<String, Ship> vessels;  // A hashmap containing each of the five ship types for the current board
    private GameState gameState;
    private int lastRowShot;
    private int lastColShot;

    private boolean deadBoard; //tells us whether all the ships on this board have been destroyed

    // Constructor

    /**
     * The Constructor takes only the player's name as a parameter
     */
    public Board() {
        this.vessels = new HashMap<>();
        this.grid = new Grid(); // Create a default grid
        this.gameState = GameState.PENDING_READY;
        chooseGrid();
        // Initialise last shot to invalid coordinates.
        this.lastRowShot = -1;
        this.lastColShot = -1;
    }

    // Getters

    /**
     * Return the 2D string array to display in the client window
     *
     * @return The 2D String Array in its current state
     */
    public Cell[][] getGrid() {
        // System.out.println(this.gameState.toString());
        if (this.gameState == GameState.PENDING_READY) {
            System.out.println("Edited Grid");
            this.grid.generateGrid(this.vessels);
        }
        return this.grid.getGrid();
    }

    /**
     * Getter for the player's current game state
     *
     * @return enum game state value
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Function to get list of vessels
     *
     * @return hashmap of vessels - Ship objects
     */
    public HashMap<String, Ship> getVessels() {
        return vessels;
    }

    /**
     * Return the ship object of the specified class on the current game board
     *
     * @param shipClass A String containing the class of the ship
     * @return Ship object
     */
    public Ship getShip(String shipClass) {
        return this.vessels.get(shipClass);
    }

    /**
     * @return getter for the alive/dead state of board
     */
    public boolean isBoardDead() {
        return deadBoard;
    }
    // Setters

    /**
     * Sets the current game state for the player
     *
     * @param gameState enum value
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Updates / sets the Ship objects within the vessels HashMap
     *
     * @param vessels the Hashmap containing all Ship objects
     */
    public void setVessels(HashMap<String, Ship> vessels) {
        this.vessels = vessels;
    }

    /**
     * Sets the CellType for a given coordinate in the grid
     *
     * @param col      horizontal position
     * @param row      vertical position
     * @param cellType enum value representing the cell state changing to
     */
    public void setGridCell(int col, int row, CellType cellType) {
        this.grid.setCellType(col, row, cellType);
        // if the cell is being changed to a "hit" or "miss" cell, update the cell's shotAt boolean to be true
        // This function is also used to update the grid when placing ships, it was causing issues because all parts of the
        // ships were being read as "shotAt"
        if (cellType.toString().equals("X") || cellType.toString().equals(".")) {
            this.grid.shootCell(col, row);
        }
    }

    /**
     * Sets the most recent shot fields to the given coordinates.
     */
    public void setLastShot(int row, int col) {
        this.lastRowShot = row;
        this.lastColShot = col;
    }

    // Methods

    /**
     * Function to initialise grid's layout of ships
     */
    public void chooseGrid() {
        defaultGrid(this.grid.defaultShips());
    }

    /**
     * Set the vessels map, and place ships on the grid in a default position
     */
    public void defaultGrid(HashMap<String, Ship> ships) {
        this.vessels = new HashMap<>(ships);
        this.getGrid();
    }

    /**
     * Sinks all ships on the current board, for testing achievements and game state
     */
    public void sinkAll(String playerName) {
        vessels.forEach((shipType, ship) -> {
            Cell[] shipParts = ship.getShipParts();
            for (int i = 0; i < shipParts.length; i++) {
                int col = shipParts[i].getVerticalCoordInt();
                int row = shipParts[i].getHorizontalCoord();
                ship.updateShipStatus(col, row, playerName);
            }
        });
    }

    /**
     * Function to determine if the game has ended with all ships being sunk
     *
     * @param name Name of the player to check
     * @return true if game over
     */
    public boolean checkGameOver(String name) {
        // for ship in vessels hashmap, check if it is sunk and increment counter
        deadBoard = false;
        AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
        int counter = 0;
        for (Map.Entry<String, Ship> ship : vessels.entrySet()) {
            if (ship.getValue().isSunk()) {
                counter++;
            }
            if (counter == 5) {
                handler.unlockAchievement(name, MISSION_COMPLETE.toString());
                deadBoard = true;

            }
        }
        return deadBoard;
    }

    /**
     * Method to get the ship corresponding to the coordinate input
     *
     * @param target  cell used to grab coordinates from (new cell created when passed in with the user's x,y coord)
     * @param vessels player's list of vessels
     * @return the Ship object shot at
     */
    public Ship getVessel(Cell target, HashMap<String, Ship> vessels) {
        ArrayList<String> shipClasses = new ArrayList<>();
        vessels.forEach((key, value) -> {
            shipClasses.add(key);
            System.out.println(key);
        });

        for (String shipClass : shipClasses) {
            Cell[] parts = vessels.get(shipClass).getShipParts();
            ArrayList<String> coords = new ArrayList<>();
            for (Cell part : parts) {
                coords.add(part.getBothCoords());
            }
            if (coords.contains(target.getBothCoords())) {
                System.out.println("Coordinate contained");
                return vessels.get(shipClass);
            }

        }
        // Returns a 'null' ship which will give false values required for the functions use
        return new Ship("", 0, new Cell[0], 0, 0, true);
    }

    /**
     * Function to create a grid of strings to be displayed
     *
     * @param boardTitle String value for the board title
     * @return HTML formatted string to be displayed
     */
    public String generateBoard(String boardTitle, boolean isEnemy) {
        StringBuilder gridStrings = new StringBuilder();
        String chars = "ABCDEFGHIJ";
        Cell[][] grd = getGrid();

        // Character width of the board
        int strLength = 24;
        int titleSpace = round((float) (strLength - boardTitle.length()) / 2);

        // Adds space to the start of the title to centre the text
        for (int i = 0; i < titleSpace; i++) {
            gridStrings.append(" ");
        }
        gridStrings.append(boardTitle).append("\n").append(" ---------------------\n");

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 && j == 0) gridStrings.append("   ");
                if (i == 0) gridStrings.append(j).append(" ");
                if (j == 0 && i != 0) gridStrings.append(" ").append(chars.charAt(i - 1)).append(" ");
                if (i > 0) {
                    String cellString = grd[i - 1][j].getCellTypeString();
                    // Replace boats with water if enemy board
                    if (isEnemy && !("X.".contains(cellString))) cellString = "~";
                    // Most recent shot is coloured red
                    if (i - 1 == lastRowShot && j == lastColShot) {
                        cellString = "<span style='color:red'>" + cellString + "</span>";
                    }
                    gridStrings.append(cellString).append(" ");
                }
            }
            if (i < 10) gridStrings.append("\n");
        }
        // Put string into HTML format
        return "<html><body>"
                + gridStrings.toString().replace(" ", "&nbsp;").replace("\n", "<br>")
                + "</body></html>";
    }

    //TODO: remove this function if not needed.
    public String showEnemyBoard(String boardTitle) {
        StringBuilder gridStrings = new StringBuilder();
        String chars = "ABCDEFGHIJ";
        Cell[][] grd = getGrid();

        // Character width of the board
        int strLength = 24;
        int titleSpace = round((float) (strLength - boardTitle.length()) / 2);

        // Adds space to the start of the title to centre the text
        for (int i = 0; i < titleSpace; i++) {
            gridStrings.append(" ");
        }
        gridStrings.append(boardTitle).append("\n").append(" ---------------------\n");

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 && j == 0) gridStrings.append("   ");
                if (i == 0) gridStrings.append(j).append(" ");
                if (j == 0 && i != 0) gridStrings.append(" ").append(chars.charAt(i - 1)).append(" ");
                if (i > 0) {
                    String cellString = grd[i - 1][j].getCellTypeString();
                    if (cellString.equals(".") || cellString.equals("X")) {
                        if (i - 1 == lastColShot && j == lastRowShot) {
                            cellString = "<span style='color:red'>" + cellString + "</span>";
                        }
                        gridStrings.append(cellString).append(" ");
                    } else {
                        gridStrings.append("~ ");
                    }
                }
            }
            if (i < 10) gridStrings.append("\n");
        }
        // Put string into HTML format
        return "<html><body>"
                + gridStrings.toString().replace(" ", "&nbsp;").replace("\n", "<br>")
                + "</body></html>";
    }

    /**
     * Call the grid's default grid creator
     *
     * @return A 2D array of cells
     */
    public Cell[][] defaultGridCreator() {
        return this.grid.defaultGridCreator();
    }
}
