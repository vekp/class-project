package minigames.client.memory;

import io.vertx.core.json.JsonObject;

import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

import java.awt.*;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.*;

/**
 * COSC220 Assessment 3
 * @author Melinda Luo, Lyam Talbot, Scott Lehmann, William Koller
 * Memory Card Game
 */
public class Memory implements GameClient, ActionListener, MouseListener {

    // Client instance
    MinigameNetworkClient mnClient;

    // Send commands to server
    GameMetadata gm;

    // Player
    String player;

    /** Swing components */
    JPanel mainPanel; // Main panel for the game
    JPanel gameMenuPanel; // Panel for the game menu which houses the options for the game
    JPanel commandPanel; // Panel for the game to react to user input such as "it's a pair"
    JPanel headingPanel; // Panel that houses the game and player objects
    JLabel title;
    JPanel playerPanel; // Panel for the player information
    JLabel playerName;
    JPanel gameOptionsPanel; // Houses the button objects for the game options
    JButton newGameButton;
    JButton restartLevelButton;
    JButton exitButton;
    JPanel gridContainerPanel; // Wraps the cardGridPanel
    JPanel cardGridPanel; // Sets the grid for the playing cards

    JTextArea textArea;

    // Game variable
    boolean gameStarted = false;

    /** Initialize Swing components */
    public Memory() {
        headingPanel = new JPanel();
        headingPanel.setLayout(new GridLayout(1, 1));
        title = new JLabel("Memory");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setHorizontalAlignment(JLabel.CENTER);
        headingPanel.add(title);

        // Create the game menu panel
        gameMenuPanel = new JPanel();
        gameMenuPanel.setLayout(new GridLayout(3, 0));
        gameMenuPanel.setBorder((new EmptyBorder(0,0,15,0)));
        gameMenuPanel.add(headingPanel); // Add heading panel to the game menu panel

        // Create the player panel
        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 1));
        playerPanel.setPreferredSize(new Dimension(800,30));
        playerPanel.setBackground(Color .decode("#B2C6B2"));
        playerName = new JLabel("Player: " + player); // Placeholder text for player name
        playerName.setFont(new Font("Arial", Font.BOLD, 16));
        playerPanel.add(playerName);

        // Create the game options panel which hosts new game, restart level, and exit buttons
        gameOptionsPanel = new JPanel();
        gameOptionsPanel.setLayout(new GridLayout(1, 3));
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartLevelButton = new JButton("Restart Level");
        restartLevelButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        gameOptionsPanel.add(newGameButton);
        gameOptionsPanel.add(restartLevelButton);
        gameOptionsPanel.add(exitButton);

        gameMenuPanel.add(playerPanel);
        gameMenuPanel.add(gameOptionsPanel);

        // Create placeholder for card grid
        cardGridPanel = new JPanel();
        cardGridPanel.setLayout(new GridLayout(4, 4, 0, 0)); // 4x4 grid for placeholders
        cardGridPanel.addMouseListener(this); // Add mouse listener to the card grid

        // Create the card placeholders
        for (int i = 0; i < 16; i++) {
            JLabel cardLabel = new JLabel("Card " + (i + 1)); // Placeholder text for cards
            cardLabel.setHorizontalAlignment(JLabel.CENTER);
            cardLabel.setVerticalAlignment(JLabel.CENTER);
            cardLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            cardGridPanel.add(cardLabel);
        }

        // Container for the card grid
        gridContainerPanel = new JPanel(new BorderLayout());
        gridContainerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2,true)); // Set border around player grid to separate the UI elements
        gridContainerPanel.setPreferredSize(new Dimension(750, 450));
        gridContainerPanel.add(cardGridPanel, BorderLayout.CENTER); // Add the cardGridPanel to the container

        // Create the command panel
        commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.setBorder((new EmptyBorder(15,10,0,10)));
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.BOLD, 16));
        commandPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Set up the main layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gameMenuPanel, BorderLayout.NORTH);
        mainPanel.add(gridContainerPanel, BorderLayout.CENTER);
        mainPanel.add(commandPanel, BorderLayout.SOUTH);


        for (Component c:  new Component[] {title, headingPanel, playerName, newGameButton, restartLevelButton, exitButton,
                cardGridPanel, commandPanel, textArea, gameMenuPanel, playerPanel, gridContainerPanel, mainPanel})
            c.setBackground(Color.decode("#B2C6B2"));

        // Add ActionListeners to the buttons
        newGameButton.addActionListener(this);
        restartLevelButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Add ActionListeners to the card grid
        for (Component c : cardGridPanel.getComponents()) {
            c.addMouseListener(this);
        }
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

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
            //TODO
            System.out.println("Testing...");
        }
        if (e.getSource() == restartLevelButton) {
            //TODO
            System.out.println("Testing...");
        }
        if (e.getSource() == exitButton) {
            //TODO
            System.out.println("Testing...");
        }

    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }
}