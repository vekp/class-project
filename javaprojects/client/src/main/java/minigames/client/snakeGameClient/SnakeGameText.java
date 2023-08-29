package minigames.client.snakeGameClient;

import java.awt.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JPanel;
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
public class SnakeGameText implements GameClient {

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */
    String player;
    JButton start, score, help;

    JLabel headerText, footerText;
    JPanel headerPanel, footerPanel, mainMenuPanel, buttonPanel;

    public SnakeGameText() {

        /** Header Section with Menu*/
        headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(800,100));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();

        headerText = new JLabel("Snake Game");
        headerText.setForeground(Color.WHITE);
        headerText.setFont(new Font("Monospaced", Font.PLAIN, 32));
        headerPanel.add(headerText, g);


         /** Footer Panel*/
        footerPanel = new JPanel();
        footerPanel.setPreferredSize(new Dimension(800,30));
        footerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());


        /**Footer Section */
        footerText = new JLabel("Developer: Sushil, Sean, Luke, Matthew ");
        footerText.setForeground(Color.WHITE);
        footerText.setFont(new Font("Monospaced", Font.PLAIN, 16));
        footerPanel.add(footerText,g);
        


        /** Menu section */
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new GridBagLayout());
        mainMenuPanel.setPreferredSize(new Dimension(800,800));
        mainMenuPanel.setBackground(Color.BLACK);

        /** Button Panel inside menu section*/
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        buttonPanel.setPreferredSize(new Dimension(800,700));
        buttonPanel.setBackground(Color.BLACK);


        /**Button Section*/

        start = new JButton("Start Game");
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener((evt) -> sendCommand("START"));

        score = new JButton("Score");
        score.setAlignmentX(Component.CENTER_ALIGNMENT);
        score.addActionListener((evt) -> sendCommand("SCORE"));

        help = new JButton("Help");
        help.setAlignmentX(Component.CENTER_ALIGNMENT);
        help.addActionListener((evt) -> sendCommand("HELP"));


        /** Adding buttons into button panel*/
        for (Component c : new Component[] { start, score, help }) {
            buttonPanel.add(c);
            buttonPanel.add(Box.createRigidArea(new Dimension(15,15)));
    
        }

        /** Using GridBagLayout to position button panel inside menu panel*/
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(-150, 0, 0, 0);
        mainMenuPanel.add(buttonPanel,gbc);

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
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().addCenter(buttonPanel);
        mnClient.getMainWindow().addSouth(footerPanel);


        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
        mnClient.getNotificationManager().setMargins(15, 10, 10);
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't nead to say "break;" at the end of our cases)


    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}
