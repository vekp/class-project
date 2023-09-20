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

public class ImageGuesser {

    private static final Logger logger = Logger.getLogger(GameShow.class.getName());
    private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/";
    private static final ImageIcon submitButtonButton = new ImageIcon(dir + "submit-button.png");
    private static GridPanel gridPanel;
    private static JTextField guessField;
    private static boolean gameRunning = true;

    public static void startImageGuesser(GameShow gs, String imageFileName) {
        clearGameContainer();
        loadAndDisplayImage(imageFileName);
        startCellVisibilityTimer();
        createInputPanel(gs);
        addComponentsToGameContainer(gs);
        validateAndRepaintGameContainer();
    }

    private static void clearGameContainer() {
        GameShowUI.gameContainer.removeAll();
        GameShowUI.gameContainer.validate();
        GameShowUI.gameContainer.repaint();
    }

    private static void loadAndDisplayImage(String imageFileName) {
        String imageFolderLocation = "src/main/resources/images/memory_game_pics/" + imageFileName;
        ImageIcon imageIcon = new ImageIcon(imageFolderLocation);
        JLabel imageLabel = new JLabel(imageIcon);
        gridPanel = new GridPanel(imageIcon);
        gridPanel.setPreferredSize(new Dimension(400, 215));
        GameShowUI.gameContainer.add(gridPanel, BorderLayout.CENTER);
    }

    private static void showWholeImage() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (!gridPanel.isCellVisible(x, y)) {
                    gridPanel.setFadeCell(x, y);
                }
            }
        }
    }

    private static void startCellVisibilityTimer() {
        gameRunning = true;
        Timer timer = new Timer(1000, e -> {
            if (gameRunning) {
                boolean cellVisible = true;
                while (cellVisible) {
                    Random random = new Random();
                    int randomX = random.nextInt(10);
                    int randomY = random.nextInt(10);
                    if (!gridPanel.isCellVisible(randomX, randomY)) {
                        gridPanel.setFadeCell(randomX, randomY);
                        cellVisible = false;
                    }
                }
            }
        });
        timer.start();
    }

    private static void createInputPanel(GameShow gs) {
        gs.inputPanel = new JPanel(new BorderLayout(10, 0));
        gs.inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        gs.outcomeContainer = new JPanel(new BorderLayout(10, 0));

        JPanel inputComponents = new JPanel();
        inputComponents.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        guessField = createGuessField();
        JButton submitButton = createSubmitButton(gs);

        inputComponents.add(guessField);
        inputComponents.add(submitButton);

        gs.inputPanel.add(gs.gameTimer, BorderLayout.NORTH);
        gs.inputPanel.add(inputComponents, BorderLayout.CENTER);
        gs.inputPanel.add(gs.outcomeContainer, BorderLayout.SOUTH);
    }

    private static JTextField createGuessField() {
        JTextField guessField = new JTextField(20);
        Font pixelFont = GameShowUI.pixelFont;
        guessField.setFont(pixelFont.deriveFont(18f));
        return guessField;
    }

    private static JButton createSubmitButton(GameShow gs) {
        JButton submitButton = new JButton(
                new ImageIcon(submitButtonButton.getImage().getScaledInstance(150, 40, Image.SCALE_DEFAULT)));
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.addActionListener((evt) -> sendGuess(gs, guessField.getText()));
        return submitButton;
    }

    // Add the grid panel to the center of the container
    private static void addComponentsToGameContainer(GameShow gs) {
        JPanel revealHeader = GameShowUI.generateRevealHeader();
        GameShowUI.gameContainer.add(revealHeader, BorderLayout.NORTH);
        GameShowUI.gameContainer.add(gs.inputPanel, BorderLayout.SOUTH);
    }

    private static void validateAndRepaintGameContainer() {
        GameShowUI.gameContainer.validate();
        GameShowUI.gameContainer.repaint();
    }

    public static void guess(GameShow gs, boolean correct) {
        clearOutcomeContainer(gs);

        if (!correct) {
            displayTryAgainMessage(gs);
        } else {
            handleCorrectGuess(gs);
        }

        validateAndRepaintInputPanel(gs);
    }

    private static void clearOutcomeContainer(GameShow gs) {
        gs.outcomeContainer.removeAll();
        gs.outcomeContainer.validate();
        gs.outcomeContainer.repaint();
    }

    private static void displayTryAgainMessage(GameShow gs) {
        JLabel tryAgain = new JLabel("That's not quite right :( Try again!",
                SwingConstants.CENTER);
        Font pixelFont = GameShowUI.pixelFont;
        tryAgain.setFont(pixelFont.deriveFont(15f));
        gs.outcomeContainer.add(tryAgain, BorderLayout.CENTER);
    }

    private static void handleCorrectGuess(GameShow gs) {
        gs.gameTimer.stop();
        gameRunning = false;
        showWholeImage();
        int remainingTime = gs.gameTimer.calculateScore();
        logger.log(Level.INFO, "Remaining Time: " + remainingTime);
        JLabel congrats = new JLabel("Congratulations! You Win :)",
                SwingConstants.CENTER);
        Font pixelFont = GameShowUI.pixelFont;
        congrats.setFont(pixelFont.deriveFont(15f));
        gs.outcomeContainer.add(congrats, BorderLayout.CENTER);

        JButton nextRoundButton = new JButton("Next round ->");
        nextRoundButton.addActionListener((evt) -> {
            gs.sendCommand(new JsonObject().put("command", "nextRound").put("round", gs.round + 1));
        });
        gs.outcomeContainer.add(nextRoundButton, BorderLayout.PAGE_END);
    }

    private static void validateAndRepaintInputPanel(GameShow gs) {
        gs.inputPanel.validate();
        gs.inputPanel.repaint();
    }

    private static void sendGuess(GameShow gs, String guess) {
        gs.sendCommand(new JsonObject()
                .put("command", "guess")
                .put("game", "ImageGuesser")
                .put("guess", guess)
                .put("round", gs.round)
                .put("score", gs.gameTimer.calculateScore()));
    }
}
