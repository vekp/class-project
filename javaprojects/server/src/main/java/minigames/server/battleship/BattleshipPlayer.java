package minigames.server.battleship;

import minigames.server.achievements.AchievementHandler;

import java.util.ArrayList;
import java.util.Arrays;
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
     * TODO: add method description
     * @param opponent
     * @param x
     * @param y
     * @return
     */
    private BattleshipTurnResult shotOutcome(Board opponent, int x, int y) {
        String input = formatInput(chars.charAt(x)+""+y);
        // Get players current grid
        Cell[][] grid = opponent.getGrid();

        // Get cell type of player's coordinate
        CellType currentState = grid[x][y].getCellType();
        // If player hit ocean set CellType to Miss and return false
        if (currentState.equals(CellType.OCEAN)) {
//            opponent.setGridCell(x, y, CellType.MISS);
            opponent.getGrid()[x][y].shoot();
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
//            opponent.setGridCell(x, y, CellType.HIT);
            opponent.getGrid()[x][y].shoot();

            // Update the ships to include the hit in their cells
            HashMap<String, Ship> vessels = opponent.getVessels();
            vessels.forEach((key, value) ->{
                Ship current = value;
                current.updateShipStatus(x, y, getName());
                vessels.replace(key, current);
            });
            opponent.setVessels(vessels);

            // Get the ship shot at
            Ship ship = opponent.getVessel(new Cell(y, x), opponent.getVessels());

            return BattleshipTurnResult.hitTarget(input, ship.isJustSunk());
        }
        // TODO: increment turn number?
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
     * TODO: add method description etc
     * @return
     */
    private String generateCoordinate() {

        // Generate random coordinates
        // TODO: needs better error checking to ensure we stay in board bounds? current limits are hard coded
        Random rand = new Random();
        int randX = rand.nextInt(10);
        int randY = rand.nextInt(10);
        String cpuCoordStr = chars.charAt(randX) + "" + randY;
        System.out.println(cpuCoordStr);
        return cpuCoordStr;
    }

    public void moveShip(String move, Ship ship) {
        // List of available ships
        String[] ships = {"Carrier", "Battleship", "Destroyer", "Submarine", "Patrol Boat"};
        // Currently selected ship
        int shipIndex = 0;
        for (int i=0; i<ships.length; i++) {
            if (ships[i].equals(ship.getShipClass())) shipIndex = i;
        }


        int row = ship.getRow();
        int col = ship.getCol();
        boolean hor = ship.isHorizontal();

        switch (move) {
            case "UP" -> {
                System.out.println("Moving up");
                getBoard().customShip(ship.getShipClass(), shipIndex, row-1, col, hor, getBoard().getVessels());
            }
            case "DOWN" -> {
                System.out.println("Moving down");
                getBoard().customShip(ship.getShipClass(), shipIndex, row+1, col, hor, getBoard().getVessels());
            }
        }

    }

    public boolean validPlacement(int shipIndex, int colMov, int rowMov, boolean rotate) {
        // Get selected ship
        String[] ships = this.getBoard().getVessels().keySet().toArray(new String[0]);
        Ship ship = this.getBoard().getShip(ships[shipIndex]);
        System.out.println("Ship Class: "+ship.getShipClass());
        // Get coordinate list of existing ship location
        ArrayList<String> oldCoords = new ArrayList<>();
        for (Cell part:ship.getShipParts()) {
            oldCoords.add(part.getBothCoords());
        }

        // TODO check if it overlaps? each turn or only at end?

        // Check new placement will be inside the grid
        int row = ship.getRow();
        int col = ship.getCol();
        int horSize = col + ship.getShipParts().length + colMov;
        System.out.println("Horizontal: "+ship.isHorizontal());
        System.out.println("Horizontal Size: "+horSize);
        int length = ship.getShipParts().length;
        System.out.println("Ship Length: "+length);
        System.out.println(col + " + " + (length-1) + " + " + colMov + " = " + horSize);

        if (ship.isHorizontal()) {
            if (colMov != 0 && rowMov == 0 && horSize < length || horSize > 9) {
                System.out.println("Cannot move horizontally");
                return false;
            }
            if (ship.isHorizontal() && row + rowMov < 0 || row + rowMov > 9 && colMov == 0) {
                System.out.println("Cannot move vertically");
                return false;
            }
        }




//        if (ship.isHorizontal() && (horSize > 10 || horSize < length) && colMov != 0) {
//            System.out.println("Horizontal ship -> horizontal movement");
//            return false;
//        }
//        if (ship.isHorizontal() && (row + rowMov > 10 || row + rowMov < 0) && rowMov != 0) {
//            System.out.println("Horizontal ship -> vertical movement");
//            return false;
//        }
//        int verSize = ship.getShipParts().length + row + rowMov;
//        if (!ship.isHorizontal() && (verSize > 10 || verSize < length) && rowMov != 0) {
//            System.out.println("Vertical ship -> vertical movement");
//            return false;
//        }
//        if (!ship.isHorizontal() && (col + colMov > 10 || col + colMov < 0) && colMov != 0) {
//            System.out.println("Vertical ship -> horizontal movement");
//            return false;
//        }
//
//        if (rotate) {
//            if (ship.isHorizontal() && ship.getShipParts().length + row > 10) return false;
//            if (!ship.isHorizontal() && ship.getShipParts().length + row > 10) return false;
//        }

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

    /**
     * Sets player ready in response to a client command
     * @param value true if player is ready, false otherwise
     */
    public void setReady(boolean value){ ready = value;}

    // Setters

    /**
     * Takes players existing messages and adds a new one to the string
     * NOTE: this should only be called when the input is valid
     * @param input String message - eg, console input, response from server
     */
    public void updateHistory(String input) {
        this.messageHistory = playerMessageHistory() + input;
        historyDirty = true;
    }

    public void setPlayerBoard(Board playerBoard) {
        this.playerBoard = playerBoard;
    }

    /**
     * Sets the player's ready state to true
     */
    public void setReadyState() {
        this.ready = true;
    }
}

