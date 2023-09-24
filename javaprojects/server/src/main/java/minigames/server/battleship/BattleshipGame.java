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
 * Main game glass for a battleship instance.
 * Games are run with 2 players. When the first player joins, they will sit and wait until either a 2nd player joins, or
 * they enter the 'start' command to play against the AI
 *
 * This class uses a state pattern to keep track of the various game stages (Waiting for Player, Waiting to start game,
 * game running, game over). It is also responsible for holding the main 'pieces' of the game - the players (who in
 * turn manage their game boards).
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
    int gameTurn = 1; //game starts at turn 1, and increments every time 2 players have both taken their turns
    int turnSwitchCounter = 0; //counter to help increment the game turn every 2nd player switch

    static final String player = "Nautical Map";
    static final String enemy = "Target Map";
    static String welcomeMessage = """
            Good evening Captain! Enter 'Ready' to start conquering the seas!
            ...""";
    public static String chars = "ABCDEFGHIJ";

    // Constructor

    /**
     * @param gameName   Name of the game session
     * @param playerName Name of the host player
     */
    public BattleshipGame(String gameName, String playerName) {
        this.gameName = gameName;
        this.achievementHandler = new AchievementHandler(BattleshipServer.class);
        this.playerName = playerName;
        this.gameState = GameState.WAITING_JOIN;
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
        if (bPlayers.size() > 0)
            return bPlayers.keySet().toArray(String[]::new);
        else return new String[0];
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
     * @return gets the state of the current game
     */
    public GameState getGameState() {
        return this.gameState;
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
        BattleshipPlayer currentClient = bPlayers.get(cp.player());
        BattleshipPlayer current = bPlayers.get(activePlayer);
        BattleshipPlayer opponent = bPlayers.get(opponentPlayer);

        // Handle exit game commands
        if (userInput.equals("exitGame")) {
            System.out.println("This should exit that game");
            // Remove the exiting player. This will leave just 1 player (if it is a human player), and when they
            // send their own command package, they will hit the below else condition and be told that this player
            // has left
            bPlayers.remove(cp.player());

            return new RenderingPackage(gameMetadata(), commands);

        } else if (bPlayers.size() < 2 && gameState != GameState.WAITING_JOIN) {
            // Here is where a player has left a running game
            // to pop up a window saying other player has left
            // also just return a render package immediately here as the game is over
            commands.add(new JsonObject().put("command", "playerExited"));
            return new RenderingPackage(gameMetadata(), commands);
        }

        // If the game hasn't been aborted, we can move on to processing commands based on what the gamestate is
        switch (gameState) {
            case WAITING_JOIN -> {
                // Once 2 players are in the game, we can start
                if (bPlayers.size() >= 2) {
                    //we have 2 players available, set this player to ready
                    bPlayers.get(cp.player()).setReady(true);
                    //if both players are ready then we can start
                    String[] players = getPlayerNames();
                    if (bPlayers.get(players[0]).isReady() && bPlayers.get(players[1]).isReady()) {
                        //resetting ready flags so they dont immediately start game
                        bPlayers.get(players[0]).setReady(false);
                        bPlayers.get(players[1]).setReady(false);
                        gameState = GameState.PENDING_READY;
                    } else {
                        //if both players aren't ready then send the game render back so that both players
                        //get some graphics to draw while the game is transitioning between states
                        commands.addAll(getGameRender(cp.player()));
                    }
                } else {
                    if (userInput.equalsIgnoreCase("Start")) {
                        // If user enters start and there isn't another player, make a computer one
                        BattleshipPlayer computerPlayer = new BattleshipPlayer("Computer",
                                new Board(), false, welcomeMessage);
                        computerPlayer.setReady(true);
                        bPlayers.put("Computer", computerPlayer);
                        gameState = GameState.PENDING_READY;
                        commands.addAll(getGameRender(cp.player()));
                    } else {
                        commands.add(new JsonObject().put("command", "waitForJoin"));
                    }
                }
                return new RenderingPackage(gameMetadata(), commands);
            }
            case PENDING_READY -> {
                if (userInput.equalsIgnoreCase("ready")) {
                    // Set this player to ready
                    currentClient.setReady(true);
                    currentClient.updateHistory("Ready");
                    currentClient.updateHistory(BattleshipTurnResult.firstInstruction().playerMessage());

                    // If the other player is also ready or are AI, we are good to start the game
                    // the gamestate assumes all players are ready. On checking this loop, if we find any players that are
                    // not yet ready, set the state back to placement/waiting
                    gameState = GameState.IN_PROGRESS;
                    for (BattleshipPlayer player : bPlayers.values()) {
                        if (!(player.isReady() || player.isAIControlled())) {
                            gameState = GameState.PENDING_READY;
                        }
                    }
                    // Once all players are ready, pick a starting player and begin the game
                    if (gameState.equals(GameState.IN_PROGRESS)) {
                        Random rng = new Random();
                        // 50% chance for either player to start first
                        int firstPlayer = rng.nextInt(0, 2);
                        String[] names = getPlayerNames();
                        if (names.length != 2) {
                            //we should never be able to get here, but if we've somehow gotten in here without 2 players
                            //we will just go back to the 'waiting for players to join' state
                            gameState = GameState.WAITING_JOIN;
                            break;
                        }
                        // Set up the first turn player and their opponent
                        activePlayer = names[firstPlayer];
                        opponentPlayer = names[1 - firstPlayer];


                    }
                    // Both players can go into the 'wait' stage - they will refresh during IN_PROGRESS, and the
                    // player whose turn it is will be prompted shortly after
                    commands.add(new JsonObject().put("command", "wait"));
                }
            }
            case IN_PROGRESS -> {
                current.getBoard().setGameState(GameState.IN_PROGRESS);
                opponent.getBoard().setGameState(GameState.IN_PROGRESS);

                if (Objects.equals(current.getName(), cp.player())) {
                    // On this users turn, we process any non-refresh commands as an input coordinate
                    if (!userInput.equals("refresh")) {
                        commands = runGameCommand(current, opponent, userInput);
                    } else {
                        // If this was a refresh command, we tell the player it is now their turn and they can
                        // send coordinates over
                        commands.addAll(getGameRender(current, opponent));
                        // We add the game turn too, as the client will display an intro message on the player's
                        // first turn
                        commands.add(new JsonObject()
                                .put("command", "prepareTurn")
                                .put("turnCount", gameTurn));
                    }
                } else {
                    // If it is not this player's turn, but the opponent is an AI, we do the opponents turn and
                    // send the result
                    if (current.isAIControlled()) {
                        // This should be guaranteed to give a successful turn because the AI cant enter invalid input
                        BattleshipTurnResult AIResult = current.processAITurn(opponent.getBoard());
                        opponent.updateHistory(AIResult.opponentMessage());
                        SwapTurns();
                        //current and opponent need to be swapped here, because 'opponent' is actually our player
                        //during the AI's turn
                        commands.addAll(getGameRender(opponent, current));
                        //since it is now the player's turn, they can immediately prepare to take input
                        commands.add(new JsonObject().put("command", "prepareTurn"));
                    }
                    //no other commands run here, player will continue waiting until their turn
                }

                if (opponent.getBoard().checkGameOver(current.getName()) || current.getBoard().checkGameOver(opponent.getName())) {
                    this.gameState = GameState.GAME_OVER;
                    commands.addAll(getGameRender(current, opponent));
                    commands.addAll(getGameOverMessaging(current, opponent));
                }

                return new RenderingPackage(gameMetadata(), commands);
            }
            case GAME_OVER -> {
                commands.addAll(getGameRender(current, opponent));
                commands.addAll(getGameOverMessaging(current, opponent));

                return new RenderingPackage(gameMetadata(), commands);
            }
        }

        return new RenderingPackage(gameMetadata(), commands);
    }

    /**
     * Function called during gameplay for the current turn's player. Processes the player's input and
     * returns a shot result
     *
     * @param currentPlayer the current player whose turn we wish to process
     * @param userInput     the user input that client entered (should be a shot coordinate)
     * @return a list of commands to put in a rendering package
     */
    public ArrayList<JsonObject> runGameCommand(BattleshipPlayer currentPlayer, BattleshipPlayer opponent, String userInput) {

        //tell the player to take their turn - if they provided invalid input for any reason, this will
        //fail, and they will be prompted to provide another input
        BattleshipTurnResult result = currentPlayer.processTurn(userInput, opponent.getBoard());

        // Update current player's message history with the result
        currentPlayer.updateHistory(result.playerMessage());

        //the commands to be sent back to the player
        ArrayList<JsonObject> renderingCommands = new ArrayList<>(getGameRender(currentPlayer, opponent));

        //if the turn wasn't successful, there was an invalid input, the player will stay on their turn until
        //they enter a valid coordinate
        if (result.successful()) {
            //opponent should now get feedback about the player's turn
            opponent.updateHistory(result.opponentMessage());
            //we can now swap turns and set the player to wait for their next turn
            SwapTurns();
            renderingCommands.add(new JsonObject().put("command", "wait"));
        }

        return renderingCommands;
    }


    /**
     * Helper function to get all the commands related to redrawing the entire game screen, the boards, player names,
     * messages, etc
     *
     * @param targetPlayer the player who owns the client
     * @param opponent     the player's opponent
     * @return a list of json objects with the commands for rendering the main game
     */
    private ArrayList<JsonObject> getGameRender(BattleshipPlayer targetPlayer, BattleshipPlayer opponent) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject()
                .put("command", "updateHistory")
                .put("history", targetPlayer.playerMessageHistory())
                .put("historyUpdated", targetPlayer.messageHistoryStatus()));
        renderingCommands.add(new JsonObject()
                .put("command", "updatePlayerName")
                .put("player", targetPlayer.getName()));
        renderingCommands.add(new JsonObject()
                .put("command", "placePlayer1Board")
                .put("text", targetPlayer.getBoard().generateBoard(player, false)));
        renderingCommands.add(new JsonObject()
                .put("command", "placePlayer2Board")
                .put("text", opponent.getBoard().generateBoard(enemy, true)));
        renderingCommands.add(new JsonObject()
                .put("command", "updateTurnCount")
                .put("turnCount", gameTurn));
        return renderingCommands;
    }

    /**
     * Alternate helper method to get the correct game render commands using just 1 player name
     *
     * @param playerName the player whos screen we want to render
     * @return the board and message data for this player
     */
    private ArrayList<JsonObject> getGameRender(String playerName) {
        String[] players = getPlayerNames();
        if (players[0].equals(playerName)) {
            return getGameRender(bPlayers.get(players[0]), bPlayers.get(players[1]));
        } else {
            return getGameRender(bPlayers.get(players[1]), bPlayers.get(players[0]));
        }
    }

    /**
     * Will set up message commands for the game over state, depending on whether the opponent won, or the player
     * won
     * @param player Current player (client who is asking for this info)
     * @param opponent The opposite player
     * @return The messaging instructions with appropriate Win/Lose message
     */
    private ArrayList<JsonObject> getGameOverMessaging(BattleshipPlayer player, BattleshipPlayer opponent) {
        ArrayList<JsonObject> commands = new ArrayList<>();
        String gameOverMessage = "";
        if (opponent.getBoard().isBoardDead()) {
            gameOverMessage = "Congratulations, you win! Please exit to menu to try another game";
        } else if (player.getBoard().isBoardDead()) {
            gameOverMessage = "You have lost! Please exit the menu to try another game";
        }

        commands.add(new JsonObject().put("command", "gameOver")
                .put("message", gameOverMessage));
        return commands;
    }

    /**
     * Helper function to swap turns to the other player
     */
    void SwapTurns() {
        //every 2nd swap, we will have completed 2 player turns, so increment the game turn number
        gameTurn += turnSwitchCounter % 2;
        turnSwitchCounter++;

        String temp = activePlayer;
        activePlayer = opponentPlayer;
        opponentPlayer = temp;
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
        //once a game has started, nobody can join it
        if (gameState != GameState.WAITING_JOIN) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("This game has already started")
                    }).map(RenderingCommand::toJson).toList()
            );
        }
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new LoadClient("Battleship", "Battleship", gameName, playerName).toJson());
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "updateHistory").put("history", welcomeMessage));
        renderingCommands.add(new JsonObject().put("command", "updatePlayerName").put("player", playerName));
        renderingCommands.add(new JsonObject().put("command", "turnCountGameStart").put("turnCount", "- Place Ships -"));

        String[] currentPlayers = getPlayerNames();
        BattleshipPlayer player; //this always either gets set or we return before using it, no need to create one

        //this is a 'dummy' opponent so that we have at least a blank board to render. It will be replaced by
        //an active player (in the case that this is player 2 joining)
        BattleshipPlayer opponent = new BattleshipPlayer("", new Board(), false, welcomeMessage);

        if (currentPlayers.length == 0) {
            BattleshipPlayer newPlayer = new BattleshipPlayer(playerName,
                    new Board(), true, welcomeMessage);
            player = newPlayer;
            bPlayers.put(playerName, newPlayer);
            renderingCommands.add(new JsonObject().put("command", "waitForJoin"));

        } else if (currentPlayers.length == 1) {
            if (currentPlayers[0].equals(playerName)) {
                //if the player is trying to double-join their own game, this should fail
                return new RenderingPackage(
                        gameMetadata(),
                        Arrays.stream(new RenderingCommand[]{
                                new NativeCommands.ShowMenuError("You are already in this game!")
                        }).map(RenderingCommand::toJson).toList()
                );
            }
            //we can add a 2nd player as long as it's not the same player joining twice
            BattleshipPlayer newPlayer2 = new BattleshipPlayer(playerName,
                    new Board(), true, welcomeMessage);
            //the opponent for the joining player is the player that is already here
            opponent = bPlayers.get(currentPlayers[0]);
            player = newPlayer2;
            bPlayers.put(playerName, newPlayer2);
        } else {
            //shouldn't be able to get here as the game should start once 2 players join, but just
            //tell the user that the game has already started
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("This game has already started")
                    }).map(RenderingCommand::toJson).toList()
            );
        }
        renderingCommands.addAll(getGameRender(player, opponent));

        return new RenderingPackage(gameMetadata(), renderingCommands);
    }
}