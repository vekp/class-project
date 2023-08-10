package minigames.server.battleship;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                grid[i][j] = new Cell();
            }
        }
        grid = placeDefaultConvoy(grid);
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
    public boolean placeShip(String coordinates, String orientation){
        // The first chunk of code deals with validating the user's input
        // Regex to check that the coordinate string is valid
        String regex = "^[A-J][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        //convert the coordinates to uppercase
        coordinates = coordinates.toUpperCase();
        Matcher matcher = pattern.matcher(coordinates);
        // If the coordinates don't match then return false
        if(!matcher.matches()){
            return false;
        }
        // Force the character to be uppercase
        orientation = orientation.toUpperCase();
        // If the orientation
        if(orientation != "V" && orientation != "H"){
            return false;
        }
        // TODO Finish this manual placement function off later, for now just use a default placement function
        return true;
    }

    /**
     * For now, I'm literally just putting in a default ship placement lazily, it will be changed later
     */
    public Cell[][] placeDefaultConvoy(Cell[][] grid) {
        // The Board should perhaps just contain the 2D array of cells and nothing more so that functions like this one
        // can be called upon other grid objects
        this.grid = grid;

        // Create the cells
        // TODO: See if there is a less redundant way to do this
        Cell top = new Cell();
        top.setCellType(CellType.SHIP_UP);
        Cell bottom = new Cell();
        bottom.setCellType(CellType.SHIP_DOWN);
        Cell right = new Cell();
        right.setCellType(CellType.SHIP_RIGHT);
        Cell left = new Cell();
        left.setCellType(CellType.SHIP_LEFT);
        Cell mid = new Cell();
        mid.setCellType(CellType.SHIP_HULL);
        // TODO: See if there is a less redundant way to do this too
        // Create the carrier
        this.setCell(1, 1, top);
        for(int i = 2; i < 6; i++){
            this.setCell(1, i, mid);
        }
        this.setCell(1, 6, bottom);

        // Create the Battleship
        this.setCell(2, 2, top);
        for(int i = 3; i < 6; i++){
            this.setCell(2, i, mid);
        }
        this.setCell(2,6,bottom);

        // Create the Submarine
        this.setCell(4,5, top);
        for(int i = 6; i < 8; i++){
            this.setCell(4, i, mid);
        }
        this.setCell(4, 8, bottom);

        // Create the Destroyer
        this.setCell(6, 6, left);
        for(int i = 7; i < 9; i++){
            this.setCell(i, 6, mid);
        }
        this.setCell(9, 6, right);

        // And finally the Patrol Boat
        this.setCell(4, 2, left);
        this.setCell(5, 2, mid);
        this.setCell(6, 2, right);
        return this.grid;
//        this.grid = new String[][]{
//                {" ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"},
//                {"A", "^", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
//                {"B", "0", "~", "<", "0", ">", "~", "~", "~", "~", "~"},
//                {"C", "0", "~", "~", "~", "~", "<", "0", "0", ">", "~"},
//                {"D", "0", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
//                {"E", "0", "~", "~", "^", "~", "~", "~", "~", "~", "~"},
//                {"F", "V", "~", "~", "0", "~", "~", "~", "~", "~", "~"},
//                {"G", "~", "~", "~", "0", "~", "~", "~", "~", "~", "~"},
//                {"H", "~", "~", "~", "V", "~", "~", "~", "~", "~", "~"},
//                {"I", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
//                {"J", "~", "~", "~", "~", "~", "<", "0", "0", "0", ">"}
//        };
    }
    /**
     * Set the Cell at a specific x and y coordinate
     * @param y
     * @param x
     * @param cell
     */
    public void setCell(int x, int y, Cell cell){
        this.grid[y][x - 1] = cell;
    }
}
