package minigames.client.gameshow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;

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

    JPanel gameContainer;
    JPanel gameArea;
    JPanel gameSelect;
    JLabel gameSelectInstructions;
    JPanel guessContainer;

    JButton wordScramble;
    JButton memoryGame;
    JButton guessingGame;

    public GameShow() {
        background = new JPanel(new BorderLayout());
        background.setPreferredSize(new Dimension(800, 600));
        background.setBackground(Color.WHITE);

        titleArea = new JPanel(new BorderLayout());
        titleArea.setPreferredSize(new Dimension(800, 100));
        titleArea.setBackground(new Color(255, 255, 0));

        title = new JLabel("GAME SHOW", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.PLAIN, 60));

        titleArea.add(title, BorderLayout.CENTER);

        background.add(titleArea, BorderLayout.NORTH);

        gameContainer = new JPanel(new BorderLayout());
        gameContainer.setPreferredSize(new Dimension(700, 400));
        gameContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        gameContainer.setBackground(Color.WHITE);

        gameSelect = new JPanel(new BorderLayout());
        gameSelect.setPreferredSize(new Dimension(760, 40));
        gameSelect.setBackground(new Color(255, 106, 210));

        gameSelectInstructions = new JLabel("Select a game to play", SwingConstants.CENTER);
        gameSelectInstructions.setFont(new Font("Arial", Font.PLAIN, 24));

        gameSelect.add(gameSelectInstructions, BorderLayout.CENTER);

        gameArea = new JPanel(new BorderLayout(10, 10));
        gameArea.setBackground(new Color(255, 106, 210));

        gameArea.add(gameSelect, BorderLayout.NORTH);

        wordScramble = new JButton("Word Scramble");
        wordScramble.setPreferredSize(new Dimension(100, 50));
        wordScramble.addActionListener((evt) -> sendCommand(new JsonObject().put("command", "wordScramble")));

        gameArea.add(wordScramble, BorderLayout.NORTH);

        memoryGame = new JButton("Memory Game");
        memoryGame.setPreferredSize(new Dimension(100, 50));

        gameArea.add(memoryGame, BorderLayout.CENTER);

        guessingGame = new JButton("Guess the Animal");
        guessingGame.setPreferredSize(new Dimension(100, 50));

        gameArea.add(guessingGame, BorderLayout.SOUTH);

        gameContainer.add(gameArea);

        background.add(gameContainer);
    }

    public void startWordScramble(String scrambledWord, int gameId) {
        gameContainer.removeAll();
        gameContainer.validate();
        gameContainer.repaint();

        JPanel scrambledWordPanel = new JPanel(new BorderLayout());
        JLabel scrambled = new JLabel(scrambledWord, SwingConstants.CENTER);

        scrambledWordPanel.add(scrambled, BorderLayout.CENTER);

        gameContainer.add(scrambledWordPanel, BorderLayout.NORTH);

        guessContainer = new JPanel(new BorderLayout(50, 50));
        guessContainer.setBorder(new EmptyBorder(100, 100, 100, 100));

        JTextField guess = new JTextField(5);
        JButton sendGuess = new JButton("Submit guess");
        sendGuess.addActionListener((evt) -> sendCommand(new JsonObject()
                .put("command", "guess")
                .put("guess", guess.getText())
                .put("gameId", gameId)));

        guessContainer.add(guess, BorderLayout.CENTER);
        guessContainer.add(sendGuess, BorderLayout.SOUTH);

        gameContainer.add(guessContainer, BorderLayout.CENTER);
        gameContainer.validate();
        gameContainer.repaint();
    }

    public void wordScrambleGuess(boolean correct) {
        if (!correct) {
            JLabel tryAgain = new JLabel("That's not quite right :( Try again!",
                    SwingConstants.CENTER);
            guessContainer.add(tryAgain, BorderLayout.NORTH);
        } else {
            guessContainer.removeAll();
            guessContainer.validate();
            guessContainer.repaint();
            JLabel congrats = new JLabel("Congratulations! You Win :)",
                    SwingConstants.CENTER);
            guessContainer.add(congrats, BorderLayout.CENTER);
        }
        logger.log(Level.INFO, "GameShow instance created");


        guessContainer.validate();
        guessContainer.repaint();
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
        logger.log(Level.INFO, "sendCommand called with command: {0}", command);
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
        mnClient.getMainWindow().addCenter(background);

        // Don't forget to call pack - it triggers the window to resize and repaint
        // itself
        mnClient.getMainWindow().pack();
        logger.log(Level.INFO, "load executed for player: {0}, game: {1}", new Object[]{player, game.name()});

    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.log(Level.INFO, "execute called with command: {0}", command.getString("command"));

        switch (command.getString("command")) {
            case "startWordScramble" -> {
                this.startWordScramble(
                        command.getString("scrambledWord"),
                        (int) command.getInteger("gameId"));
            }
            case "guessOutcome" -> {
                wordScrambleGuess(command.getBoolean("outcome"));
            }
        }

    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}
