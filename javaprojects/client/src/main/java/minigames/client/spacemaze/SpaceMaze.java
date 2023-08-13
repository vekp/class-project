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

    /** A text area for showing room descriptions, etc */
    //JTextArea textArea; 

    /** Direction commands */
    //JButton north, south, east, west;
    //JTextField userCommand;

    //JButton send;
    
    JPanel commandPanel;

    public SpaceMaze() {
        /*
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(800, 600));
        textArea.setForeground(Color.GREEN);
        textArea.setBackground(Color.BLACK);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));

        north = new JButton("NORTH");
        north.addActionListener((evt) -> sendCommand("NORTH"));
        south = new JButton("SOUTH");
        south.addActionListener((evt) -> sendCommand("SOUTH"));
        east = new JButton("EAST");
        east.addActionListener((evt) -> sendCommand("EAST"));
        west = new JButton("WEST");
        west.addActionListener((evt) -> sendCommand("WEST"));

        userCommand = new JTextField(20);
        send = new JButton(">");
        send.addActionListener((evt) -> sendCommand(userCommand.getText()));

        commandPanel = new JPanel();
        for (Component c : new Component[] { north, south, east, west, userCommand, send }) {
            commandPanel.add(c);
        }
        
       // Listen for all key presses whenever focus is in the UI
       KeyEventDispatcher thisKeyEventDispatcher = new DefaultFocusManager();
       KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(myKeyEventDispatcher);
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
         
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        
        
    }

    @Override
    public void closeGame() {
        // Nothing to do        
    }
    
}
