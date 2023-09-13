package minigames.client.gameshow;

import java.awt.Image;
import java.awt.Insets;
import java.util.Collections;

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
public class GameShow implements GameClient {
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

    /** The current round for this player, zero-indexed */
    protected int round;
    JPanel gamePanel;
    GameTimer gameTimer;
    private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";
    private static final ImageIcon quitButton = new ImageIcon(dir + "quit-button.png");

    public static GameShow Main;

    public GameShow() {
        Main = this;
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

        logger.info("Joined GameShow '{}' as '{}'", new Object[] { game.name(), player });

    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.info("execute called with command: {}", command.getString("command"));

        switch (command.getString("command")) {
            case "nextRound" -> {
                logger.info("Starting minigame {}", command.getString("minigame"));
                this.round = command.getInteger("round");
                switch (command.getString("minigame")) {
                    case "ImageGuesser" -> {
                        this.gameTimer = new GameTimer(130000);
                        ImageGuesser.startImageGuesser(this, command.getString("imageFilePath"));
                        this.gameTimer.start();
                    }
                    case "WordScramble" -> {
                        this.gameTimer = new GameTimer(130000);
                        WordScramble.startGame(this, command.getString("letters"));
                        this.gameTimer.start();
                    }
                }
            }
            case "startGame" -> {
                switch (command.getString("game")) {
                    case "wordScramble" -> {
                        WordScramble.startGame(
                                this,
                                command.getString("letters"));
                    }
                }
            }
            case "guessOutcome" -> {
                switch (command.getString("game")) {
                    case "wordScramble" -> {
                        WordScramble.processGuess(
                                this,
                                command.getBoolean("correct"));
                    }
                }
            }
            case "startImageGuesser" -> {
                this.gameTimer = new GameTimer(130000);
                ImageGuesser.startImageGuesser(this, command.getString("imageFilePath"));
                this.gameTimer.start();
            }
            case "guessImageOutcome" -> {
                ImageGuesser.guess(this, command.getBoolean("outcome"));
            }
            case "ready" -> { // Log the ready state (for testing purposes)
                logger.info(
                        "Player '{}' is now ready: {}",
                        new Object[] { player, command.getBoolean("state")}
                );
            }
        }
    }

    @Override
    public void closeGame() {
        logger.info("Exited GameShow '{}' as '{}'", new Object[] { this.gm.name(), this.player });
    }

}
