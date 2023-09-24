package minigames.server.battleship;

import minigames.server.achievements.AchievementHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static minigames.server.battleship.achievements.*;

public class BattleshipPlayer {

    // Fields
    private String name;
    private boolean controlledByPlayer;
    private Board playerBoard;
    private String messageHistory;  // String of all valid messages, both game and player
    private boolean historyDirty; //whether the history has been changed since the game last asked for it
    private boolean ready;
    String chars = "ABCDEFGHIJ";

    // Constructor

    /**
     * Constructor to create a BattleshipPlayer
     * @param name String value of a player's name
     * @param playerBoard Board object representing the player's grid
     * @param controlledByPlayer boolean value representing if they are a human player
     */
    public BattleshipPlayer(String name, Board playerBoard, boolean controlledByPlayer, String messageHistory) {
        this.name = name;
        this.playerBoard = playerBoard;
        this.controlledByPlayer = controlledByPlayer;
        this.messageHistory = messageHistory;
        historyDirty = true;
        this.ready = false;
    }

    // Methods

    /**
     * Method to process a turn for an AI player
     * @param opponent the other player's board
     * @return BattleshipTurnResult containing a boolean for successful input, whether a ship was hit, and a message
     */
    public BattleshipTurnResult processAITurn(Board opponent){
        return processTurn(generateCoordinate(), opponent);
    }

    /**
     * Method to determine if the player's input was valid, then format the input and update the player's message history.
     * Then determines the result of shooting the other player's board at that coordinate
     * @param input player's input
     * @param opponent other player's board
     * @return BattleshipTurnResult containing a boolean for successful input, whether a ship was hit, and a message
     */
    public BattleshipTurnResult processTurn(String input, Board opponent) {
        // Check input entered is a coordinate
        if (!validateInput(input)) {
            return BattleshipTurnResult.invalidInput(input);
        }
        // Get formatted input and split it to be passed into shotOutcome()
        String[] validatedInput = formatInput(input).split(",");

        int row = BattleshipGame.chars.indexOf(validatedInput[0]);
        int col = -1;
        // This technically should not need error handling as only valid input can get to this stage - precautionary
        try {
            col = Integer.parseInt(validatedInput[1]);
        } catch (NumberFormatException e) {
            return BattleshipTurnResult.invalidInput(input);
        }

        // Add input to the message history
        updateHistory(formatInput(input));

        opponent.setLastShot(row, col);

        return shotOutcome(opponent, row, col);
    }

    /**
     * Method to determine the result of shooting at a coordinate and returns a BattleshipTurnResult containing
     * messages for both players depending on the outcome of shooting that grid cell.
     *
     * @param opponent enemy players board that you shot at
     * @param row      vertical location index
     * @param col      horizontal location index
     * @return BattleshipTurnResult containing messages, status info for successful (valid input), ship hit and ship sunk
     */
    private BattleshipTurnResult shotOutcome(Board opponent, int row, int col) {
        String input = formatInput(chars.charAt(row)+""+ col);
        // Get players current grid
        Cell[][] grid = opponent.getGrid();

        // Get cell type of player's coordinate
        CellType currentState = grid[row][col].getCellType();
        // If player hit ocean set CellType to Miss and return false
        if (currentState.equals(CellType.OCEAN)) {
            opponent.getGrid()[row][col].shoot();
            return BattleshipTurnResult.missTarget(input);
            // If the Cell is a MISS cell, return false and check for Slow Learner Achievement
        } else if (currentState.equals(CellType.MISS)) {
            //no need to check for already unlocked as handler will do that
            System.out.println("Slow Learner Achievement - Requirements met for " + getName());
            AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
            handler.unlockAchievement(this.getName(), SLOW_LEARNER.toString());
            return BattleshipTurnResult.missTarget(input);
        } else if (currentState.equals(CellType.HIT)) {
            System.out.println("You Got Him Achievement - Requirements met for " + getName());
            AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
            handler.unlockAchievement(this.getName(), YOU_GOT_HIM.toString());
            return BattleshipTurnResult.alreadyHitCell(input);
        } else {
            // If the cell is not an ocean, miss, or hit cell, set the cell to a "hit"
            opponent.getGrid()[row][col].shoot();

            // Update the ships to include the hit in their cells
            HashMap<String, Ship> vessels = opponent.getVessels();
            vessels.forEach((key, value) ->{
                Ship current = value;
                current.updateShipStatus(row, col, getName());
                vessels.replace(key, current);
            });
            opponent.setVessels(vessels);

            // Get the ship shot at
            Ship ship = opponent.getVessel(new Cell(col, row), opponent.getVessels());

            return BattleshipTurnResult.hitTarget(input, ship.isJustSunk());
        }
    }

    /**
     * Format the user input for better readability
     *
     * @param input String input from the console
     * @return formatted string
     */
    private String formatInput(String input) {
        input = input.toUpperCase();
        String a = String.valueOf(input.charAt(0));
        String b = String.valueOf(input.charAt(1));
        return a + "," + b;
    }

    /**
     * This function is responsible for validating user input
     *
     * @param input The raw coordinate string that the user has entered into the console
     * @return true if the coordinate is valid, false if not
     */
    private boolean validateInput(String input) {
        // Make input case in-sensitive
        input = input.toUpperCase();
        // If the player enters C120 as the coordinates give them the COSC120 inside joke achievement
        if (input.equals("C120")) {
            AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
            handler.unlockAchievement(this.getName(), C_120.toString());
        }

        // Debug commands for testing achievements and states - Craig
        if (input.equals("ROSEBUD")){
            playerBoard.sinkAll(this.getName());
        }

        // Craig's code split off and moved here by Mitch
        // Regex to check that the coordinate string is valid
        String regex = "^[A-J][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        // Convert the coordinates to uppercase
        input = input.toUpperCase();
        Matcher matcher = pattern.matcher(input);
        // If the coordinates don't match it will return false
        return matcher.matches();
    }

    /**
     * Method to generate a coordinate for an AI player
     * @return formatted coordinate String
     */
    private String generateCoordinate() {

        // Generate random coordinates

        // Pass in the opposing grid where this function is called, get the last shot at cell and check if it was a hit
        // if so, pick a coordinate relative to that location. If last shot at was a list of the past 2 locations it
        // would make this a lot better in terms of picking the direction of the ship

        Random rand = new Random();
        int randX = rand.nextInt(10);
        int randY = rand.nextInt(10);
        String cpuCoordStr = chars.charAt(randX) + "" + randY;
        System.out.println(cpuCoordStr);
        return cpuCoordStr;
    }

    // This is supposed to check if a ship with the corresponding movement added is inside the grid
    public boolean isInsideGrid(Ship shipIn, int colMov, int rowMov, boolean rotate) {
        // Get selected ship
        String[] ships = this.getBoard().getVessels().keySet().toArray(new String[0]);
        for (String s:ships) {
            System.out.print(s+", ");
        }
        System.out.println("");
        Ship ship = this.getBoard().getShip(shipIn.getShipClass());
        System.out.println("Ship Class: "+ship.getShipClass());
        // Get coordinate list of existing ship location
        ArrayList<String> oldCoords = new ArrayList<>();
        for (Cell part:ship.getShipParts()) {
            oldCoords.add(part.getBothCoords());
        }

        // Check new placement will be inside the grid
        int row = ship.getRow();
        int col = ship.getCol();
        int len = ship.getShipParts().length;
        boolean orientation = ship.isHorizontal();

        // Moving left, if horizontal OR vertical and less than 0 return false
        if ((colMov == -1) && (col + colMov < 0)) return false;
        // Moving right, if horizontal OR vertical and greater than 10 return false
        if ((colMov == 1) && ((col + len >= 10) || (col + colMov >= 10))) return false;
        // Moving up, if horizontal OR vertical, and less than 0 return false
        if ((rowMov == -1) && (row + rowMov < 0)) return false;
        // Moving down, if horizontal OR vertical and greater than 10 return false
        if ((rowMov == 1) && ((!orientation && row + len >= 10) || (orientation && row + rowMov >= 10))) return false;

        return true;
    }


    // Getters

    /**
     * The game will not continue if player 1 is not human controlled
     * @return whether this is a human controlled player
     */
    public boolean isHumanControlled() {
        return controlledByPlayer;
    }

    /**
     * Opposite of human controlled. A player that is not human controlled will be
     * controlled by the AI. Only player 2 can be controlled by AI and have the game
     * continue (otherwise we leave open the possibility that both players are AI
     * and the game will auto-resolve which is likely undesirable)
     *
     * @return whether this object is controlled by the computer
     */
    public boolean isAIControlled() {
        return !controlledByPlayer;
    }

    /**
     * Returns the player's name, or a fake name if the BattleshipPlayer is an AI
     * @return a String representing the player's name
     */
    public String getName() {
        if (isAIControlled()) {
            //computer controlled opponents don't have a proper name, just return
            //some fake sci-fi name
            return "BHal2000";
        }
        return name;
    }

    /**
     * Returns the player's board
     * @return A Board object
     */
    public Board getBoard() {
        return playerBoard;
    }

    /**
     * Returns the current message history for the player
     * @return The String to be displayed to the player
     */
    public String playerMessageHistory() {
        return this.messageHistory;
    }

    /**
     * Used to determine whether the message history has been updated since the last time we asked for it.
     * Will be set to true every time the player's history is changed
     * @return whether the player's message history has had changes since the last status request
     */
    public boolean messageHistoryStatus(){
        boolean status = historyDirty;
        historyDirty = false;
        return status;
    }

    /**
     * @return whether the player is ready
     */
    public boolean isReady() {
        return ready;
    }


    // Setters

    /**
     * Sets player ready in response to a client command
     * @param value true if player is ready, false otherwise
     */
    public void setReady(boolean value){ ready = value;}

    /**
     * Takes players existing messages and adds a new one to the string
     * NOTE: this should only be called when the input is valid
     * @param input String message - eg, console input, response from server
     */
    public void updateHistory(String input) {
        this.messageHistory = playerMessageHistory() + input;
        historyDirty = true;
    }
}

