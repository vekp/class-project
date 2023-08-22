package minigames.server.battleship;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.vertx.core.json.impl.JsonUtil;
import minigames.server.achievements.AchievementHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

import static minigames.server.battleship.achievements.*;

/**
 *
 */
public class BattleshipGame {

    /**
     * A logger for logging output
     */
    private static final Logger logger = LogManager.getLogger(BattleshipGame.class);

    static String player = "Nautical Map";
    static String enemy = "Target Map";

    //  ("Carrier", 6);
    //  ("BattleShip", 5);
    //  ("Destroyer", 4);
    //  ("Submarine", 4);
    //  ("Patrol Boat", 3);

    /**
     * Uniquely identifies this game
     */
    String gameName;
    String playerName;

    AchievementHandler achievementHandler;

    public BattleshipGame(String gameName, String playerName) {
        this.gameName = gameName;
        this.achievementHandler = new AchievementHandler(BattleshipServer.class);
        this.playerName = playerName;
    }

    static String welcomeMessage = """
            Good evening Captain! Enter 'Ready' to start conquering the seas!
            Use arrow keys to move ships around the grid. Press 'Tab' to switch vessel and 'Space' to rotate.
            ...
            """;

    String chars = "ABCDEFGHIJ";

    //TODO: having two set players will likely not work for multiplayer and will need to be fixed - Names should also not be fixed values
    Board player1 = new Board(playerName, welcomeMessage);
    Board player2 = new Board("CPU", welcomeMessage);
    HashMap<String, Board> players = new HashMap<>();

    /**
     * Returns the names of the players currently playing the game
     *
     * @return An array containing the names of current players
     */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String returnGameName() {
        return this.gameName;
    }

    /**
     * Return the meta-data for the in-progress game
     *
     * @return a GameMetadata Object containing the information for the current game
     */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Battleship", gameName, getPlayerNames(), true);
    }

    /**
     * Returns the current state of the requested player, takes a BattleShipPlayer Object as a parameter
     *
     * @param p The player whose state is to be described
     * @return The String to be displayed to the player
     */
    private String messageHistory(Board p) {
        if (p.getMessageHistory().isEmpty()) return welcomeMessage;
        else {
            return p.getMessageHistory();
        }
    }

    /**
     * This function is responsible for validating user input
     *
     * @param coordinates The raw coordinate string that the user has entered into the console
     * @return true if the coordinate is valid, false if not
     */
    private boolean validate(String coordinates) {
        // If the player enters C120 as the coordinates give them the COSC120 inside joke achievement
        if(coordinates.equals("C120")){
            achievementHandler.unlockAchievement(playerName, C_120.toString());
        }
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
     * Function to create an appropriate message to the player after their input
     *
     * @param player     player
     * @param gameState  player's current game state
     * @param coordinate coordinate of enemy
     */
    private void respondToInput(Board player, GameState gameState, String coordinate, boolean hit) {
        //TODO: Clean up, it's a bit messy too

        // Response strings
        String inputPending = "...";
        String initialInstruction = "To fire at the enemy, enter grid coordinates: (eg, A4)";
        String incoming = "Prepare for incoming fire!";
        String enterCoords = "Enter grid coordinates:";
        String coordTag = "Return fire!";
        String shipSunk = "Enemy ship has been sunk!";

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
            }
            case CALC_PLAYER -> {
                if (hit) {
                    sb.append("\n\n").append(playerHitEnemy[randomNum]).append(" ").append(incoming);
                } else {
                    sb.append("\n\n").append(playerMissedEnemy[randomNum]).append(" ").append(incoming);
                }
            }
            case CALC_ENEMY -> {
                if (hit) {
                    //TODO: append appropriate ship class value? or remove and leave just coordinates
                    sb.append("\n").append(enemyHitPlayer[randomNum]);  //.append(" Ship-Class:").append(" Carrier");
                    sb.append(" enemy fired at coordinates: [").append(coordinate).append("]");
                    sb.append("\n\n").append(coordTag).append(" ").append(enterCoords).append("\n").append(inputPending);
                } else {
                    sb.append("\n").append(enemyMissedPlayer[randomNum]).append(" Salvo fired at: coordinates: [");
                    sb.append(coordinate).append("]");
                    sb.append("\n\n").append(coordTag).append(" ").append(enterCoords).append("\n").append(inputPending);
                }
            }
            case SHIP_SUNK -> {
                sb.append("\n").append(shipSunk);
            }
        }
        player.updateMessageHistory(sb.toString());
    }

    /**
     * Test function to print the current user's name to see if it could be implemented into the login framework
     */
    public void printUsername(){
        // "USER" is where the user's name is stored for UNIX based systems
        String username = System.getenv("USER");
        // For Windows the above query will return null, as on windows the variable for the user is USERNAME
        if (username == null) {
            username = System.getenv("USERNAME");
        }
        System.out.println("Username: " + username);
    }

    /**
     * Function to determine whether the player's input has hit a ship. Sets the CellType accordingly and returns
     * true or false. The boolean value is used in another function to determine the response
     *
     * @param player current player - Board object
     * @param x      horizontal coordinate
     * @param y      vertical coordinate
     * @return true if player has hit a ship, false if player hit water or previously missed cell
     */
    private boolean shotOutcome(Board player, int x, int y) {

        // TODO Remove this after showing Nathan
        printUsername(); // test call to printUsername function
        // Get players current grid
        Cell[][] grid = player.getGrid();
        // Get cell type of player's coordinate
        CellType currentState = grid[x][y].getCellType();
//        System.out.println("Cell Type: "+ currentState.toString());
        // If player hit ocean set CellType to Miss and return false
        if (currentState.equals(CellType.OCEAN)) {
            player.setGridCell(x, y, CellType.MISS);
            return false;
        // If the Cell is a MISS cell, return false and check for Slow Learner Achievement
        } else if (currentState.equals(CellType.MISS)) {
            //no need to check for already unlocked as handler will do that
            System.out.println("Slow Learner Achievement - Requirements met for " + playerName);
            achievementHandler.unlockAchievement(playerName, SLOW_LEARNER.toString());
            return false;
        } else if (currentState.equals(CellType.HIT)) {
            System.out.println("You Got Him Achievement - Requirements met for " + playerName);
            achievementHandler.unlockAchievement(playerName, YOU_GOT_HIM.toString());
            return true;
        } else {
            // Set the cell to a "hit"
            player.setGridCell(x, y, CellType.HIT);
            // TODO: Do a loop within a hashmap instead
            Ship[] ships = new Ship[5];
            ships[0] = this.player2.getShip("Carrier");
            ships[1] = this.player2.getShip("Battleship");
            ships[2] = this.player2.getShip("Destroyer");
            ships[3] = this.player2.getShip("Submarine");
            ships[4] = this.player2.getShip("Patrol Boat");

            for(int i = 0; i < ships.length; i++){
                ships[i] = ships[i].updateShipStatus(x, y);
            }

            HashMap<String, Ship> vessels = this.player1.getVessels();

            vessels.replace("Carrier", ships[0]);
            vessels.replace("Battleship", ships[1]);
            vessels.replace("Destroyer", ships[2]);
            vessels.replace("Submarine", ships[3]);
            vessels.replace("Patrol Boat", ships[4]);

            this.player2.setVessels(vessels);





            // Not sure what this check does - Craig
            if (sunk(player, x, y)) {
                respondToInput(player, GameState.SHIP_SUNK, "",true);
            }
            return true;
        }
        // TODO: increment turn number?

    }

    private boolean sunk(Board player, int x, int y) {
         Cell[][] grid = player.getGrid();
         return true;
    }

    private int[] generateCoordinate(Board player) {

        // Generate random coordinates
        Random rand = new Random();
        int randX = rand.nextInt(10);
        int randY = rand.nextInt(10);

        return new int[]{randX, randY};
    }

    /**
     * Function to determine what to do with user input based on the current game state
     *
     * @param player current player - Board object
     * @param input  user input from the console
     */
    private void calcUserInput(Board player, String input) {
        // TODO: Tidy up
        // Convert the input to uppercase
        input = input.toUpperCase();
        // Respond to the user's input in relation to game state
        switch (player.getGameState()) {
            case SHIP_PLACEMENT -> {
                // Check if the users input was "ready", to start the game
                if (input.matches("READY")) {
                    player.updateMessageHistory("Ready");
                    respondToInput(player, player.getGameState(), "", false);
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
                        String coordVert = validatedInput.split(",")[0];
                        int x = chars.indexOf(coordVert);
                        int y = Integer.parseInt(validatedInput.split(",")[1]);
                        // Pass in the other players board to see if it hit their ship
                        boolean result = shotOutcome(player2, x, y);
                        respondToInput(player, player.getGameState(), "", result);
                        player.setGameState(GameState.CALC_ENEMY);
                    }
                    if (player.getGameState().equals(GameState.CALC_ENEMY)) {
                        // Determine the result of enemy action
                        int[] cpuCoord = generateCoordinate(player1);
                        String cpuCoordStr = chars.charAt(cpuCoord[0]) + "," + cpuCoord[1];
                        //System.out.println(cpuCoordStr);

                        boolean result = shotOutcome(player1, cpuCoord[0], cpuCoord[1]);
                        respondToInput(player, player.getGameState(), cpuCoordStr, result);
                        player.setGameState(GameState.INPUT_CALC);
                    }
                }
            }
        }
    }

    /**
     * Run the commands received from the BattleshipServer class
     *
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
        renderingCommands.add(new JsonObject().put("command", "updatePlayerName").put("player", p.getPlayerName()));
        renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text", Board.generateBoard(player, player1.getGrid())));
        renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text", Board.generateBoard(enemy, player2.getGrid())));
        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /**
     * Joins an in-progress game taking the player name as a parameter. Returns the rendering package to display the
     * game in its current state
     * NOTE: We'll be adding in some logic to check the number of players and other conditions to not open / join a game
     *
     * @param playerName The name of the player making the request to join a game
     * @return The RenderingPackage for the game in its current (or new) state
     */
    public RenderingPackage joinGame(String playerName) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        // Don't allow a player to join if the player's name is already taken
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map((r) -> r.toJson()).toList()
            );
        } else {
            Board p = new Board(playerName, welcomeMessage);
            players.put(playerName, p);


            renderingCommands.add(new LoadClient("Battleship", "Battleship", gameName, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "updateHistory").put("history", messageHistory(p)));
            renderingCommands.add(new JsonObject().put("command", "updatePlayerName").put("player", playerName));
            renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text", Board.generateBoard(player, player1.getGrid())));
            renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text", Board.generateBoard(enemy, player2.getGrid())));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
}
