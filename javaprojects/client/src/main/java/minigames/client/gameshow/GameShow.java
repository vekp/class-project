package minigames.client.gameshow;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    private static final Logger logger = Logger.getLogger(GameShow.class.getName());

    MinigameNetworkClient mnClient;

    /**
     * We hold on to this because we'll need it when sending commands to the server
     */
    GameMetadata gm;

    /** Your name */
    String player;

    JButton imageButton;

    JPanel background;

    JPanel titleArea;
    JLabel title;

    JPanel homeScreen;

    JPanel gameArea;
    JPanel gameSelect;
    JLabel gameSelectInstructions;
    JPanel guessContainer;
    JPanel inputPanel;
    JPanel outcomeContainer;

    JButton wordScramble;
    JButton imageGuesserStart;
    JButton memoryGame;
    JButton guessingGame;

    int gameId;
    JPanel gamePanel;
    GameTimer gameTimer;

    public static GameShow Main;

    public GameShow() {
        Main = this;
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
        logger.log(Level.INFO, "Sending JSON: {0}", json.toString());
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

        logger.log(Level.INFO, "load executed for player: {0}, game: {1}", new Object[] { player, game.name() });

    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.log(Level.INFO, "execute called with command: {0}", command.getString("command"));

        switch (command.getString("command")) {
            case "startGame" -> {
                switch (command.getString("game")) {
                    case "wordScramble" -> {
                        WordScramble.startGame(
                                this,
                                command.getString("letters"),
                                (int) command.getInteger("gameId"));
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
                ImageGuesser.startImageGuesser(this, command.getString("imageFilePath"),
                        (int) command.getInteger("gameId"));
                this.gameTimer.start();
            }
            case "guessImageOutcome" -> {
                ImageGuesser.guess(this, command.getBoolean("outcome"));
            }
        }
    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}
