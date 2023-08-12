package minigames.client.memory;

import io.vertx.core.json.JsonObject;

import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

import java.awt.*;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;

import org.apache.logging.log4j.*;

/**
 * COSC220 Assessment 3
 * @author Melinda Luo, Lyam Talbot, Scott Lehmann, William Koller
 * Memory Card Game
 */
public class Memory implements GameClient {

    // Client instance
    MinigameNetworkClient mnClient;

    // Send commands to server
    GameMetadata gm;

    // Player
    String player;

    // Swing components
    JPanel mainPanel;
    JPanel gamePanel;
    JPanel commandPanel;
    JPanel headingPanel;
    JLabel title;
    JPanel playerPanel;
    JLabel playerName;
    JPanel gameOptionsPanel;
    JButton newGameButton;
    JButton restartLevelButton;
    JButton exitButton;
    JPanel cardGridPanel;

    JTextArea textArea;

    // Game variable
    boolean gameStarted = false;

    public Memory() {
        headingPanel = new JPanel();
        headingPanel.setLayout(new GridLayout(1, 1));
        title = new JLabel("Memory");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setHorizontalAlignment(JLabel.CENTER);
        headingPanel.add(title);

        // Create the game panel
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 1));
        gamePanel.add(headingPanel);

        // Create the player panel
        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 1));
        playerName = new JLabel("Player: ");
        playerName.setFont(new Font("Arial", Font.BOLD, 20));
        playerPanel.add(playerName);

        // Create the game options panel which hosts new game, restart level, and exit buttons
        gameOptionsPanel = new JPanel();
        gameOptionsPanel.setLayout(new GridLayout(1, 3));
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartLevelButton = new JButton("Restart Level");
        restartLevelButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        gameOptionsPanel.add(newGameButton);
        gameOptionsPanel.add(restartLevelButton);
        gameOptionsPanel.add(exitButton);

        gamePanel.add(playerPanel);
        gamePanel.add(gameOptionsPanel);

        // Create placeholder for card grid
        cardGridPanel = new JPanel();
        cardGridPanel.setLayout(new GridLayout(4, 4)); // 4x4 grid for placeholders
        for (int i = 0; i < 16; i++) {
            JLabel cardLabel = new JLabel("Card " + (i + 1)); // Placeholder text for cards
            cardLabel.setHorizontalAlignment(JLabel.CENTER);
            cardLabel.setVerticalAlignment(JLabel.CENTER);
            cardLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            cardGridPanel.add(cardLabel);
        }

        // Create the command panel
        commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.BOLD, 20));
        commandPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Set up the main layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.NORTH);
        mainPanel.add(cardGridPanel, BorderLayout.CENTER);
        mainPanel.add(commandPanel, BorderLayout.SOUTH);

        /* 
            // Create and configure the main frame
            JFrame frame = new JFrame("Memory Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(mainPanel);
            frame.setPreferredSize(new Dimension(1920, 1080));
            frame.pack();
            frame.setVisible(true);
        */
        
    }

    /** 
     * Sends a command to the game at the server.
     * This being a text adventure, all our commands are just plain text strings our gameserver will interpret.
     * We're sending these as 
     * { "command": command }
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);

        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addCenter(mainPanel);  

        textArea.append("Starting...");

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();     
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't need to say "break;" at the end of our cases)
        switch (command.getString("command")) {
            case "clearText" -> textArea.setText("");
            case "appendText" -> textArea.setText(textArea.getText() + command.getString("text"));
        }
    }

    @Override
    public void closeGame() {
        // Nothing to do        
    }

}