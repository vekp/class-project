package minigames.client.gameshow;

import minigames.client.gameshow.GridPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
// import javax.swing.JTextArea;
// import javax.swing.JTextField;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout; // Import FlowLayout from the java.awt package


import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




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
    JPanel inputPanel;
    JPanel outcomeContainer;

    JButton wordScramble;
    JButton imageGuesser;
    JButton memoryGame;
    JButton guessingGame;

    JPanel gamePanel;

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

        gameArea = new JPanel();
        gameArea.setLayout(new BoxLayout(gameArea, BoxLayout.Y_AXIS));
        gameArea.setBackground(new Color(255, 106, 210));

        wordScramble = new JButton("Word Scramble");
        wordScramble.setAlignmentX(Component.CENTER_ALIGNMENT);
        wordScramble.addActionListener((evt) -> WordScramble.welcome(this));
        gameArea.add(wordScramble);

        imageGuesser = new JButton("Image Guesser");
        imageGuesser.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageGuesser.addActionListener((evt) -> sendCommand(new JsonObject().put("command", "imageGuesser")));
        gameArea.add(imageGuesser);

        memoryGame = new JButton("Memory Game");
        memoryGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameArea.add(memoryGame);

        guessingGame = new JButton("Guess the Animal");
        guessingGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameArea.add(guessingGame);

        gameContainer.add(gameArea);

        background.add(gameContainer);

    }



    public void startImageGuesser(String imageFileName, int gameId) {
        gameContainer.removeAll();
        gameContainer.validate();
        gameContainer.repaint();


        String imageFolderLocation = "src/main/resources/images/memory_game_pics/" + imageFileName;
        ImageIcon imageIcon = new ImageIcon(imageFolderLocation);

        // Load the image
        JLabel imageLabel = new JLabel(imageIcon);

        GridPanel gridPanel = new GridPanel(imageIcon);

        Timer timer = new Timer(1000, e -> {
            boolean cellVisible = true;
            while (cellVisible) {
                Random random = new Random();
                int randomX = random.nextInt(10);
                int randomY = random.nextInt(10);
                if (!gridPanel.isCellVisible(randomX, randomY)) {
                    gridPanel.setFadeCell(randomX, randomY);
                    cellVisible = false; // Set to false to exit the loop when a non-visible cell is found
                }
            }
        });
        timer.start();


       // Create a panel for the guess input and submit button
        inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        outcomeContainer = new JPanel(new BorderLayout(10, 0));


        JPanel inputComponents = new JPanel(); // Create a container for fixed-size components
        inputComponents.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // You can adjust the layout manager as needed

        JTextField guessField = new JTextField(20);
        JButton submitButton = new JButton("Submit Guess");
        submitButton.addActionListener((evt) -> sendCommand(new JsonObject()
                .put("command", "guessImage")
                .put("guess", guessField.getText())
                .put("gameId", gameId)));

        inputComponents.add(guessField);
        inputComponents.add(submitButton);

        inputPanel.add(inputComponents, BorderLayout.CENTER); // Add the container with fixed-size components
        inputPanel.add(outcomeContainer, BorderLayout.SOUTH);
        // Add the grid panel to the center of the container
        gameContainer.add(gridPanel, BorderLayout.CENTER);
        gameContainer.add(inputPanel, BorderLayout.SOUTH);

        gameContainer.validate();
        gameContainer.repaint();
    }


    public void imageGuesserGuess(boolean correct) {
        if (!correct) {
            JLabel tryAgain = new JLabel("That's not quite right :( Try again!",
                    SwingConstants.CENTER);
            outcomeContainer.add(tryAgain, BorderLayout.CENTER);
        } else {
            outcomeContainer.removeAll();
            outcomeContainer.validate();
            outcomeContainer.repaint();
            JLabel congrats = new JLabel("Congratulations! You Win :)",
                    SwingConstants.CENTER);
            outcomeContainer.add(congrats, BorderLayout.CENTER);
        }
        logger.log(Level.INFO, "GameShow instance created");


        inputPanel.validate();
        inputPanel.repaint();
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
            case "startGame" -> {
                switch (command.getString("game")) {
                    case "wordScramble" -> {
                        WordScramble.startGame(
                            this,
                            command.getString("letters"),
                            (int) command.getInteger("gameId")
                        );
                    }
                }
            }
            case "guessOutcome" -> {
                switch (command.getString("game")) {
                    case "wordScramble" -> {
                        WordScramble.processGuess(
                            this,
                            command.getBoolean("correct")
                        );
                    }
                }
            }
            case "startImageGuesser" -> {
                this.startImageGuesser(
                        command.getString("imageFilePath"),
                        (int) command.getInteger("gameId"));
            }
            case "guessImageOutcome" -> {
                imageGuesserGuess(command.getBoolean("outcome"));
            }
        }
    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}
