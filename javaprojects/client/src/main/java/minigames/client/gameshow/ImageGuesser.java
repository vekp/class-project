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

    public static void startImageGuesser(GameShow gs, String imageFileName, int gameId) {
        gs.gameContainer.removeAll();
        gs.gameContainer.validate();
        gs.gameContainer.repaint();

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
        gs.inputPanel = new JPanel(new BorderLayout(10, 0));
        gs.inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        gs.outcomeContainer = new JPanel(new BorderLayout(10, 0));

        JPanel inputComponents = new JPanel(); // Create a container for fixed-size components
        inputComponents.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // You can adjust the layout manager as
                                                                              // needed

        JTextField guessField = new JTextField(20);
        JButton submitButton = new JButton("Submit Guess");
        submitButton.addActionListener((evt) -> gs.sendCommand(new JsonObject()
                .put("command", "guessImage")
                .put("guess", guessField.getText())
                .put("gameId", gs.gameId)));

        inputComponents.add(guessField);
        inputComponents.add(submitButton);

        gs.inputPanel.add(inputComponents, BorderLayout.CENTER); // Add the container with fixed-size components
        gs.inputPanel.add(gs.outcomeContainer, BorderLayout.SOUTH);
        // Add the grid panel to the center of the container
        gs.gameContainer.add(gridPanel, BorderLayout.CENTER);
        gs.gameContainer.add(gs.inputPanel, BorderLayout.SOUTH);

        gs.gameContainer.validate();
        gs.gameContainer.repaint();
    }

    public static void guess(GameShow gs, boolean correct) {
        gs.outcomeContainer.removeAll();
        gs.outcomeContainer.validate();
        gs.outcomeContainer.repaint();
        if (!correct) {
            JLabel tryAgain = new JLabel("That's not quite right :( Try again!",
                    SwingConstants.CENTER);
            Font pixelFont = GameShowUI.pixelFont;
            tryAgain.setFont(pixelFont.deriveFont(15f));// size may need changing

            gs.outcomeContainer.add(tryAgain, BorderLayout.CENTER);
        } else {
            JLabel congrats = new JLabel("Congratulations! You Win :)",
                    SwingConstants.CENTER);
            gs.outcomeContainer.add(congrats, BorderLayout.CENTER);
        }
        // logger.log(Level.INFO, "GameShow instance created");

        gs.inputPanel.validate();
        gs.inputPanel.repaint();
    }

    private static void sendGuess(GameShow gs, String guess, int gameId) {
        gs.sendCommand(new JsonObject()
                .put("command", "guess")
                .put("game", "imageGuesser")
                .put("guess", guess)
                .put("gameId", gameId));
    }

}
