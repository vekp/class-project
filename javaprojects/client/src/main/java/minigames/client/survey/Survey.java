package minigames.client.survey;

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
 */
public class Survey implements GameClient {

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

    public Survey() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(800, 600));
        textArea.setForeground(Color.GREEN);
        textArea.setBackground(Color.BLACK);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
    }

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        // this.gm = game;
        this.player = player;   

        textArea.append("Starting...");

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();     
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {

        
    }

    @Override
    public void closeGame() {
        // Nothing to do        
    }
    
}
