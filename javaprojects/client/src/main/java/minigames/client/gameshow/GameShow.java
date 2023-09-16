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
 * A very simple interface for a text-based game.
 *
 * It understands three commands:
 * { "command": "clearText" } to clear the contents of the text area
 * { "command": "appendText", "text": text } to add contents to the text area
 * { "command": "setDirections", "directions": directions} to enable/disable the
 * N, S, E, W buttons
 * depending on whether the directions string contains "N", "S", "E", "W"
 * (e.g. { "command": "setDirections", "directions": "NS" } would enable only N
 * and S)
 */
public class GameShow implements GameClient, Tickable {
    private static final Logger logger = LogManager.getLogger(GameShow.class);

    static MinigameNetworkClient mnClient;

    /**
     * We hold on to this because we'll need it when sending commands to the server
     */
    GameMetadata gm;

    /** Your name */
    String player;

    JPanel homeScreen;

    JPanel inputPanel;
    JPanel outcomeContainer;

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

    public GameShow() {
        Main = this;
        this.lastPoll = System.nanoTime();
        this.started = false;
    }

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
     * Sends a command to the game at the server.
     * This being a text adventure, all our commands are just plain text strings our
     * gameserver will interpret.
     * We're sending these as
     * { "command": command }
     */
    public void sendCommand(JsonObject json) {
        // Collections.singletonList() is a quick way of getting a "list of one item"
        // logger.log(Level.INFO, "sendCommand called with command: {0}", command);
        logger.info("Sending JSON: {}", json.toString());
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), this.player, Collections.singletonList(json)));
    }

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {

        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        // Add our components to the north, south, east, west, or centre of the main
        // window's BorderLayout
        homeScreen = GameShowUI.generateIntroPanel(this);
        mnClient.getMainWindow().addCenter(homeScreen);
        mnClient.getMainWindow().pack();
        // mnClient.getMainWindow().setVisible(true)

        mnClient.getAnimator().requestTick(this); // Start receiving ticks

        logger.info("Joined GameShow '{}' as '{}'", new Object[] { game.name(), player });

    }

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
            case "WordScramble":
                startGame(command);
                break;
            default:
                logger.warn("Unknown game: {}", game);
        }

        this.gameTimer.start();
    }

    private void startGame(JsonObject command) {
        // String game = command.getString("game");
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

    private void logReadyState(JsonObject command) {
        logger.info(
                "Player '{}' is now ready: {}",
                new Object[] { player, command.getBoolean("state")}
        );
    }


    @Override
    public void closeGame() {
        logger.info("Exited GameShow '{}' as '{}'", new Object[] { this.gm.name(), this.player });
    }

    /** The client polls the server once per second in order to detect when the game has started */
    @Override
    public void tick(Animator al, long now, long delta) {
        if (!this.started) {
            if (now - this.lastPoll > 1000000000) {
                this.lastPoll = now;
                sendCommand(new JsonObject().put("command", "allReady"));
            }

            al.requestTick(this);
        }
    }

}
