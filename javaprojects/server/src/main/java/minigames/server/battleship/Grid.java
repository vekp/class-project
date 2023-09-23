package minigames.server.battleship;

import java.util.HashMap;
import java.util.Random;

/**
 * The Grid Class contains a 2D array containing Cell items
 */
public class Grid {
    private Cell[][] grid;

    public Grid(){
        this.grid = defaultGridCreator();
    }

    /**
     * Create a default grid of Cells (moved from the Board Class, originally Mitch's Code)
     * @return 2D array of Cells
     */
    public Cell[][] defaultGridCreator() {
        Cell[][] grid = new Cell[10][10];
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        return grid;
    }

    /**
     * Function to return the grid
     * @return 2D Cell array
     */
    public Cell[][] getGrid() {
        return this.grid;
    }

    /**
     *
     * @param row
     * @param col
     */
    public void shootCell(int row, int col){
        this.grid[row][col].shoot();
    }

    /**
     * Function to set a particular cell's CellType in the grid
     * @param row row index
     * @param col column index
     * @param cellType enum value to set the Cell's parameter to
     */
    public void setCellType(int row, int col, CellType cellType){
        this.grid[row][col].setCellType(cellType);
    }

    /**
     * Function to create a new ship object
     * Ships are constructed from the top left,
     * @param shipType int representing class of ship eg. Carrier
     * @param col int representing horizontal location
     * @param row int representing vertical location
     * @param horizontal boolean determining if ship should be created horizontally
     * @return new Ship object
     */
    public Ship createShip(int shipType, int col, int row, boolean horizontal) {
        // Ship Classes
        String[] shipClass = {"Carrier", "Battleship", "Destroyer", "Submarine", "Patrol Boat"};
        int[] shipSizes = {6, 5, 4, 4, 3};

        // Size of ship
        int size = shipSizes[shipType];

        // Ship parts
        CellType[] hor = {CellType.SHIP_LEFT, CellType.SHIP_HULL, CellType.SHIP_RIGHT};
        CellType[] ver = {CellType.SHIP_UP, CellType.SHIP_HULL, CellType.SHIP_DOWN};

        // Create array for the new ship parts
        Cell[] parts = new Cell[size];
        for (int i=0; i<size; i++) {
            parts[i] = new Cell(col, row);
        }

        // Set parts - location is validated beforehand
        parts[0].setCellType((horizontal)? hor[0] : ver[0]);
        for (int i=1; i<size; i++) {
            if (i<size-1) {
                parts[i].setCellType((horizontal)? hor[1] : ver[1]);
            } else {
                parts[i].setCellType((horizontal)? hor[2] : ver[2]);
            }
            parts[i].setHorizontalCoord((horizontal)? col + i: col);
            parts[i].setVerticalCoord((horizontal)? row: row + i);
        }

        return new Ship(shipClass[shipType], shipType, parts, col, row, horizontal);
    }

    /**
     * Function to create and add ships in a random default configuration to a hashmap which is returned
     * @return a hashmap containing Ship objects
     */
    public HashMap<String,Ship> defaultShips() {

        // Random ship layout selection
        Random rand = new Random();
        int option = rand.nextInt(0, 4);

        // Override for specific option:
        // option = 3;

        // Map of ships
        HashMap<String, Ship> vessels = new HashMap<>();

        // Ship layout options
        switch (option) {
            case 0 -> {
                // Best looking if you want to set option for images??
                vessels.put("Carrier", createShip(0, 0, 2, true));
                vessels.put("Battleship", createShip(1, 8, 1, false));
                vessels.put("Destroyer", createShip(2, 1, 4, false));
                vessels.put("Submarine", createShip(3, 3, 8, true));
                vessels.put("Patrol Boat", createShip(4, 4, 5, true));
            }
            case 1 -> {
                vessels.put("Carrier", createShip(0, 2, 4, true));
                vessels.put("Battleship", createShip(1, 9, 3, false));
                vessels.put("Destroyer", createShip(2, 1, 1, true));
                vessels.put("Submarine", createShip(3, 5, 6, false));
                vessels.put("Patrol Boat", createShip(4, 1, 7, true));
            }
            case 2 -> {
                vessels.put("Carrier", createShip(0, 8, 1, false));
                vessels.put("Battleship", createShip(1, 3, 2, true));
                vessels.put("Destroyer", createShip(2, 1, 4, false));
                vessels.put("Submarine", createShip(3, 5, 9, true));
                vessels.put("Patrol Boat", createShip(4, 2, 4, true));
            }
            case 3 -> {
                // Trolling
                vessels.put("Carrier", createShip(0, 3, 2, false));
                vessels.put("Battleship", createShip(1, 3, 1, true));
                vessels.put("Destroyer", createShip(2, 2, 1, false));
                vessels.put("Submarine", createShip(3, 2, 5, false));
                vessels.put("Patrol Boat", createShip(4, 4, 4, true));
            }
            case 4 -> {
                vessels.put("Carrier", createShip(0, 2, 4, false));
                vessels.put("Battleship", createShip(1, 8, 4, false));
                vessels.put("Destroyer", createShip(2, 5, 2, false));
                vessels.put("Submarine", createShip(3, 6, 0, true));
                vessels.put("Patrol Boat", createShip(4, 0, 1, true));
            }
        }

        // Place ships on the grid
        generateGrid(vessels);

        return vessels;
    }

    /**
     * Cleans a board by setting all cells to OCEAN
     */
    public void wipe() {
        for (Cell[] group: this.grid) {
            for (Cell cell: group) {
                cell.setCellType(CellType.OCEAN);
            }
        }
    }

    /**
     * Places each ship in the map passed in onto the grid
     * @param ships hashmap of ship objects
     */
    public void generateGrid(HashMap<String,Ship> ships) {
        // Reset the grid
        wipe();

        // Place ships on the grid
        ships.forEach((key, value) ->{
            for (Cell cell: value.getShipParts()) {
                this.grid[cell.getVerticalCoordInt()][cell.getHorizontalCoord()].setCellType(cell.getCellType());
            }
        });
    }

}
