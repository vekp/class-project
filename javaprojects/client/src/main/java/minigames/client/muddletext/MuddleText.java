package minigames.client.muddletext;

import java.awt.*;
import java.util.Collections;

import javax.swing.*;
import javax.swing.border.LineBorder;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

/**
 * A very simple interface for a text-based game.
 * It understands three commands:
 * { "command": "clearText" } to clear the contents of the text area
 * { "command": "appendText", "text": text } to add contents to the text area
 * { "command": "setDirections", "directions": directions} to enable/disable the N, S, E, W buttons
 *   depending on whether the directions string contains "N", "S", "E", "W"
 *   (e.g. { "command": "setDirections", "directions": "NS" } would enable only N and S) 
 */
public class MuddleText implements GameClient {

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */    
    String player;

    /** A text area for showing room descriptions, etc */
    JTextArea textArea; 

    /** Direction commands */
    JButton north, south, east, west;

    JTextField userCommand;
    JButton send;
    
    JPanel commandPanel;

    public MuddleText() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(800, 600));
        textArea.setForeground(Color.GREEN);
        textArea.setBackground(Color.BLACK);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));

        JButton achievementButton = new JButton("Achv");
        achievementButton.addActionListener(e -> mnClient.getGameAchievements(player, gm.gameServer()));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mnClient.runMainMenuSequence());

        north = new JButton("NORTH");
        north.addActionListener((evt) -> sendCommand("NORTH"));
        south = new JButton("SOUTH");
        south.addActionListener((evt) -> sendCommand("SOUTH"));
        east = new JButton("EAST");
        east.addActionListener((evt) -> sendCommand("EAST"));
        west = new JButton("WEST");
        west.addActionListener((evt) -> sendCommand("WEST"));

        userCommand = new JTextField(15);
        send = new JButton(">");
        send.addActionListener((evt) -> sendCommand(userCommand.getText()));

        commandPanel = new JPanel();
        for (Component c : new Component[] { north, south, east, west, userCommand, send, achievementButton, backButton }) {
            commandPanel.add(c);
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
        mnClient.getMainWindow().addCenter(textArea);
        mnClient.getMainWindow().addSouth(commandPanel);   

        textArea.append("Starting...");

        // Notification Manager settings
        mnClient.getNotificationManager()
                .setNotificationArea(textArea)
                .setColours(Color.GREEN, Color.DARK_GRAY)
                .setFont("Monospaced")
                .setBorder(new LineBorder(new Color(0, 127, 0)));

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't nead to say "break;" at the end of our cases)
        switch (command.getString("command")) {
            case "clearText" -> textArea.setText("");
            case "appendText" -> textArea.setText(textArea.getText() + command.getString("text"));
            case "setDirections" -> {
                String directions = command.getString("directions");
                north.setEnabled(directions.contains("N"));
                south.setEnabled(directions.contains("S"));
                east.setEnabled(directions.contains("E"));
                west.setEnabled(directions.contains("W"));
            }
        }
        
    }

    @Override
    public void closeGame() {
        // Nothing to do        
    }
    
}
