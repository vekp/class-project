package minigames.client.gameshow;



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



public class ImageGuesser {
    private JPanel gameContainer;
    private JPanel inputPanel;
    private String imageFileName;
    private int gameId;
    private MinigameNetworkClient mnClient;
    private GameMetadata game;
    private String player;

    private JPanel outcomeContainer;
    private static final Logger logger = Logger.getLogger(ImageGuesser.class.getName());

    public ImageGuesser(MinigameNetworkClient mnClient, GameMetadata game,  JPanel gameContainer, String player, String imageFileName, int gameId) {
        this.gameContainer = gameContainer;
        this.imageFileName = imageFileName;
        this.gameId = gameId;
        this.mnClient = mnClient;
        this.game = game;
        this.player = player;
    }


    public void startImageGuesser() {
        this.gameContainer.removeAll();
        this.gameContainer.validate();
        this.gameContainer.repaint();


        String imageFolderLocation = "src/main/resources/images/memory_game_pics/" + this.imageFileName;
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
        this.inputPanel = new JPanel(new BorderLayout(10, 0));
        this.inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.outcomeContainer = new JPanel(new BorderLayout(10, 0));


        JPanel inputComponents = new JPanel(); // Create a container for fixed-size components
        inputComponents.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // You can adjust the layout manager as needed

        JTextField guessField = new JTextField(20);
        JButton submitButton = new JButton("Submit Guess");
        submitButton.addActionListener((evt) -> sendCommand(new JsonObject()
                .put("command", "guessImage")
                .put("guess", guessField.getText())
                .put("gameId", this.gameId)));

        inputComponents.add(guessField);
        inputComponents.add(submitButton);

        this.inputPanel.add(inputComponents, BorderLayout.CENTER); // Add the container with fixed-size components
        this.inputPanel.add(this.outcomeContainer, BorderLayout.SOUTH);
        // Add the grid panel to the center of the container
        this.gameContainer.add(gridPanel, BorderLayout.CENTER);
        this.gameContainer.add(this.inputPanel, BorderLayout.SOUTH);

        this.gameContainer.validate();
        this.gameContainer.repaint();
    }

    public void guess( boolean correct) {
        this.outcomeContainer.removeAll();
        this.outcomeContainer.validate();
        this.outcomeContainer.repaint();
        if (!correct) {
            JLabel tryAgain = new JLabel("That's not quite right :( Try again!",
                    SwingConstants.CENTER);

            this.outcomeContainer.add(tryAgain, BorderLayout.CENTER);
        } else {
            JLabel congrats = new JLabel("Congratulations! You Win :)",
                    SwingConstants.CENTER);
            this.outcomeContainer.add(congrats, BorderLayout.CENTER);
        }
        logger.log(Level.INFO, "GameShow instance created");


        this.inputPanel.validate();
        this.inputPanel.repaint();
    }

    public void sendCommand(JsonObject json) {
        // Collections.singletonList() is a quick way of getting a "list of one item"
        // logger.log(Level.INFO, "sendCommand called with command: {0}", command);
        logger.log(Level.INFO, "Sending JSON: {0}", json.toString());
        this.mnClient.send(new CommandPackage(this.game.gameServer(), this.game.name(), this.player, Collections.singletonList(json)));
    }

}



