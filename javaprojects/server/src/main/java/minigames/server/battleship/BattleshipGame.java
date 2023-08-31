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
    String activePlayer;    //the player whose turn it currently is
    String opponentPlayer; //the opponent player (not their turn yet)
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

        //if the game hasn't been aborted, we can move on to processing commands based on what the gamestate is
        switch (gameState) {
            case SHIP_PLACEMENT -> {
                if (userInput.equalsIgnoreCase("ready")) {
                    //set this player to ready
                    BattleshipPlayer currentClient = bPlayers.get(cp.player());
                    currentClient.setReady(true);

                    //if the other player is also ready or are AI, we are good to start the game
                    //the gamestate assumes all players are ready. On checking this loop, if we find any players that are
                    //not yet ready, set the state back to placement/waiting
                    gameState = GameState.IN_PROGRESS;
                    for (BattleshipPlayer player : bPlayers.values()) {
                        if (!(player.isReady() || player.isAIControlled())) {
                            gameState = GameState.SHIP_PLACEMENT;
                        }
                    }
                    //once all players are ready, pick a starting player and begin the game
                    if (gameState.equals(GameState.IN_PROGRESS)) {
                        Random rng = new Random();
                        //50% chance for either player to start first
                        int firstPlayer = rng.nextInt(0, 1);
                        String[] names = getPlayerNames();
                        if (names.length != 2) {
                            //todo if we're trying to start a game without 2 players, something has gone wrong - deal
                            // with error
                        }
                        //set up the first turn player and their opponent
                        activePlayer = names[firstPlayer];
                        opponentPlayer = names[1 - firstPlayer];

                        //both players can go into the 'wait' stage - they will refresh during IN_PROGRESS, and the
                        //player whose turn it is will be prompted shortly after
                        commands.add(new JsonObject().put("command", "wait"));

                    } else {
                        commands.add(new JsonObject().put("command", "waitReady"));
                    }
                }
            }
            case IN_PROGRESS -> {
                BattleshipPlayer current = bPlayers.get(activePlayer);
                BattleshipPlayer opponent = bPlayers.get(opponentPlayer);

                //No gameplay is progressed unless it is this players turn.
                if (!Objects.equals(current.getName(), cp.player())) {
                    return new RenderingPackage(gameMetadata(), commands);
                }

                //the refresh command is only sent by currently waiting clients
                if (userInput.equals("refresh")) {
                    //if we got past the above check, it is now this player's turn, inform them to switch to a
                    //prepare turn state
                    //todo need to implement the global turn counter. If this is turn 1 or 2, it will be the first
                    // turn for this player - so we should add the FirstInstruction() turn result message to the
                    // player's history here

                    //if (turn <=2) current.updateHistory(BattleShipTurnResult.firstInstruction().playerMessage());
                    commands.addAll(getGameRender(current, opponent));
                    commands.add(new JsonObject().put("command", "prepareTurn"));
                } else {
                    //if this was not a refresh request, we assume it's an input, so we can process the player's turn
                    commands = runGameCommand(current, opponent, userInput);
                }
                return new RenderingPackage(gameMetadata(), commands);
            }
            case GAME_OVER -> {
                //todo do game over stuff
            }
        }

        return new RenderingPackage(gameMetadata(), commands);
    }

    /**
     * Function called during gameplay for the current turn's player. Processes the player's input and
     * returns a shot result
     *
     * @param currentPlayer         the current player whose turn we wish to process
     * @param userInput the user input that client entered (should be a shot coordinate)
     * @return a list of commands to put in a rendering package
     */
    public ArrayList<JsonObject> runGameCommand(BattleshipPlayer currentPlayer, BattleshipPlayer opponent, String userInput) {
        //tell the player to take their turn - if they provided invalid input for any reason, this will
        //fail and they will be prompted to provide another input
        BattleshipTurnResult result = currentPlayer.processTurn(userInput, opponent.getBoard());

        //if the opponent is AI, do their turn immediately, otherwise switch turns to next player
        if (result.successful()) {
            // Update current player's message history with the result
            currentPlayer.updateHistory(result.playerMessage());
            // Determine response to other player based on their result
            if (result.shipHit()) {
                opponent.updateHistory(result.opponentMessage());
            } else {
                opponent.updateHistory(result.opponentMessage());
            }

            //if the opponent is ai, do their turn immediately. No turn changing required as the player can
            //just take their next turn right away, otherwise, switch the active and opponent players around
            if (opponent.isAIControlled()) {
                BattleshipTurnResult opponentResult = opponent.processAITurn(currentPlayer.getBoard());
                // System.out.println(opponentResult.message());
                currentPlayer.updateHistory(opponentResult.opponentMessage());
            } else {
                //swapping player turns
                String temp = activePlayer;
                activePlayer = opponentPlayer;
                opponentPlayer = temp;
            }
        }

        ArrayList<JsonObject> renderingCommands = new ArrayList<>(getGameRender(currentPlayer, opponent));
        renderingCommands.add(new JsonObject().put("command", "wait"));
        return renderingCommands;
    }

    /**
     * Helper function to get all of the commands related to redrawing the entire game screen, the boards, player names,
     * messages, etc
     *
     * @param targetPlayer the player who owns the client
     * @param opponent     the player's opponent
     * @return a list of json objects with the commands for rendering the main game
     */
    private ArrayList<JsonObject> getGameRender(BattleshipPlayer targetPlayer, BattleshipPlayer opponent) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "updateHistory")
                .put("history", targetPlayer.playerMessageHistory())
                .put("historyUpdated", targetPlayer.messageHistoryStatus()));
        renderingCommands.add(new JsonObject().put("command", "updatePlayerName")
                .put("player", targetPlayer.getName()));
        renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").
                put("text", Board.generateBoard(player, targetPlayer.getBoard().getGrid())));
        renderingCommands.add(new JsonObject().put("command", "placePlayer2Board")
                .put("text", Board.showEnemyBoard(enemy, opponent.getBoard().getGrid())));
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
        } else if (gameState.equals(GameState.IN_PROGRESS)) {
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