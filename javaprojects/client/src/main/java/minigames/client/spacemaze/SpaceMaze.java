package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.Box;

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
 * { "command": "setDirections", "directions": directions} to enable/disable the N, S, E, W buttons
 *   depending on whether the directions string contains "N", "S", "E", "W"
 *   (e.g. { "command": "setDirections", "directions": "NS" } would enable only N and S) 
 */
public class SpaceMaze implements GameClient {

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */
    String player;

    //Header Section
    JPanel headerPanel;
    JLabel headerText;

    //Menu Section
    JPanel mainMenuPanel; 
    JPanel buttonsPanel;  
    JButton startGameButton; //Start Game Button
    JButton highScoreButton; //High Score Button
    JButton mainMenuButton;  //Main Menu Button
    JButton exitButton;      //Exit Button
    
    //Main Container
    JLabel developerCredits;


    public SpaceMaze() {

        //Menu Header Section
        headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(600, 200));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        headerText = new JLabel("Space Maze");
        headerText.setForeground(Color.WHITE);
        headerText.setFont(new Font("Monospaced", Font.PLAIN, 32));
        headerPanel.add(headerText, gbc);

        //Menu Section
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new GridBagLayout());
        mainMenuPanel.setPreferredSize(new Dimension(600, 600));
        mainMenuPanel.setBackground(Color.BLACK);

        //Buttons panel inside menu section
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.BLACK);
        buttonsPanel.setPreferredSize(new Dimension(200,200));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)); //Box layout with items arranged vertically.

        startGameButton = new JButton("Start Game");
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.addActionListener((evt) -> sendCommand("START"));

        highScoreButton = new JButton("High Score");
        highScoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreButton.addActionListener((evt) -> sendCommand("SCORE"));

        mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainMenuButton.addActionListener((evt) -> sendCommand("MENU"));

        exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener((evt) -> sendCommand("EXIT"));

        //Adding buttons to the buttons panel
        for (Component c : new Component[] { startGameButton, highScoreButton, mainMenuButton, exitButton }) {
            buttonsPanel.add(c);
            buttonsPanel.add(Box.createRigidArea(new Dimension(10,10)));

        }

        //Using GridBagLayout to position buttons panel inside menu section
        gbc.gridx = 0;
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(-200, 0, 0, 0);
        mainMenuPanel.add(buttonsPanel, gbc);

        //Credit Section
        developerCredits = new JLabel("Developed by: Andy, Nik, Natasha, Niraj");
      
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

        mnClient.getMainWindow().addCenter(mainMenuPanel);
        mnClient.getMainWindow().addSouth(developerCredits); 
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        
        this.gm = game;

      /*  
        switch(command.getString("command")){
            case "command" -> sampleText.setText("Program running");
        }
        */
    }

    @Override
    public void closeGame() {
        // Nothing to do        
    }
    
}

