package minigames.server.battleship;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void placeDefaultConvoy() {
        this.grid = new String[][]{
                {" ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"},
                {"A", "^", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
                {"B", "0", "~", "<", "0", ">", "~", "~", "~", "~", "~"},
                {"C", "0", "~", "~", "~", "~", "<", "0", "0", ">", "~"},
                {"D", "0", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
                {"E", "0", "~", "~", "^", "~", "~", "~", "~", "~", "~"},
                {"F", "V", "~", "~", "0", "~", "~", "~", "~", "~", "~"},
                {"G", "~", "~", "~", "0", "~", "~", "~", "~", "~", "~"},
                {"H", "~", "~", "~", "V", "~", "~", "~", "~", "~", "~"},
                {"I", "~", "~", "~", "~", "~", "~", "~", "~", "~", "~"},
                {"J", "~", "~", "~", "~", "~", "<", "0", "0", "0", ">"}
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
