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

    public Cell[][] getGrid(){return this.grid;}

    /**
     * This function is responsible for placing Ships on the game board. It will check whether the ship can be placed
     * and will return false if it cannot. If the placement is valid an appropriate Ship Object will be created and added
     * to the vessels array, and the relevant grid cells will be updated
     * @param coordinates The raw coordinate string that the user has entered into the console
     * @param orientation The raw String that the user has entered into the console
     * @return true if the placement is valid, false if not
     */
//    public boolean placeShip(String coordinates, String orientation){
//        // The first chunk of code deals with validating the user's input
//        // Regex to check that the coordinate string is valid
//        String regex = "^[A-J][0-9]$";
//        Pattern pattern = Pattern.compile(regex);
//        //convert the coordinates to uppercase
//        coordinates = coordinates.toUpperCase();
//        Matcher matcher = pattern.matcher(coordinates);
//        // If the coordinates don't match then return false
//        if(!matcher.matches()){
//            return false;
//        }
//        // Force the character to be uppercase
//        orientation = orientation.toUpperCase();
//        // If the orientation
//        if(orientation != "V" && orientation != "H"){
//            return false;
//        }
//        // TODO Finish this manual placement function off later, for now just use a default placement function
//        return true;
//    }

//    /**
//     * For now, I'm literally just putting in a default ship placement lazily, it will be changed later
//     */
//    public Cell[][] placeDefaultConvoy(Cell[][] grid) {
//        // The Board should perhaps just contain the 2D array of cells and nothing more so that functions like this one
//        // can be called upon other grid objects
//        this.grid = grid;
//
//        // Create the cells
//        // TODO: See if there is a less redundant way to do this
//        Cell top = new Cell(horizontalCoord, verticalCoord);
//        top.setCellType(CellType.SHIP_UP);
//        Cell bottom = new Cell(horizontalCoord, verticalCoord);
//        bottom.setCellType(CellType.SHIP_DOWN);
//        Cell right = new Cell(horizontalCoord, verticalCoord);
//        right.setCellType(CellType.SHIP_RIGHT);
//        Cell left = new Cell(horizontalCoord, verticalCoord);
//        left.setCellType(CellType.SHIP_LEFT);
//        Cell mid = new Cell(horizontalCoord, verticalCoord);
//        mid.setCellType(CellType.SHIP_HULL);
//        // TODO: See if there is a less redundant way to do this too
//        // Create the carrier
//        this.setCell(1, 1, top);
//        for(int i = 2; i < 6; i++){
//            this.setCell(1, i, mid);
//        }
//        this.setCell(1, 6, bottom);
//
//        // Create the Battleship
//        this.setCell(2, 2, top);
//        for(int i = 3; i < 6; i++){
//            this.setCell(2, i, mid);
//        }
//        this.setCell(2,6,bottom);
//
//        // Create the Submarine
//        this.setCell(4,5, top);
//        for(int i = 6; i < 8; i++){
//            this.setCell(4, i, mid);
//        }
//        this.setCell(4, 8, bottom);
//
//        // Create the Destroyer
//        this.setCell(6, 6, left);
//        for(int i = 7; i < 9; i++){
//            this.setCell(i, 6, mid);
//        }
//        this.setCell(9, 6, right);
//
//        // And finally the Patrol Boat
//        this.setCell(4, 2, left);
//        this.setCell(5, 2, mid);
//        this.setCell(6, 2, right);
//        return this.grid;
////        this.grid = new String[][]{
////                {" ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"},
////                {"A", "^", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
////                {"B", "0", "~", "<", "0", ">", "~", "~", "~", "~", "~"},
////                {"C", "0", "~", "~", "~", "~", "<", "0", "0", ">", "~"},
////                {"D", "0", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
////                {"E", "0", "~", "~", "^", "~", "~", "~", "~", "~", "~"},
////                {"F", "V", "~", "~", "0", "~", "~", "~", "~", "~", "~"},
////                {"G", "~", "~", "~", "0", "~", "~", "~", "~", "~", "~"},
////                {"H", "~", "~", "~", "V", "~", "~", "~", "~", "~", "~"},
////                {"I", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
////                {"J", "~", "~", "~", "~", "~", "<", "0", "0", "0", ">"}
////        };
//    }
    /**
     * Set the Cell at a specific x and y coordinate
     * @param y
     * @param x
     * @param cell
     */
    public void setCell(int x, int y, Cell cell){
        this.grid[y][x - 1] = cell;
    }

    public void shootCell(int x, int y){
        this.grid[y][x - 1].shoot();
    }

    /**
     * Function to set a particular cell's CellType in the grid
     * @param x x index
     * @param y y index
     * @param cellType enum value to set the Cell's parameter to
     */
    public void setCellType(int x, int y, CellType cellType){
        this.grid[x][y].setCellType(cellType);
    }

    /**
     * Function to place ship on the grid and return a Ship object
     * @param vesselClass string value describing the ship class
     * @param parts vertical or horizontal list of ship parts
     * @param size size of ship, eg Carrier = 6
     * @param y vertical location in grid
     * @param x horizontal location in grid
     * @return Ship object
     */
    public Ship placeShip(String vesselClass, CellType[] parts, int size, int y, int x) {
        Cell[] newShipParts = new Cell[size];

        for (int i=0; i<size; i++) {
            newShipParts[i] = new Cell(x, y);
        }

        // If horizontal
        if (parts[0].equals(CellType.SHIP_LEFT)) {
            if (x + parts.length < 10) {
                this.grid[y][x].setCellType(parts[0]);
                newShipParts[0].setCellType(parts[0]);
                for (int i = 1; i < size; i++) {
                    if (i<size-1) {
                        this.grid[y][x + i].setCellType(parts[1]);
                        newShipParts[i].setCellType(parts[1]);
                        newShipParts[i].setHorizontalCoord(x + i);
                        newShipParts[i].setVerticalCoord(y);
                    } else {
                        this.grid[y][x + i].setCellType(parts[2]);
                        newShipParts[i].setCellType(parts[2]);
                        newShipParts[i].setHorizontalCoord(x + i);
                        newShipParts[i].setVerticalCoord(y);
                    }
                }
            }
        }

        // If vertical
        if (parts[0].equals(CellType.SHIP_UP)) {
            if (y + parts.length < 10) {
                this.grid[y][x].setCellType(parts[0]);
                newShipParts[0].setCellType(parts[0]);
                for (int i = 1; i < size; i++) {
                    if (i<size-1) {
                        this.grid[y + i][x].setCellType(parts[1]);
                        newShipParts[i].setCellType(parts[1]);
                        newShipParts[i].setHorizontalCoord(x);
                        newShipParts[i].setVerticalCoord(y + i);
                    } else {
                        this.grid[y + i][x].setCellType(parts[2]);
                        newShipParts[i].setCellType(parts[2]);
                        newShipParts[i].setHorizontalCoord(x);
                        newShipParts[i].setVerticalCoord(y + i);
                    }
                }
            }
        }
//        System.out.println("Ship: "+vesselClass);
//        for (Cell cell:
//                newShipParts) {
//            System.out.print(cell.getVerticalCoordString()+cell.getHorizontalCoord()+"  ");
//        }
//        System.out.println(" ");
//        for (Cell cell:
//                newShipParts) {
//            System.out.print(cell.getCellType()+"   ");
//        }
//        System.out.println("\n");

        return new Ship(vesselClass, newShipParts);
    }

    /**
     * Function to place ships in a default configuration and add each ship to a hashmap which is returned
     * @return a hashmap containing Ship objects
     */
    public HashMap<String,Ship> defaultShips() {
        // Ship parts
        CellType[] hor = {CellType.SHIP_LEFT,CellType.SHIP_HULL, CellType.SHIP_RIGHT};
        CellType[] ver = {CellType.SHIP_UP,CellType.SHIP_HULL, CellType.SHIP_DOWN};
        // Map of ships
        HashMap<String, Ship> vessels = new HashMap<>();
        vessels.put("Carrier", placeShip("Carrier", hor, 6,  2, 0));
        vessels.put("Battleship", placeShip("Battleship", ver, 5,  1, 8));
        vessels.put("Destroyer", placeShip("Destroyer", ver, 4,  4, 1));
        vessels.put("Submarine", placeShip("Submarine", hor, 4,  8, 3));
        vessels.put("Patrol Boat", placeShip("Patrol Boat", hor, 3,  5, 4));
        return vessels;
    }
}
