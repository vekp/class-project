package minigames.client.gameshow;

import java.awt.Image;
import java.awt.Insets;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;


import minigames.client.Animator;
import minigames.client.Tickable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

/**
 * The `GameShow` class represents a text-based game client for a game show minigame.
 * It provides a simple interface for interacting with the game and communicates with the server.
 */
public class GameShow implements GameClient, Tickable {
    private static final Logger logger = LogManager.getLogger(GameShow.class);

    static MinigameNetworkClient mnClient;

    /**
     * We hold on to this because we'll need it when sending commands to the server
     */
    GameMetadata gm;

    /** The name of the player */
    String player;

    JPanel homeScreen;

    JPanel inputPanel;
    JPanel outcomeContainer;

    /** Stores whether the game is active on the client */
    private boolean isActive;
    /** Whether the game has started */
    private boolean started;
    /** When the last server poll occurred */
    private long lastPoll;
    /** The current round for this player, zero-indexed */
    protected int round;
    JPanel gamePanel;
    GameTimer gameTimer;
    private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";
    private static final ImageIcon quitButton = new ImageIcon(dir + "quit-button.png");

    public static GameShow Main;

    /**
     * Initializes a new instance of the `GameShow` class.
     */
    public GameShow() {
        Main = this;
        this.lastPoll = System.nanoTime();
        this.isActive = true;
        this.started = false;
    }

    /**
     * Creates a "Quit" button for the game.
     *
     * @return A JButton representing the "Quit" button.
     */
    public static JButton quit() {
        JButton quitGame;

        quitGame = new JButton(
                new ImageIcon(quitButton.getImage().getScaledInstance(100, 20, Image.SCALE_DEFAULT)));
        quitGame.setContentAreaFilled(false);
        quitGame.setFocusPainted(false);
        quitGame.setBorderPainted(false);
        quitGame.setMargin(new Insets(0, 650, 0, 0));
        quitGame.addActionListener(e -> Main.sendCommand(new JsonObject().put("command", "quit")));

        return quitGame;
    }

    /**
     * Sends a command to the game server.
     *
     * @param json The JSON object representing the command to send.
     */
    public void sendCommand(JsonObject json) {
        logger.info("Sending JSON: {}", json.toString());
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), this.player, Collections.singletonList(json)));
    }

    /**
     * Loads the game client into the main screen and prepares it for gameplay.
     *
     * @param mnClient The MinigameNetworkClient used for communication.
     * @param game     The game metadata.
     * @param player   The name of the player.
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {

        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        homeScreen = GameShowUI.generateIntroPanel(this);
        mnClient.getMainWindow().addCenter(homeScreen);
        mnClient.getMainWindow().pack();

        mnClient.getAnimator().requestTick(this); // Start receiving ticks

        logger.info("Joined GameShow '{}' as '{}'", new Object[] { game.name(), player });

    }

    /**
     * Executes a game command received from the server.
     *
     * @param game    The game metadata.
     * @param command The JSON object representing the command to execute.
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.info("execute called with command: {}", command.getString("command"));

        Map<String, Runnable> commandHandlers = new HashMap<>();
        commandHandlers.put("nextRound", () -> startNextRound(command));
        commandHandlers.put("startGame", () -> startGame(command));
        commandHandlers.put("guessOutcome", () -> processGuessOutcome(command));
        commandHandlers.put("ready", () -> logReadyState(command));

        String commandString = command.getString("command");
        if (commandHandlers.containsKey(commandString)) {
            commandHandlers.get(commandString).run();
        } else {
            logger.warn("Unknown command: {}", commandString);
        }
    }

    /**
     * Starts the next round of the game.
     *
     * @param command The JSON object representing the command to start the next round.
     */
    private void startNextRound(JsonObject command) {
        logger.info("Starting minigame {}", command.getString("game"));
        logger.info("Starting round {}", command.getInteger("round"));
        this.started = true;
        this.round = command.getInteger("round");
        String game = command.getString("game");
        this.gameTimer = new GameTimer(130000);

        switch (game) {
            case "ImageGuesser":
                startGame(command);
                break;
            case "WordScramble":
                startGame(command);
                break;
            default:
                logger.warn("Unknown game: {}", game);
        }

        this.gameTimer.start();
    }

    /**
     * Starts a specific game based on the provided command.
     *
     * @param command The JSON object representing the command to start the game.
     */
    private void startGame(JsonObject command) {
        String game = command.getString("game");
        this.started = true;
        switch (game) {
            case "WordScramble":
                WordScramble.startGame(this, command.getString("letters"));
                break;
            case "ImageGuesser":
                this.gameTimer = new GameTimer(130000);
                ImageGuesser.startImageGuesser(this, command.getString("imageFilePath"));
                this.gameTimer.start();
                break;
            default:
                logger.warn("Unknown game: {}", game);
        }
    }

    /**
     * Processes the outcome of a guess in the game.
     *
     * @param command The JSON object representing the command with the guess outcome.
     */
    private void processGuessOutcome(JsonObject command) {
        String game = command.getString("game");
        switch (game) {
            case "WordScramble":
                WordScramble.processGuess(this, command.getBoolean("correct"));
                break;
            case "ImageGuesser":
                ImageGuesser.guess(this, command.getBoolean("correct"));
                break;
            default:
                logger.warn("Unknown game: {}", game);
        }
    }

    /**
     * Logs the ready state of a player.
     *
     * @param command The JSON object representing the command with the player's ready state.
     */
    private void logReadyState(JsonObject command) {
        logger.info(
                "Player '{}' is now ready: {}",
                new Object[] { player, command.getBoolean("state")}
        );
    }

    /**
     * Closes the game client and exits the game.
     */
    @Override
    public void closeGame() {
        this.isActive = false;
        logger.info("Exited GameShow '{}' as '{}'", new Object[] { this.gm.name(), this.player });
    }

    /**
     * GameShow clients poll the server once per second in order to detect when the game has started.
     * To reduce server load, clients will poll only if
     *   - the game is active on the client
     *   - the game has not already started
     */
    @Override
    public void tick(Animator al, long now, long delta) {
        if (this.isActive && !this.started) {
            if (now - this.lastPoll > 1000000000) {
                this.lastPoll = now;
                sendCommand(new JsonObject().put("command", "allReady"));
            }

            al.requestTick(this);
        }
    }

}
