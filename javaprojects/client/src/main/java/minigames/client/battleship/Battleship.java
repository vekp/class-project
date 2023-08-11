package minigames.client.battleship;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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

    // Background colour
    String bgColour = "#07222b";
    // Foreground colour
    String fgColour = "#ffffff";

    JPanel mainPanel;
    JPanel heading;
    JLabel title;
    JLabel currentPlayerName;
    JPanel maps;
    JPanel nauticalMap;
    JTextArea nauticalText;
    JPanel targetMap;
    JTextArea targetText;
    JPanel terminal;
    JTextArea messages;
    JScrollPane commandTerminal;
    JTextField userCommand;

    /**
     * Creates the panels and layout for the game
     */
    public Battleship() {
        //TODO: Add current player label functionality

        // Heading
        heading = new JPanel();  // Game title and current player
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

        // Maps
        maps = new JPanel();
        nauticalMap = new JPanel();  // Add player ship grid
        nauticalText = new JTextArea();
        nauticalText.setFont(fonts[1]);
        nauticalText.setEditable(false);
        nauticalMap.add(nauticalText);

        maps.add(nauticalMap);
        maps.add(Box.createRigidArea(new Dimension(100,330)));


        targetMap = new JPanel();  // Add enemy ship grid
        targetText = new JTextArea();
        targetText.setFont(fonts[1]);
        targetText.setEditable(false);
        targetMap.add(targetText);
        maps.add(targetMap);

        // Terminal - Messages and input area
        terminal = new JPanel();
        terminal.setLayout(new BorderLayout());
        messages = new JTextArea();  // Message history
        messages.setEditable(false);
        messages.setFont(fonts[3]);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);

        commandTerminal = new JScrollPane(messages);
        commandTerminal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                " Command Terminal: ", TitledBorder.LEFT, TitledBorder.TOP, fonts[2], Color.WHITE));
        commandTerminal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        commandTerminal.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        commandTerminal.setPreferredSize(new Dimension(800, 130));
        commandTerminal.setWheelScrollingEnabled(true);
        DefaultCaret caret = (DefaultCaret)messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        terminal.add(commandTerminal, BorderLayout.NORTH);

        userCommand = new JTextField();  // User input
        userCommand.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " "));
        userCommand.addActionListener((evt) -> sendCommand(userCommand.getText()));
        userCommand.setFont(fonts[3]);
        terminal.add(userCommand, BorderLayout.CENTER);

        // Add everything to one panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(heading);
        mainPanel.add(maps);
        mainPanel.add(terminal);

        // Set colours for all panels
        for (Component c : new Component[] {mainPanel, heading, title, currentPlayerName, nauticalMap, nauticalText,
                targetMap, targetText, maps, messages, commandTerminal, userCommand}) {
            c.setForeground(Color.decode(fgColour));
            c.setBackground(Color.decode(bgColour));
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
            case "clearText" -> {
                nauticalText.setText("");
                targetText.setText("");
                messages.setText("");
            }
            case "updateHistory" -> {
                messages.setText(command.getString("history"));
                messages.setCaretPosition(messages.getDocument().getLength());
            }
//            case "appendText" -> messages.append(command.getString("input"));
            case "placePlayer1Board" -> nauticalText.setText(nauticalText.getText() + command.getString("text"));
            case "placePlayer2Board" -> targetText.setText(targetText.getText() + command.getString("text"));
            case "clearInput" -> userCommand.setText("");
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
