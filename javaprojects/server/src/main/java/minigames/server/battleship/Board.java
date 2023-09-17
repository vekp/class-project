package minigames.server.battleship;

import java.util.*;

import static java.lang.Math.round;

/**
 * The Board Class contains all the information about the current state of a player's game board, including the player's name
 */
public class Board {

    // Fields
    private Grid grid; // A two-dimensional array of Cells for drawing the game board
    private HashMap<String, Ship> vessels;  // A hashmap containing each of the five ship types for the current board
    private int turnNumber;  // The current turn number
    private int shipSelected;

    private GameState gameState;

    // Constructor

    /**
     * The Constructor takes only the player's name as a parameter
     * @param choice integer used to choose the default board - For now
     */
    public Board(int choice){
        this.turnNumber = 0;
        this.vessels = new HashMap<>();
        this.grid = new Grid(); // Create a default grid
        this.gameState = GameState.SHIP_PLACEMENT;
        this.shipSelected = 0;
        chooseGrid(choice);
        // Set the player to be the owner for all ships on this board
        this.setPlayerOwner();

    }

    // Getters

    /**
     * Return the 2D string array to display in the client window
     * @return The 2D String Array in its current state
     */
    public Cell[][] getGrid() {
        System.out.println(this.gameState.toString());
        if (this.gameState == GameState.SHIP_PLACEMENT) {
            this.grid.generateGrid(this.vessels);
        }
        return this.grid.getGrid();
    }

    /**
     * Getter for the current turn number
     * @return An int for the current turn number
     */
    public int getTurnNumber() {return this.turnNumber;}

    /**
     * Getter for the player's current game state
     * @return enum game state value
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * TODO
     * @return
     */
    public HashMap<String, Ship> getVessels() {
        return vessels;
    }

    /**
     * Return the ship object of the specified class on the current game board
     * @param shipClass A String containing the class of the ship
     * @return
     */
    public Ship getShip(String shipClass){return this.vessels.get(shipClass);}

    /**
     * TODO
     * @return
     */
    public int getShipSelected() {
        return shipSelected;
    }

    // Setters

    /**
     * TODO
     */
    public void setShipSelected() {
        if (this.shipSelected < 4) {
            this.shipSelected++;
        } else {
            this.shipSelected = 0;
        }
    }

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
            // current.setOwner(this.playerName);
            vessels.replace(key, current);
        });
    }

    /**
     * Sets the CellType for a given coordinate in the grid
     * @param col horizontal position
     * @param row vertical position
     * @param cellType enum value representing the cell state changing to
     */
    public void setGridCell(int col, int row, CellType cellType){
        this.grid.setCellType(col, row, cellType);
        // if the cell is being changed to a "hit" or "miss" cell, update the cell's shotAt boolean to be true
        // This function is also used to update the grid when placing ships, it was causing issues because all parts of the
        // ships were being read as "shotAt"
        if(cellType.toString().equals("X") || cellType.toString().equals(".")) {
            this.grid.shootCell(col, row);
        }
    }

    /**
     * Increments the turn number
     */
    public void incrementTurnNumber() {
        this.turnNumber++;
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

    public static String showEnemyBoard(String boardTitle, Cell[][] grid) {
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
                    if(grid[i-1][j].getCellTypeString().equals(".") || grid[i-1][j].getCellTypeString().equals("X")){
                        gridStrings.append(grid[i-1][j].getCellTypeString()).append(" ");
                    } else {
                        gridStrings.append("~ ");
                    }

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

    public Cell[][] chooseGrid(int choice){
        if (choice == 1){
            return otherGrid();
        } else {
            return defaultGrid();
        }
    }

    /**
     * Set the vessels map, and place ships on the grid in a default position
     * @return a 2D cell array
     */
    public Cell[][] defaultGrid() {
        this.vessels = new HashMap<>(this.grid.defaultShips());
        return this.getGrid();
    }

    public Cell[][] otherGrid(){
        this.vessels = new HashMap<>(this.grid.defaultShips1());
        return this.getGrid();
    }

//    public void moveShip() {
//        this.setVessels(this.grid.customShip(this.vessels));
//    };

    /**
     * Method to get the ship corresponding to the coordinate input
     * @param target cell used to grab coordinates from (new cell created when passed in with the user's x,y coord)
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
        return new Ship("", new Cell[0], 0,0, true);
    }

    /**
     * TODO
     * @param shipTitle
     * @param shipType
     * @param row
     * @param col
     * @param horizontal
     * @param exShips
     * @return
     */
    public HashMap<String,Ship> customShip(String shipTitle, int shipType, int row, int col, boolean horizontal, HashMap<String,Ship> exShips){
        // Map of ships
        HashMap<String, Ship> vessels = new HashMap<>(exShips);
        // Replace ship in the map
        vessels.replace(shipTitle, this.grid.createShip(shipType, row, col, horizontal));

        return vessels;
    }

}
