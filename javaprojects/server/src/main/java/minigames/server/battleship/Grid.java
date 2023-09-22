package minigames.server.battleship;

import java.util.HashMap;

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
//        grid = placeDefaultConvoy(grid);
        return grid;
    }

    /**
     *
     * @return
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

//    /**
//     * Function to place ship on the grid and return a Ship object
//     * @param vesselClass string value describing the ship class
//     * @param parts vertical or horizontal list of ship parts
//     * @param size size of ship, eg Carrier = 6
//     * @param col vertical location in grid
//     * @param row horizontal location in grid
//     * @return Ship object
//     */
//    public Ship placeShip(String vesselClass, CellType[] parts, int size, int col, int row) {
//        Cell[] newShipParts = new Cell[size];
//
//        for (int i=0; i<size; i++) {
//            newShipParts[i] = new Cell(row, col);
//        }
//
//        // If horizontal
//        if (parts[0].equals(CellType.SHIP_LEFT)) {
//            if (row + parts.length < 10) {
//                this.grid[col][row].setCellType(parts[0]);
//                newShipParts[0].setCellType(parts[0]);
//                for (int i = 1; i < size; i++) {
//                    if (i<size-1) {
//                        this.grid[col][row + i].setCellType(parts[1]);
//                        newShipParts[i].setCellType(parts[1]);
//                        newShipParts[i].setHorizontalCoord(row + i);
//                        newShipParts[i].setVerticalCoord(col);
//                    } else {
//                        this.grid[col][row + i].setCellType(parts[2]);
//                        newShipParts[i].setCellType(parts[2]);
//                        newShipParts[i].setHorizontalCoord(row + i);
//                        newShipParts[i].setVerticalCoord(col);
//                    }
//                }
//            }
//        }
//
//        // If vertical
//        if (parts[0].equals(CellType.SHIP_UP)) {
//            if (col + parts.length < 10) {
//                this.grid[col][row].setCellType(parts[0]);
//                newShipParts[0].setCellType(parts[0]);
//                for (int i = 1; i < size; i++) {
//                    if (i<size-1) {
//                        this.grid[col + i][row].setCellType(parts[1]);
//                        newShipParts[i].setCellType(parts[1]);
//                        newShipParts[i].setHorizontalCoord(row);
//                        newShipParts[i].setVerticalCoord(col + i);
//                    } else {
//                        this.grid[col + i][row].setCellType(parts[2]);
//                        newShipParts[i].setCellType(parts[2]);
//                        newShipParts[i].setHorizontalCoord(row);
//                        newShipParts[i].setVerticalCoord(col + i);
//                    }
//                }
//            }
//        }
//
//
//        return new Ship(vesselClass, newShipParts, row, col);
//    }

    public Ship createShip(int shipType, int col, int row, boolean horizontal) {
        // Ship Classes
        String[] shipClass = {"Carrier", "Battleship", "Destroyer", "Submarine", "Patrol Boat"};
        int[] shipSizes = {6, 5, 4, 4, 3};
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
     * Function to add ships in a default configuration to a hashmap which is returned
     * @return a hashmap containing Ship objects
     */
    public HashMap<String,Ship> defaultShips() {

        // Map of ships
        HashMap<String, Ship> vessels = new HashMap<>();
        vessels.put("Carrier",     createShip(0, 0, 0, true));
        vessels.put("Battleship",  createShip(1, 9, 1, false));
        vessels.put("Destroyer",   createShip(2, 0, 4, false));
        vessels.put("Submarine",   createShip(3, 3, 9, true));
        vessels.put("Patrol Boat", createShip(4, 4, 5, true));

        // Place ships on the grid
        generateGrid(vessels);

        return vessels;
    }


    public HashMap<String,Ship> defaultShips1(){
//        // Ship parts
//        CellType[] hor = {CellType.SHIP_LEFT,CellType.SHIP_HULL, CellType.SHIP_RIGHT};
//        CellType[] ver = {CellType.SHIP_UP,CellType.SHIP_HULL, CellType.SHIP_DOWN};
//        // Map of ships
//        HashMap<String, Ship> vessels = new HashMap<>();
//        vessels.put("Carrier", placeShip("Carrier", ver, 6,  2, 9));
//        vessels.put("Battleship", placeShip("Battleship", hor, 5,  0, 3));
//        vessels.put("Destroyer", placeShip("Destroyer", hor, 4,  2, 1));
//        vessels.put("Submarine", placeShip("Submarine", ver, 4,  4, 8));
//        vessels.put("Patrol Boat", placeShip("Patrol Boat", hor, 3,  5, 4));
//        return vessels;

        // Map of ships
        HashMap<String, Ship> vessels = new HashMap<>();
        vessels.put("Carrier",     createShip(0, 0, 2, true));
        vessels.put("Battleship",  createShip(1, 8, 1, false));
        vessels.put("Destroyer",   createShip(2, 1, 4, false));
        vessels.put("Submarine",   createShip(3, 3, 8, true));
        vessels.put("Patrol Boat", createShip(4, 4, 5, true));

        // Place ships on the grid
        generateGrid(vessels);

        return vessels;
    }

    /**
     * TODO
     */
    public void wipe() {
        for (Cell[] group: this.grid) {
            for (Cell cell: group) {
                cell.setCellType(CellType.OCEAN);
            }
        }
    }

    /**
     * TODO
     * @param ships
     */
    public void generateGrid(HashMap<String,Ship> ships) {
        // Reset the grid
        wipe();

        // Place ships on the grid
        ships.forEach((key, value) ->{
            for (Cell cell: value.getShipParts()) {
                grid[cell.getVerticalCoordInt()][cell.getHorizontalCoord()].setCellType(cell.getCellType());
            }
        });
    }

}
