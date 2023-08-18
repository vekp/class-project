package minigames.client.gameshow;

import minigames.client.gameshow.ImageGame;

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
public class GameShow implements GameClient {
    private static final Logger logger = Logger.getLogger(GameShow.class.getName());

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */
    String player;

    JButton imageButton;


    JPanel commandPanel;

    public GameShow() {

        imageButton = new JButton("Image game");
        imageButton.addActionListener((evt) -> sendCommand("imageGame")); // Replace with your desired action

        commandPanel = new JPanel();
        for (Component c : new Component[] { imageButton }) {
            commandPanel.add(c);
        }
        logger.log(Level.INFO, "GameShow instance created");


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
        logger.log(Level.INFO, "sendCommand called with command: {0}", command);
        logger.log(Level.INFO, "Sending JSON: {0}", json.toString());
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), this.player, Collections.singletonList(json)));
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
        mnClient.getMainWindow().addCenter(commandPanel);
        // mnClient.getMainWindow().addSouth(commandPanel);

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
        logger.log(Level.INFO, "load executed for player: {0}, game: {1}", new Object[]{player, game.name()});

    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.log(Level.INFO, "execute called with command: {0}", command.getString("command"));

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't nead to say "break;" at the end of our cases)
        switch (command.getString("command")) {
            case "imageGame" -> {
                ImageGame imageGame = new ImageGame();
                JPanel imagePanel = imageGame.renderWindow();
                mnClient.getMainWindow().clearAll();

                mnClient.getMainWindow().addCenter(imagePanel);
                mnClient.getMainWindow().pack();
            }
        }

    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}
