package minigames.client.battleship;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Collections;

public class Battleship implements GameClient {

    MinigameNetworkClient mnClient;
    // Needed for sending commands to the server
    GameMetadata gm;
    // Player name
    String player;

    Font[] fonts = {
            new Font("Lucida Sans Typewriter", Font.BOLD, 30),
            new Font("Lucida Sans Typewriter", Font.PLAIN, 20),
            new Font("Lucida Sans Typewriter", Font.BOLD, 18),
            new Font("Lucida Sans Typewriter", Font.PLAIN, 16)
    };
    String colour = "#07222b";

    JPanel mainPanel;
    JPanel heading;
    JLabel title;
    JLabel currentPlayerName;
    JPanel maps;
    JPanel fleetMap;
    JPanel targetMap;
    JPanel terminal;
    JTextArea messages;
    JScrollPane commandTerminal;
    JTextField userCommand;

    public Battleship() {
        heading = new JPanel();
        title = new JLabel("< BattleShip >");
        currentPlayerName = new JLabel("Current Player: ");
        heading.add(title);
        heading.add(currentPlayerName);

        maps = new JPanel();
        fleetMap = new JPanel();   // Add player ship grid
        targetMap = new JPanel();  // Add enemy ship grid
        maps.add(fleetMap);
        maps.add(targetMap);

        terminal = new JPanel();
        messages = new JTextArea();  // Add message/message history here
        messages.setEditable(false);
        commandTerminal = new JScrollPane(messages);
        commandTerminal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                " Command Terminal: ", TitledBorder.LEFT, TitledBorder.TOP, fonts[2], Color.WHITE));
        commandTerminal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        terminal.add(commandTerminal);

        userCommand = new JTextField();
        userCommand.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " "));
        userCommand.addActionListener((evt) -> sendCommand(userCommand.getText()));

        mainPanel = new JPanel();
        mainPanel.add(maps);
        mainPanel.add(terminal);

        for (Component c : new Component[] {heading, maps, terminal, userCommand}) {
            c.setForeground(Color.WHITE);
            c.setBackground(Color.decode(colour));
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
     * What happens when client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addNorth(heading);
        mnClient.getMainWindow().addCenter(mainPanel);
        mnClient.getMainWindow().addSouth(userCommand);

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
            case "clearText" -> messages.setText("");
            case "appendText" -> messages.setText(messages.getText() + command.getString("text"));
        }

    }

    @Override
    public void closeGame() {
        // Nothing to do
    }
}
