package minigames.server.battleship;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.Math.round;

/**
 * The Board Class contains all the information about the current state of a player's game board, including the player's name
 */
public class Board {
    private String playerName;
    private String boardTitle;
    private Cell[][] grid; // A two-dimensional array of Cells for drawing the game board
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
        this.grid = defaultGridCreator();
    }

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
     * Note, this does not work... - CRAIG
     */
    public Cell[][] placeDefaultConvoy() {
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
        this.setCell(6, 10, left);
        for(int i = 7; i < 10; i++){
            this.setCell(i, 10, mid);
        }
        this.setCell(10,10,right);

        // Create the Submarine
        this.setCell(4,5, top);
        for(int i = 6; i < 8; i++){
            this.setCell(4, i, mid);
        }
        this.setCell(4, 8, bottom);

        // Create the Destroyer
        this.setCell(6, 3, left);
        for(int i = 7; i < 9; i++){
            this.setCell(6, i, mid);
        }
        this.setCell(10, 3, right);

        // And finally the Patrol Boat
        this.setCell(4, 2, left);
        this.setCell(5, 2, mid);
        this.setCell(6, 2, mid);
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

    public void setCell(int x, int y, Cell cell){
        this.grid[x][y] = cell;
    }

    /**
     * Return the 2D string array to display in the client window
     * @return The 2D String Array in its current state
     */
    public Cell[][] getGrid(){return this.grid;}



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
     * Function to create a grid of strings to be displayed
     * @param boardTitle
     * @param grid
     * @return
     */
    public static String createGrid(String boardTitle, Cell[][] grid) {
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
     * Create a default grid of Cells
     * @return 2D array of Cells
     */
    public Cell[][] defaultGridCreator() {
        Cell[][] grid = new Cell[10][10];
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                grid[i][j] = new Cell();
            }
        }
        return grid;
    }

}
