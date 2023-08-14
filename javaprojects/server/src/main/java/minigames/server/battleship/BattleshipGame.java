package minigames.server.battleship;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minigames.server.achievements.AchievementHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

import static minigames.server.battleship.achievements.SLOW_LEARNER;

/**
 *
 */
public class BattleshipGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(BattleshipGame.class);

    static String player = "Nautical Map";
    static String enemy = "Target Map";

    //  ("Carrier", 6);
    //  ("BattleShip", 5);
    //  ("Destroyer", 4);
    //  ("Submarine", 4);
    //  ("Patrol Boat", 3);

    /** Uniquely identifies this game */
    String name;

    AchievementHandler achievementHandler;

    public BattleshipGame(String name) {
        this.name = name;
        this.achievementHandler = new AchievementHandler(BattleshipServer.class);
    }

    static String welcomeMessage = "Good evening Captain! Enter 'Ready' to start conquering the seas!\nUse arrow keys to move ships" +
            " around the grid. Press 'Tab' to switch vessel and 'Space' to rotate.\n...";

    //TODO: having two set players will likely not work for multiplayer and will need to be fixed - Names should also not be fixed values
    static Board player1 = new Board("Mitcho", welcomeMessage);
    static Board player2 = new Board("Craig", welcomeMessage);
    HashMap<String, Board> players = new HashMap<>();

    /**
     * Returns the names of the players currently playing the game
     * @return An array containing the names of current players
     */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /**
     * Return the meta-data for the in-progress game
     * @return a GameMetadata Object containing the information for the current game
     */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Battleship", name, getPlayerNames(), true);
    }

    /**
     * Returns the current state of the requested player, takes a BattleShipPlayer Object as a parameter
     * @param p The player whose state is to be described
     * @return The String to be displayed to the player
     */
    private String messageHistory(Board p) {
        if (p.getMessageHistory().isEmpty()) return welcomeMessage;
        else {
            StringBuilder sb = new StringBuilder();

            sb.append(p.getMessageHistory());

            return sb.toString();
        }
    }

    /**
     * This function is responsible for validating user input
     * @param coordinates The raw coordinate string that the user has entered into the console
     * @return true if the coordinate is valid, false if not
     */
    private boolean validate(String coordinates) {
        // Craig's code split off and moved here by Mitch
        // Regex to check that the coordinate string is valid
        String regex = "^[A-J][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        //convert the coordinates to uppercase
        coordinates = coordinates.toUpperCase();
        Matcher matcher = pattern.matcher(coordinates);
        // If the coordinates don't match it will return false
        return matcher.matches();
    }

    /**
     * Format the user input for better readability
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
     * Function to return an appropriate message to the player after their input
     * @param player player - not sure if this will be needed yet
     * @param gameState player's current game state
     * @param coordinate coordinate of enemy
     * @return formatted response string
     */
    private String respondToInput(Board player, GameState gameState, String coordinate, boolean hit) {
        //TODO: Clean up, it's a bit messy too
        // Response strings
        String inputPending = "...";
        String initialInstruction = "To fire at the enemy, enter grid coordinates: (eg, A4)";
        String incoming = "Prepare for incoming fire!";
        String enterCoords = "Enter grid coordinates:";
        String coordTag = "Return fire!";

        // Currently a simple implementation with a couple options that could be chosen at random
        String[] playerHitEnemy = {"Enemy ship hit! Well done Sir.", "Straight into their hull!", "Direct Hit!"};
        String[] playerMissedEnemy = {"Salvo Missed.", "Target not hit.", "Adjust your coordinates."};
        String[] enemyHitPlayer = {"Enemy has hit our fleet,", "We've been hit!", "We're under fire!"};
        String[] enemyMissedPlayer = {"Enemy has missed!", "Enemy missed another salvo.", "They missed us."};

        // Generate a random number to pick a response string
        Random rand = new Random();
        int randomNum = rand.nextInt(3);

        StringBuilder sb = new StringBuilder();
        switch (gameState) {
            case SHIP_PLACEMENT -> {
                sb.append("\n\n").append(initialInstruction).append("\n").append(inputPending);
                return sb.toString();
            }
            case CALC_PLAYER -> {
                if (hit) {
                    sb.append("\n\n").append(playerHitEnemy[randomNum]).append(" ").append(incoming);
                } else {
                    sb.append("\n\n").append(playerMissedEnemy[randomNum]).append(" ").append(incoming);
                }
                return sb.toString();
            }
            case CALC_ENEMY -> {
                if (hit) {
                    //TODO: append appropriate ship class value? or remove and leave just coordinates
                    sb.append("\n").append(enemyHitPlayer[randomNum]).append(" Ship-Class:").append(" Carrier");
                    sb.append(" at coordinates: [").append(coordinate).append("]");
                    sb.append("\n\n").append(coordTag).append(" ").append(enterCoords).append("\n").append(inputPending);
                } else {
                    sb.append("\n").append(enemyMissedPlayer[randomNum]).append(" Salvo fired at: coordinates: [");
                    sb.append(coordinate).append("]");
                    sb.append("\n\n").append(coordTag).append(" ").append(enterCoords).append("\n").append(inputPending);
                }
                return sb.toString();
            }
        }
        return sb.toString();
    }

    /**
     * Function to determine whether the player's input has hit a ship. Sets the CellType accordingly and returns
     * true or false. The boolean value is used in another function to determine the response
     * @param player current player - Board object
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return true if player has hit a ship, false if player hit water or previously missed cell
     */
    private boolean shotOutcome(Board player, int x, int y) {
        // Get players current grid
        Cell[][] grid = player.getGrid();
        // Get cell type of player's coordinate
        CellType currentState = grid[x][y].getCellType();
        // If player hit ocean or missed set CellType to Miss and return false
        if (currentState.equals(CellType.OCEAN)) {
            player.setGridCell(x, y, CellType.MISS);
            return false;
        } else if (currentState.equals(CellType.MISS)){
            achievementHandler.unlockAchievement(name, SLOW_LEARNER.toString());
            return false;
        } else {
            player.setGridCell(x, y, CellType.HIT);
            return true;
        }
    }

    /**
     * Function to determine what to do with user input based on the current game state
     * @param player current player - Board object
     * @param input user input from the console
     */
    private void calcUserInput(Board player, String input) {
        //TODO: This is pretty messy and wants cleaning up - the whole process probably wants refactoring
        //convert the input to uppercase
        input = input.toUpperCase();
        // Respond to the user's input in relation to game state
        String response = "";
        switch (player.getGameState()) {
            case SHIP_PLACEMENT -> {
                // Check if the users input was "ready", to start the game
                if (input.matches("READY")) {
                    player.updateMessageHistory("Ready");
                    response = respondToInput(player, player.getGameState(), "", false);
                    player.updateMessageHistory(response);
                    player.setGameState(GameState.INPUT_CALC);
                }
            }
            case INPUT_CALC -> {
                // Check if coordinates are valid, if so, format and add to message history
                if (validate(input)) {
                    String validatedInput = formatInput(input);
                    player.updateMessageHistory(validatedInput);
                    player.setGameState(GameState.CALC_PLAYER);
                    if (player.getGameState().equals(GameState.CALC_PLAYER)) {
                        // Take user input and determine the result of shooting that coordinate
                        String chars = "ABCDEFGHIJ";
                        String coordVert = validatedInput.split(",")[0];
                        int x = chars.indexOf(coordVert);
                        int y = Integer.parseInt(validatedInput.split(",")[1]);
                        boolean result = shotOutcome(player2, x, y);
                        response = respondToInput(player, player.getGameState(), "", result);
                        player.updateMessageHistory(response);
                        player.setGameState(GameState.CALC_ENEMY);
                    }
                }
            }
            case CALC_ENEMY -> {
                // Determine the result of enemy action
                // Create function to shoot at a player grid coordinate
                // Generate a random boolean for now, with placeholder coordinate
                Random rand = new Random();
                boolean randomBool = rand.nextBoolean();
                response = respondToInput(player, player.getGameState(), "C,3", randomBool);
                player.updateMessageHistory(response);
                player.setGameState(GameState.INPUT_CALC);
            }
        }
    }

    /**
     * Run the commands received from the BattleshipServer class
     * @param cp The CommandPackage Object containing the commands to be run
     * @return The information to render in the client
     */
    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        Board p = players.get(cp.player());
        // Get user input typed into the console
        String userInput = String.valueOf(cp.commands().get(0).getValue("command"));
        // Update players message history
        calcUserInput(p, userInput);

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "updateHistory").put("history", p.getMessageHistory()));
        renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text", Board.generateBoard(player, player1.getGrid())));
        renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text", Board.generateBoard(enemy, player2.getGrid())));
        renderingCommands.add(new JsonObject().put("command", "clearInput"));
        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /**
     * Joins an in-progress game taking the player name as a parameter. Returns the rendering package to display the
     * game in its current state
     * NOTE: We'll be adding in some logic to check the number of players and other conditions to not open / join a game
     * @param playerName The name of the player making the request to join a game
     * @return The RenderingPackage for the game in its current (or new) state
     */
    public RenderingPackage joinGame(String playerName) {
        // Don't allow a player to join if the player's name is already taken
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map((r) -> r.toJson()).toList()
            );
        } else {
            Board p = new Board(playerName, welcomeMessage);
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Battleship", "Battleship", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "updateHistory").put("history", messageHistory(p)));
            renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text", Board.generateBoard(player, player1.defaultGrid())));
            renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text", Board.generateBoard(enemy, player2.defaultGrid())));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
}
