package minigames.server.battleship;

import minigames.server.achievements.AchievementHandler;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static minigames.server.battleship.achievements.*;

public class BattleshipPlayer {

    // Fields
    private String name;
    private boolean controlledByPlayer;
    private final Board playerBoard;
    private String messageHistory;  // String of all valid messages, both game and player
    private boolean ready;

    // Constructor
    /**
     *
     * @param name
     * @param playerBoard
     * @param controlledByPlayer
     */
    public BattleshipPlayer(String name, Board playerBoard, boolean controlledByPlayer, String messageHistory) {
        this.name = name;
        this.playerBoard = playerBoard;
        this.controlledByPlayer = controlledByPlayer;
        this.messageHistory = messageHistory;
        this.ready = false;
    }

    // Methods

    /**
     *
     * @param opponent
     * @return
     */
    public BattleShipTurnResult processAITurn(Board opponent){
        return processTurn(generateCoordinate(), opponent);
    }

    /**
     *
     * @param input
     * @param opponent
     * @return
     */
    public BattleShipTurnResult processTurn(String input, Board opponent) {
        BattleShipTurnResult invalidResult = new BattleShipTurnResult(false, "Invalid Input");
        if (input.equalsIgnoreCase("Ready")) {
            updateHistory("Ready");
            setReadyState();
//                String message = BattleShipTurnResult.instruction().message();
        }

        if (!validateInput(input)) {
            return invalidResult;
        }
        String[] validatedInput = formatInput(input).split(",");

        int col = BattleshipGame.chars.indexOf(validatedInput[0]);
        int row = -1;
        try {
            row = Integer.parseInt(validatedInput[1]);
        } catch (NumberFormatException e) {
            return invalidResult;
        }
        return shotOutcome(opponent, col, row);
    }

    private BattleShipTurnResult shotOutcome(Board opponent, int x, int y) {
        // Get players current grid
        Cell[][] grid = opponent.getGrid();
        //todo this should check if the cell coordinate is valid and return failed

        // Get cell type of player's coordinate
        CellType currentState = grid[x][y].getCellType();
        // If player hit ocean set CellType to Miss and return false
        if (currentState.equals(CellType.OCEAN)) {
            opponent.setGridCell(x, y, CellType.MISS);
            //todo these messages arent really correct - they wont display properly on the opponent client i think
            return isHumanControlled() ? BattleShipTurnResult.playerMissedEnemy() :
                    BattleShipTurnResult.enemyMissedPlayer();
            // If the Cell is a MISS cell, return false and check for Slow Learner Achievement
        } else if (currentState.equals(CellType.MISS)) {
            //no need to check for already unlocked as handler will do that
            System.out.println("Slow Learner Achievement - Requirements met for " + getName());
            AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
            handler.unlockAchievement(getName(), SLOW_LEARNER.toString());
            return isHumanControlled() ? BattleShipTurnResult.playerMissedEnemy() :
                    BattleShipTurnResult.enemyMissedPlayer();
        } else if (currentState.equals(CellType.HIT)) {
            System.out.println("You Got Him Achievement - Requirements met for " + getName());
            AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
            handler.unlockAchievement(getName(), YOU_GOT_HIM.toString());
            return BattleShipTurnResult.alreadyHitCell();
        } else {
            // If the cell is not an ocean, miss, or hit cell, set the cell to a "hit"
            opponent.setGridCell(x, y, CellType.HIT);

            // Update the ships to include the hit in their cells

            HashMap<String, Ship> vessels = opponent.getVessels();

            vessels.forEach((key, value) ->{
                Ship current = value;
                current.updateShipStatus(x, y);
                vessels.replace(key, current);
            });

            opponent.setVessels(vessels);

            //I dunno what this is so i've commented it out, reimplement?
//            if (sunk(opponent, x, y)) {
//                respondToInput(opponent, GameState.SHIP_SUNK, "",true);
//            }
            return isHumanControlled() ? BattleShipTurnResult.playerHitEnemy() : BattleShipTurnResult.enemyHitPlayer();
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
        // If the player enters C120 as the coordinates give them the COSC120 inside joke achievement
        if (input.equals("C120") && isHumanControlled()) {
            AchievementHandler handler = new AchievementHandler(BattleshipServer.class);
            handler.unlockAchievement(getName(), C_120.toString());
        }
        // Craig's code split off and moved here by Mitch
        // Regex to check that the coordinate string is valid
        String regex = "^[A-J][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        //convert the coordinates to uppercase
        input = input.toUpperCase();
        Matcher matcher = pattern.matcher(input);
        // If the coordinates don't match it will return false
        return matcher.matches();
    }

    private String generateCoordinate() {

        // Generate random coordinates
        //todo needs better error checking to ensure we stay in board bounds? current limits are hard coded
        Random rand = new Random();
        int randX = rand.nextInt(10);
        int randY = rand.nextInt(10);
        String cpuCoordStr = BattleshipGame.chars.charAt(randX) + "," + randY;
        return cpuCoordStr;
    }

    // Getters

    /**
     * The game will not continue if player 1 is not human controlled
     *
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

    public String getName() {
        if (isAIControlled()) {
            //computer controlled opponents don't have a proper name, just return
            //some fake sci-fi name
            return "BHal2000";
        }
        return name;
    }

    public Board getBoard() {
        return playerBoard;
    }

    /**
     * Returns the current message history for the player
     *
     * @return The String to be displayed to the player
     */
    public String playerMessageHistory() {
        return this.messageHistory;
    }

    // Setters

    public void updateHistory(String input) {
        this.messageHistory = playerMessageHistory() + input;
    }

    public void setReadyState() {
        this.ready = true;
    }
}

