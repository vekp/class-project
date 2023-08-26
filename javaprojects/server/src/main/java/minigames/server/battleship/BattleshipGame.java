package minigames.server.battleship;

import java.util.*;

import minigames.server.achievements.AchievementHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * TODO: add class description
 */
public class BattleshipGame {

    /**
     * A logger for logging output
     */
    private static final Logger logger = LogManager.getLogger(BattleshipGame.class);

    /**
     * Uniquely identifies this game
     */
    String gameName;
    String playerName;
    AchievementHandler achievementHandler;
    GameState gameState;

    LinkedHashMap<String, BattleshipPlayer> bPlayers = new LinkedHashMap<>();
    int currentTurn = 0;
    int gameTurn = 0;
    static final String player = "Nautical Map";
    static final String enemy = "Target Map";
    static String welcomeMessage = """
            Good evening Captain! Enter 'Ready' to start conquering the seas!
            Use arrow keys to move ships around the grid. Press 'Tab' to switch vessel and 'Space' to rotate.
            ...""";
    public static String chars = "ABCDEFGHIJ";

    // Constructor

    /**
     * @param gameName
     * @param playerName
     */
    public BattleshipGame(String gameName, String playerName) {
        this.gameName = gameName;
        this.achievementHandler = new AchievementHandler(BattleshipServer.class);
        this.playerName = playerName;
        this.gameState = GameState.SHIP_PLACEMENT;
        bPlayers.put("Player", new BattleshipPlayer("Player",
                new Board(0), true, welcomeMessage));
        bPlayers.put("Computer", new BattleshipPlayer("Computer",
                new Board(1), false, welcomeMessage));
    }

    // Reference list of ship names and their size
    //  ("Carrier", 6);
    //  ("BattleShip", 5);
    //  ("Destroyer", 4);
    //  ("Submarine", 4);
    //  ("Patrol Boat", 3);

    /**
     * Returns the names of the players currently playing the game
     *
     * @return An array containing the names of current players
     */
    public String[] getPlayerNames() {
        return bPlayers.keySet().toArray(String[]::new);
    }

    /**
     * @return the host player's name
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * @return the game's name
     */
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
     * Run the commands received from the BattleshipServer class
     *
     * @param cp The CommandPackage Object containing the commands to be run
     * @return The information to render in the client
     */
    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);

        ArrayList<JsonObject> commands = new ArrayList<>();

        // Get user input typed into the console
        String userInput = String.valueOf(cp.commands().get(0).getValue("command"));
        String currentTurnPlayer = getPlayerNames()[currentTurn];

        //Handle exit game commands
        if (userInput.equals("exitGame")) {
            System.out.println("This should exit that game");
            //todo this shouldnt clear list, just remove 1 player?
            bPlayers.clear();
            System.out.println(getPlayerNames().length);
            //probably just return a render package immediately here?

        } else if (false) { //todo change this to test if other player has left
            //todo here is where we should check if the OTHER player had left the game, and tell this client
            //to pop up a window saying other player has left
            //also just return a render package immediately here
        }

        //if the game hasnt been aborted, we can move on to processing commands based on what the gamestate is
        switch (gameState) {
            case SHIP_PLACEMENT -> {
                if (userInput.equalsIgnoreCase("ready")) {
                    BattleshipPlayer currentClient = bPlayers.get(cp.player());
                    currentClient.setReady(true);
                    //if the other player is also ready or are AI, we are good to start the game
                    gameState = GameState.IN_PROGRESS;
                    //the gamestate assumes all players are ready. On checking this loop, if we find any players that are
                    //not yet ready, set the state back to placement/waiting
                    for (BattleshipPlayer player : bPlayers.values()) {
                        if (!(player.isReady() || player.isAIControlled())) {
                            gameState = GameState.SHIP_PLACEMENT;
                        }
                    }
                }
            }
            case IN_PROGRESS -> {
                BattleshipPlayer current = bPlayers.get(cp.player());
                if (userInput.equals("refresh") || !currentTurnPlayer.equals(cp.player())) {
                    //refresh is called periodically while the client is waiting for its turn.
                    //once the client of the current turn's player asks for a refresh, we will inform
                    //it that it is now that players turn, and it can prepare to send input.
                    //otherwise, we send a wait command for the client to continue waiting for turn
                    if (currentTurnPlayer.equals(cp.player())) {
                        commands.add(new JsonObject().put("command", "prepareTurn"));
                        commands.add(new JsonObject().put("command", "inputAllowable").put("allowed", true));
                    } else {
                        commands.add(new JsonObject().put("command", "wait"));
                        // If it is not the players turn, lock their console from input
                        commands.add(new JsonObject().put("command", "inputAllowable").put("allowed", false));
                    }
                } else {
                    //if no other commands are present, and it is this player's turn, we can process the game turn
                    commands = runGameCommand(bPlayers.get(cp.player()), userInput);
                }
                return new RenderingPackage(gameMetadata(), commands);
            }
            case GAME_OVER -> {
            }
        }

        return new RenderingPackage(gameMetadata(), commands);
    }

    /**
     * Function called during gameplay for the current turn's player. Processes the player's input and
     * returns a shot result
     *
     * @param p         the current player whose turn we wish to process
     * @param userInput the user input that client entered (should be a shot coordinate)
     * @return a list of commands to put in a rendering package
     */
    public ArrayList<JsonObject> runGameCommand(BattleshipPlayer p, String userInput) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        // Get other battleship player - there can only be one other actual player
        BattleshipPlayer otherPlayer = null;
        for (String name : getPlayerNames()) {
            if (!name.equals(p.getName())) {
                otherPlayer = bPlayers.get(name);
                assert otherPlayer != null;
            }
        }
        //this alternates between 1 and 0 for the next turn -Nathan
        int nextPlayer = 1 - currentTurn;


        BattleshipTurnResult result = p.processTurn(userInput, otherPlayer.getBoard());
        //if the opponent is AI, do their turn immediately, otherwise switch turns to next player
        if (result.successful()) {
            // Update current player's message history with the result
            p.updateHistory(result.message());
            // Determine response to other player based on their result
            if (result.shipHit()) {
                otherPlayer.updateHistory(BattleshipTurnResult.enemyHitPlayer(formatInput(userInput)).message());
            } else {
                otherPlayer.updateHistory(BattleshipTurnResult.enemyMissedPlayer(formatInput(userInput)).message());
            }

            if (otherPlayer.isAIControlled()) {
                BattleshipTurnResult opponentResult = otherPlayer.processAITurn(p.getBoard());
                // System.out.println(opponentResult.message());
                p.updateHistory(opponentResult.message());
            } else {
                // opponent is human, switch turn flag so they go next
                currentTurn = nextPlayer;
            }
        }

        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "updateHistory").put("history", p.playerMessageHistory()));
        renderingCommands.add(new JsonObject().put("command", "updatePlayerName").put("player", p.getName()));
        renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").
                put("text", Board.generateBoard(player, p.getBoard().getGrid())));
        renderingCommands.add(new JsonObject().put("command", "placePlayer2Board")
                .put("text", Board.showEnemyBoard(enemy, otherPlayer.getBoard().getGrid())));

        renderingCommands.add(new JsonObject().put("command", "wait"));
        return renderingCommands;
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
        renderingCommands.add(new LoadClient("Battleship", "Battleship", gameName, playerName).toJson());
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "updateHistory").put("history", welcomeMessage));
        renderingCommands.add(new JsonObject().put("command", "updatePlayerName").put("player", "- Place Ships -"));

        // Don't allow a player to join if the player's name is already taken
        if (bPlayers.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map((r) -> r.toJson()).toList()
            );
        } else if (currentTurn > 0) {
            // TODO: if current turn alternates between 1,0 it should be getting an overall turn value for the entire game
            // Don't allow a player to join if the game has started
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("This game has already started")
                    }).map((r) -> r.toJson()).toList()
            );
        } else {
            // First player to join - Replace the "Player","Computer" with actual player followed by new computer to maintain order
            if (bPlayers.containsKey("Player")) {
                bPlayers.remove("Player");
                bPlayers.put(playerName, new BattleshipPlayer(playerName,
                        new Board(0), true, welcomeMessage));
                bPlayers.remove("Computer");
                bPlayers.put("Computer", new BattleshipPlayer("Computer",
                        new Board(1), false, welcomeMessage));
                // For one player, show enemy board of computer
                renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text",
                        Board.generateBoard(player, bPlayers.get(playerName).getBoard().getGrid())));
                renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text",
                        Board.showEnemyBoard(enemy, bPlayers.get(getPlayerNames()[1]).getBoard().getGrid())));
            } else {
                // Second player to join - Simply replaces the computer
                bPlayers.remove("Computer");
                bPlayers.put(playerName, new BattleshipPlayer(playerName,
                        new Board(1), true, welcomeMessage));
                // For two players, show enemy board of player 1
                renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text",
                        Board.generateBoard(player, bPlayers.get(playerName).getBoard().getGrid())));
                renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text",
                        Board.showEnemyBoard(enemy, bPlayers.get(getPlayerNames()[0]).getBoard().getGrid())));
            }

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}

//
//    /**
//     * This function is responsible for validating user input
//     *
//     * @param coordinates The raw coordinate string that the user has entered into the console
//     * @return true if the coordinate is valid, false if not
//     */
//    private boolean validate(String coordinates) {
//        // If the player enters C120 as the coordinates give them the COSC120 inside joke achievement
//        if(coordinates.equals("C120")){
//            achievementHandler.unlockAchievement(playerName, C_120.toString());
//        }
//        // Craig's code split off and moved here by Mitch
//        // Regex to check that the coordinate string is valid
//        String regex = "^[A-J][0-9]$";
//        Pattern pattern = Pattern.compile(regex);
//        //convert the coordinates to uppercase
//        coordinates = coordinates.toUpperCase();
//        Matcher matcher = pattern.matcher(coordinates);
//        // If the coordinates don't match it will return false
//        return matcher.matches();
//    }
//
//
//
//    /**
//     * Function to create an appropriate message to the player after their input
//     *
//     * @param player     player
//     * @param gameState  player's current game state
//     * @param coordinate coordinate of enemy
//     */
//    private void respondToInput(Board player, GameState gameState, String coordinate, boolean hit) {
//        //TODO: Clean up, it's a bit messy too
//
//        // Response strings
//        String inputPending = "...";
//        String initialInstruction = "To fire at the enemy, enter grid coordinates: (eg, A4)";
//        String incoming = "Prepare for incoming fire!";
//        String enterCoords = "Enter grid coordinates:";
//        String coordTag = "Return fire!";
//        String shipSunk = "Enemy ship has been sunk!";
//
//        // Currently a simple implementation with a couple options that could be chosen at random
//        String[] playerHitEnemy = {"Enemy ship hit! Well done Sir.", "Straight into their hull!", "Direct Hit!"};
//        String[] playerMissedEnemy = {"Salvo Missed.", "Target not hit.", "Adjust your coordinates."};
//        String[] enemyHitPlayer = {"Enemy has hit our fleet,", "We've been hit!", "We're under fire!"};
//        String[] enemyMissedPlayer = {"Enemy has missed!", "Enemy missed another salvo.", "They missed us."};
//
//        // Generate a random number to pick a response string
//        Random rand = new Random();
//        int randomNum = rand.nextInt(3);
//
//        StringBuilder sb = new StringBuilder();
//        switch (gameState) {
//            case SHIP_PLACEMENT -> {
//                sb.append("\n\n").append(initialInstruction).append("\n").append(inputPending);
//            }
//            case CALC_PLAYER -> {
//                if (hit) {
//                    sb.append("\n\n").append(playerHitEnemy[randomNum]).append(" ").append(incoming);
//                } else {
//                    sb.append("\n\n").append(playerMissedEnemy[randomNum]).append(" ").append(incoming);
//                }
//            }
//            case CALC_ENEMY -> {
//                if (hit) {
//                    //TODO: append appropriate ship class value? or remove and leave just coordinates
//                    sb.append("\n").append(enemyHitPlayer[randomNum]);  //.append(" Ship-Class:").append(" Carrier");
//                    sb.append(" enemy fired at coordinates: [").append(coordinate).append("]");
//                    sb.append("\n\n").append(coordTag).append(" ").append(enterCoords).append("\n").append(inputPending);
//                } else {
//                    sb.append("\n").append(enemyMissedPlayer[randomNum]).append(" Salvo fired at: coordinates: [");
//                    sb.append(coordinate).append("]");
//                    sb.append("\n\n").append(coordTag).append(" ").append(enterCoords).append("\n").append(inputPending);
//                }
//            }
//            case SHIP_SUNK -> {
//                sb.append("\n").append(shipSunk);
//            }
//        }
////        player.updateMessageHistory(sb.toString());
//    }
//
//    /**
//     * Test function to print the current user's name to see if it could be implemented into the login framework
//     */
//    public void printUsername(){
//        // "USER" is where the user's name is stored for UNIX based systems
//        String username = System.getenv("USER");
//        // For Windows the above query will return null, as on windows the variable for the user is USERNAME
//        if (username == null) {
//            username = System.getenv("USERNAME");
//        }
//        System.out.println("Username: " + username);
//    }
//
//    /**
//     * Function to determine whether the player's input has hit a ship. Sets the CellType accordingly and returns
//     * true or false. The boolean value is used in another function to determine the response
//     *
//     * @param player current player - Board object
//     * @param row      horizontal coordinate
//     * @param col      vertical coordinate
//     * @return true if player has hit a ship, false if player hit water or previously missed cell
//     */
//    private boolean shotOutcome(Board player, int row, int col) {
//
//        // TODO Remove this after showing Nathan
//        printUsername(); // test call to printUsername function
//        // Get players current grid
//        Cell[][] grid = player.getGrid();
//        // Get cell type of player's coordinate
//        CellType currentState = grid[row][col].getCellType();
////        System.out.println("Cell Type: "+ currentState.toString());
//        // If player hit ocean set CellType to Miss and return false
//        if (currentState.equals(CellType.OCEAN)) {
//            player.setGridCell(row, col, CellType.MISS);
//            return false;
//            // If the Cell is a MISS cell, return false and check for Slow Learner Achievement
//        } else if (currentState.equals(CellType.MISS)) {
//            //no need to check for already unlocked as handler will do that
//            System.out.println("Slow Learner Achievement - Requirements met for " + playerName);
//            achievementHandler.unlockAchievement(playerName, SLOW_LEARNER.toString());
//            return false;
//        } else if (currentState.equals(CellType.HIT)) {
//            System.out.println("You Got Him Achievement - Requirements met for " + playerName);
//            achievementHandler.unlockAchievement(playerName, YOU_GOT_HIM.toString());
//            return true;
//        } else {
//            // If the cell is not an ocean, miss, or hit cell, set the cell to a "hit"
//            player.setGridCell(row, col, CellType.HIT);
//
//            // Update the ships to include the hit in their cells
//
//            HashMap<String, Ship> vessels = player.getVessels();
//
//            vessels.forEach((key, value) ->{
//                Ship current = value;
//                current.updateShipStatus(row, col);
//                vessels.replace(key, current);
//            });
//
//            player.setVessels(vessels);
//
//            if (sunk(player, row, col)) {
//                respondToInput(player, GameState.SHIP_SUNK, "",true);
//            }
//            return true;
//        }
//        // TODO: increment turn number?
//
//    }
//
//    private boolean sunk(Board player, int row, int col) {
//        Cell[][] grid = player.getGrid();
//        return true;
//    }
//
//    private int[] generateCoordinate(Board player) {
//
//        // Generate random coordinates
//        Random rand = new Random();
//        int randX = rand.nextInt(10);
//        int randY = rand.nextInt(10);
//
//        return new int[]{randX, randY};
//    }
//
//    /**
//     * Function to determine what to do with user input based on the current game state
//     *
//     * @param player current player - Board object
//     * @param input  user input from the console
//     */
//    private void calcUserInput(Board player, String input) {
//        // TODO: Tidy up
//        // Convert the input to uppercase
//        input = input.toUpperCase();
//        // Respond to the user's input in relation to game state
//        switch (player.getGameState()) {
//            case SHIP_PLACEMENT -> {
//                // Check if the users input was "ready", to start the game
//                if (input.matches("READY")) {
////                    player.updateMessageHistory("Ready");
//                    respondToInput(player, player.getGameState(), "", false);
//                    player.setGameState(GameState.INPUT_CALC);
//                }
//            }
//            case INPUT_CALC -> {
//
//                // Check if coordinates are valid, if so, format and add to message history
//                if (validate(input)) {
//                    Board playerBoard = new Board(0);
//                    Board CPUBoard = new Board(2);
//                    String validatedInput = formatInput(input);
////                    player.updateMessageHistory(validatedInput);
//                    player.setGameState(GameState.CALC_PLAYER);
//                    if (player.getGameState().equals(GameState.CALC_PLAYER)) {
//                        // Take user input and determine the result of shooting that coordinate
//                        String coordVert = validatedInput.split(",")[0];
//                        int row = chars.indexOf(coordVert);
//                        int col = Integer.parseInt(validatedInput.split(",")[1]);
//                        // Pass in the other players board to see if it hit their ship
//                        boolean result = shotOutcome(playerBoard, row, col);
//                        respondToInput(player, player.getGameState(), "", result);
//                        player.setGameState(GameState.CALC_ENEMY);
//                    }
//                    if (player.getGameState().equals(GameState.CALC_ENEMY)) {
//                        // Determine the result of enemy action
//                        int[] cpuCoord = generateCoordinate(CPUBoard);
//                        String cpuCoordStr = chars.charAt(cpuCoord[0]) + "," + cpuCoord[1];
//                        //System.out.println(cpuCoordStr);
//
//                        boolean result = shotOutcome(CPUBoard, cpuCoord[0], cpuCoord[1]);
//                        respondToInput(player, player.getGameState(), cpuCoordStr, result);
//                        player.setGameState(GameState.INPUT_CALC);
//                    }
//                }
//            }
//        }
//    }