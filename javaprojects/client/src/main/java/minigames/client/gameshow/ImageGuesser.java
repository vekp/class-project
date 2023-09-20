package minigames.client.gameshow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import java.awt.Image;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * The ImageGuesser class is part of a larger game show application and interacts
 * with other components to facilitate gameplay, including loading and
 * revealing images, processing user guesses, and displaying game outcomes.
 *
 * The primary functions of this class include starting the image guessing
 * game, handling user input, revealing the entire image, and managing game
 * rounds and outcomes.
 */
public class ImageGuesser {

    // Logger for debugging and logging messages
    public static final Logger logger = Logger.getLogger(ImageGuesser.class.getName());

    // Directory path for game show images
    public final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";

    // ImageIcon for the submit button
    public static final ImageIcon submitButtonButton = new ImageIcon(dir + "submit-button.png");

    // GridPanel to display the game grid
    public static GridPanel gridPanel;

    // JTextField for user input
    public static JTextField guessField;

    // Boolean flag to indicate if the game is running
    public static boolean gameRunning = true;

    /**
     * Starts the Image Guesser game with the specified image file.
     *
     * @param gs           The GameShow instance.
     * @param imageFileName The name of the image file to be displayed.
     */
    public static void startImageGuesser(GameShow gs, String imageFileName) {
        // Clear the game container
        clearGameContainer();

        // Load and display the specified image
        loadAndDisplayImage(imageFileName);

        // Start the timer for cell visibility
        startCellVisibilityTimer(gs);

        // Create the input panel for user guesses
        createInputPanel(gs);

        // Add components to the game container
        addComponentsToGameContainer(gs);

        // Validate and repaint the game container
        validateAndRepaintGameContainer();
    }

    /**
     * Clears the game container by removing all components.
     */
    public static void clearGameContainer() {
        GameShowUI.gameContainer.removeAll();
        GameShowUI.gameContainer.validate();
        GameShowUI.gameContainer.repaint();
    }

    /**
     * Loads and displays the specified image on the grid panel.
     *
     * @param imageFileName The name of the image file to be loaded.
     */
    public static void loadAndDisplayImage(String imageFileName) {
        // Construct the path to the image file
        String imageFolderLocation = "src/main/resources/images/memory_game_pics/" + imageFileName;

        // Create an ImageIcon from the image file
        ImageIcon imageIcon = new ImageIcon(imageFolderLocation);

        // Create a JLabel with the loaded image
        JLabel imageLabel = new JLabel(imageIcon);

        // Initialize the grid panel with the image
        gridPanel = new GridPanel(imageIcon, 10, 10);
        gridPanel.setPreferredSize(new Dimension(400, 215));

        // Add the grid panel to the game container
        GameShowUI.gameContainer.add(gridPanel, BorderLayout.CENTER);
    }

    /**
     * Reveals the entire image by making all cells visible.
     */
    public static void showWholeImage() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (!gridPanel.isCellVisible(x, y)) {
                    gridPanel.setFadeCell(x, y);
                }
            }
        }
    }

    /**
     * Starts a timer to periodically make random cells visible.
     *  @param gs           The GameShow instance.
     */
    public static void startCellVisibilityTimer(GameShow gs) {
        long timeLimit = gs.gameTimer.getTimeLimit();
        int rows = gridPanel.getRowCount();
        int cols = gridPanel.getColCount();

        // Divide timeLimit by rows * cols
        long interval = timeLimit / (rows * cols);
        int intInterval = (int) interval;

        gameRunning = true;
        Timer timer = new Timer(intInterval, e -> {
            if (gameRunning) {
                Random random = new Random();
                int randomX;
                int randomY;
                boolean cellVisible = false;
                // Keep generating random coordinates until we find a non-visible cell
                while (!cellVisible) {
                    randomX = random.nextInt(rows);
                    randomY = random.nextInt(cols);
                    if (!gridPanel.isCellVisible(randomX, randomY)) {
                        gridPanel.setFadeCell(randomX, randomY);
                        cellVisible = true;
                    }
                }
            }
        });
        timer.start();
    }
    /**
     * Creates the input panel for user guesses and adds it to the GameShow instance.
     *
     * @param gs The GameShow instance.
     */
    public static void createInputPanel(GameShow gs) {
        gs.inputPanel = new JPanel(new BorderLayout(10, 0));
        gs.inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        gs.outcomeContainer = new JPanel(new BorderLayout(10, 0));

        JPanel inputComponents = new JPanel();
        inputComponents.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // Create the guess input field
        guessField = createGuessField();

        // Create the submit button
        JButton submitButton = createSubmitButton(gs);

        // Add input components to the input panel
        inputComponents.add(guessField);
        inputComponents.add(submitButton);

        // Add components to the input panel
        gs.inputPanel.add(gs.gameTimer, BorderLayout.NORTH);
        gs.inputPanel.add(inputComponents, BorderLayout.CENTER);
        gs.inputPanel.add(gs.outcomeContainer, BorderLayout.SOUTH);
    }

    /**
     * Creates a JTextField for user guesses and sets its properties.
     *
     * @return The created JTextField.
     */
    public static JTextField createGuessField() {
        JTextField guessField = new JTextField(20);

        // Set the font for the guess field
        Font pixelFont = GameShowUI.pixelFont;
        guessField.setFont(pixelFont.deriveFont(18f));

        return guessField;
    }

    /**
     * Creates a submit button with an image and sets its properties.
     *
     * @param gs The GameShow instance.
     * @return The created submit button.
     */
    public static JButton createSubmitButton(GameShow gs) {
        JButton submitButton = new JButton(
                new ImageIcon(submitButtonButton.getImage().getScaledInstance(150, 40, Image.SCALE_DEFAULT)));
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);

        // Add an action listener to the submit button
        submitButton.addActionListener((evt) -> sendGuess(gs, guessField.getText()));

        return submitButton;
    }

    /**
     * Adds the grid panel to the center of the game container.
     *
     * @param gs The GameShow instance.
     */
    public static void addComponentsToGameContainer(GameShow gs) {
        JPanel revealHeader = GameShowUI.generateRevealHeader();
        GameShowUI.gameContainer.add(revealHeader, BorderLayout.NORTH);
        GameShowUI.gameContainer.add(gs.inputPanel, BorderLayout.SOUTH);
    }

    /**
     * Validates and repaints the game container to update its display.
     */
    public static void validateAndRepaintGameContainer() {
        GameShowUI.gameContainer.validate();
        GameShowUI.gameContainer.repaint();
    }

    /**
     * Handles the user's guess in the game.
     *
     * @param gs      The GameShow instance.
     * @param correct Indicates whether the guess is correct or not.
     */
    public static void guess(GameShow gs, boolean correct) {
        // Clear the outcome container
        clearOutcomeContainer(gs);

        if (!correct) {
            // Display a message for an incorrect guess
            displayTryAgainMessage(gs);
        } else {
            // Handle a correct guess
            handleCorrectGuess(gs);
        }

        // Validate and repaint the input panel
        validateAndRepaintInputPanel(gs);
    }
    /**
     * Clears the outcome container by removing all components.
     *
     * @param gs The GameShow instance.
     */
    public static void clearOutcomeContainer(GameShow gs) {
        gs.outcomeContainer.removeAll();
        gs.outcomeContainer.validate();
        gs.outcomeContainer.repaint();
    }

    /**
     * Displays a "Try again" message in the outcome container.
     *
     * @param gs The GameShow instance.
     */
    public static void displayTryAgainMessage(GameShow gs) {
        JLabel tryAgain = new JLabel("That's not quite right :( Try again!",
                SwingConstants.CENTER);
        Font pixelFont = GameShowUI.pixelFont;
        tryAgain.setFont(pixelFont.deriveFont(15f));
        gs.outcomeContainer.add(tryAgain, BorderLayout.CENTER);
    }

    /**
     * Handles a correct guess by stopping the game timer, revealing the entire image,
     * and displaying a congratulatory message.
     *
     * @param gs The GameShow instance.
     */
    public static void handleCorrectGuess(GameShow gs) {
        gs.gameTimer.stop();
        gameRunning = false;
        showWholeImage();

        // Calculate the remaining time as the score
        int remainingTime = gs.gameTimer.calculateScore();
        logger.log(Level.INFO, "Remaining Time: " + remainingTime);

        // Display a congratulatory message
        JLabel congrats = new JLabel("Congratulations! You Win :)",
                SwingConstants.CENTER);
        Font pixelFont = GameShowUI.pixelFont;
        congrats.setFont(pixelFont.deriveFont(15f));
        gs.outcomeContainer.add(congrats, BorderLayout.CENTER);

        // Create a button for the next round
        JButton nextRoundButton = new JButton("Next round ->");
        nextRoundButton.addActionListener((evt) -> {
            gs.sendCommand(new JsonObject().put("command", "nextRound").put("round", gs.round + 1));
        });
        gs.outcomeContainer.add(nextRoundButton, BorderLayout.PAGE_END);
    }

    /**
     * Validates and repaints the input panel to update its display.
     *
     * @param gs The GameShow instance.
     */
    public static void validateAndRepaintInputPanel(GameShow gs) {
        gs.inputPanel.validate();
        gs.inputPanel.repaint();
    }

    /**
     * Sends the user's guess to the GameShow instance.
     *
     * @param gs    The GameShow instance.
     * @param guess The user's guess as a String.
     */
    public static void sendGuess(GameShow gs, String guess) {
        gs.sendCommand(new JsonObject()
                .put("command", "guess")
                .put("game", "ImageGuesser")
                .put("guess", guess)
                .put("round", gs.round)
                .put("score", gs.gameTimer.calculateScore()));
    }
}
