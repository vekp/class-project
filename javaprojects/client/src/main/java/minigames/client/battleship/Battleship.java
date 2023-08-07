package minigames.client.battleship;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        heading.setLayout(new BorderLayout());
        title = new JLabel("< BattleShip >");
        title.setFont(fonts[0]);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(new EmptyBorder(10, 0, 10,0));

        currentPlayerName = new JLabel("Current Player: ");
        currentPlayerName.setFont(fonts[3]);
        currentPlayerName.setHorizontalAlignment(JLabel.CENTER);

        heading.add(title, BorderLayout.NORTH);
        heading.add(Box.createRigidArea(new Dimension(0,10)));
        heading.add(currentPlayerName, BorderLayout.CENTER);

        maps = new JPanel();
        fleetMap = new JPanel();   // Add player ship grid
        targetMap = new JPanel();  // Add enemy ship grid
        maps.add(fleetMap);
        maps.add(targetMap);
        maps.setPreferredSize(new Dimension(800, 350));

        terminal = new JPanel();
        terminal.setLayout(new BorderLayout());
        messages = new JTextArea();  // Add message/message history here
        messages.setEditable(false);
        messages.setFont(fonts[3]);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);

        commandTerminal = new JScrollPane(messages);
        commandTerminal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                " Command Terminal: ", TitledBorder.LEFT, TitledBorder.TOP, fonts[2], Color.WHITE));
        // commandTerminal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        commandTerminal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        commandTerminal.setPreferredSize(new Dimension(800, 130));
        terminal.add(commandTerminal, BorderLayout.NORTH);

        userCommand = new JTextField();
        userCommand.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " "));
        userCommand.addActionListener((evt) -> sendCommand(userCommand.getText()));
        terminal.add(userCommand, BorderLayout.CENTER);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(maps, BorderLayout.CENTER);
        mainPanel.add(terminal, BorderLayout.SOUTH);

        for (Component c : new Component[] {mainPanel, heading, title, currentPlayerName, fleetMap, targetMap, maps,
                messages, commandTerminal, userCommand}) {
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
    /**
     * Loads the client into the main screen and renders it taking in the relevant information from the server
     * @param mnClient The Client Window
     * @param game The meta-game data for the current game
     * @param player The name of the player
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addCenter(mainPanel);

        messages.append("Starting...");

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
    }

    /**
     * Execute a specific command from the game's "vocabulary"
     * @param game The meta-data of the current game
     * @param command The JsonObject command to be executed
     */
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

    /**
     * Ends the game once certain conditions are met, or manually ended by the player
     */
    @Override
    public void closeGame() {
        // Nothing to do
    }
}
