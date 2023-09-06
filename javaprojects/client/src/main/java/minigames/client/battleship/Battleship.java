package minigames.client.battleship;

import io.vertx.core.json.JsonObject;
import minigames.client.Animator;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Battleship implements GameClient, Tickable {

    // Needed for sending commands to the server
    MinigameNetworkClient mnClient;
    int tickInterval = 180;
    int tickTimer = 0;

    GameMetadata gm;

    // Player name
    String player;

    // Background colour
    String bgColour = "#07222b";
    // Background colour hover
    String bgColourHover = "#113440";
    // Foreground colour
    String fgColour = "#ffffff";
    // Border colour
    String borderColour = "#6e8690";
    ArrayList<Font> fonts = determineFont();
    Border buttonBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode(borderColour)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
    );

    JPanel mainPanel;
    JPanel heading;
    JButton menuButton;
    JButton achievementButton;
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
    boolean isQuitting;

    //flag to indicate whether this client is in wait mode (which means it will be constantly asking
    // for info refreshes from the server
    boolean waiting;


    /**
     * Creates the panels and layout for the game
     */
    public Battleship() {

        // Heading
        heading = new JPanel(new GridBagLayout());  // Game title, Current player and Menu button
        GridBagConstraints gbc = new GridBagConstraints();

        // Menu button
        menuButton = new JButton("Menu");
        menuButton.addActionListener(e -> {
            closeGame();
            mnClient.runMainMenuSequence();
        });
        menuButton.setFont(fonts.get(1));
        // Achievement button
        achievementButton = new JButton("Achv");
        achievementButton.addActionListener(e -> mnClient.getGameAchievements(player, gm.gameServer()));
        achievementButton.setFont(fonts.get(1));

        for (JButton b : new JButton[]{menuButton, achievementButton}) {
            b.setOpaque(true);
            b.setBorder(buttonBorder);
            b.setFocusable(false);
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    b.setBackground(Color.decode(bgColourHover));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    b.setBackground(Color.decode(bgColour));
                }
            });
        }

        title = new JLabel("< BattleShip >");
        title.setFont(fonts.get(0));

        currentPlayerName = new JLabel("Current Player: ");
        currentPlayerName.setFont(fonts.get(3));
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        heading.add(menuButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        heading.add(achievementButton, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.9;
        gbc.gridx = 1;
        gbc.gridy = 0;
        heading.add(title, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        heading.add(currentPlayerName, gbc);


        // Maps
        maps = new JPanel();
        nauticalMap = new JPanel();  // Add player ship grid
        nauticalText = new JTextArea();
        nauticalText.setFont(fonts.get(1));
        nauticalText.setEditable(false);
        nauticalMap.add(nauticalText);

        maps.add(nauticalMap);
        maps.add(Box.createRigidArea(new Dimension(100, 330)));

        targetMap = new JPanel();  // Add enemy ship grid
        targetText = new JTextArea();
        targetText.setFont(fonts.get(1));
        targetText.setEditable(false);
        targetMap.add(targetText);
        maps.add(targetMap);

        // Terminal - Messages and input area
        terminal = new JPanel();
        terminal.setLayout(new BorderLayout());
        messages = new JTextArea();  // Message history
        messages.setEditable(false);
        messages.setFont(fonts.get(3));
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);

        commandTerminal = new JScrollPane(messages);
        commandTerminal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                " Command Terminal: ", TitledBorder.LEFT, TitledBorder.TOP, fonts.get(2), Color.WHITE));
        commandTerminal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        commandTerminal.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        commandTerminal.setPreferredSize(new Dimension(800, 130));
        commandTerminal.setWheelScrollingEnabled(true);
        DefaultCaret caret = (DefaultCaret) messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        terminal.add(commandTerminal, BorderLayout.NORTH);

        userCommand = new JTextField();  // User input
        userCommand.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " "));
        userCommand.getCaret().setBlinkRate(300);
        userCommand.getCaret().setVisible(true);
        userCommand.addActionListener((evt) -> {
            sendCommand(userCommand.getText());  // Send input to server
            userCommand.setText("");             // Clear input field
        });
        userCommand.setFont(fonts.get(3));
        terminal.add(userCommand, BorderLayout.CENTER);

        // Add everything to one panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(heading);
        mainPanel.add(maps);
        mainPanel.add(terminal);

        // Set colours for all panels
        for (Component c : new Component[]{mainPanel, heading, title, currentPlayerName, nauticalMap, nauticalText,
                targetMap, targetText, maps, messages, commandTerminal, userCommand, menuButton, achievementButton}) {
            c.setForeground(Color.decode(fgColour));
            c.setBackground(Color.decode(bgColour));
        }
    }

    /**
     * Function to create an appropriate font list based on operating system
     *
     * @return Arraylist of fint objects
     */
    public ArrayList<Font> determineFont() {
        // System.out.println(System.getProperty("os.name"));
        if (System.getProperty("os.name").contains("Windows")) {
            // System.out.println("Windows fonts");
            return new ArrayList<>(
                    Arrays.asList(
                            new Font("Lucida Sans Typewriter", Font.BOLD, 30),
                            new Font("Lucida Sans Typewriter", Font.PLAIN, 20),
                            new Font("Lucida Sans Typewriter", Font.BOLD, 18),
                            new Font("Lucida Sans Typewriter", Font.PLAIN, 16)
                    ));
        } else if (System.getProperty("os.name").contains("Mac")) {
            // System.out.println("Mac fonts");
            return new ArrayList<>(
                    Arrays.asList(
                            new Font("Andale Mono", Font.BOLD, 30),
                            new Font("Andale Mono", Font.PLAIN, 20),
                            new Font("Andale Mono", Font.BOLD, 18),
                            new Font("Andale Mono", Font.PLAIN, 16)
                    ));
        } else {
            // System.out.println("Default fonts");
            return new ArrayList<>(
                    Arrays.asList(
                            new Font("Monospaced", Font.BOLD, 30),
                            new Font("Monospaced", Font.PLAIN, 20),
                            new Font("Monospaced", Font.BOLD, 18),
                            new Font("Monospaced", Font.PLAIN, 16)
                    ));
        }

    }

    /**
     * Sends a command to the game at the server.
     * This being a text adventure, all our commands are just plain text strings our gameserver will interpret.
     * We're sending these as
     * { "command": command }
     */
    public void sendCommand(String command) {
        if (isQuitting) return;
        //reset the tick timer here so that there is a delay before the client sends new commands (mostly the
        //refresh command - allows players to read and process the newly rendered window before anything else happens)
        tickTimer = 0;
        JsonObject json = new JsonObject().put("command", command);

        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    /*
     * What happens when client is loaded into the main screen
     */

    /**
     * Loads the client into the main screen and renders it taking in the relevant information from the server
     *
     * @param mnClient The Client Window
     * @param game     The meta-game data for the current game
     * @param player   The name of the player
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        mnClient.getAnimator().requestTick(this);
        isQuitting = false;
        waiting = true; //this ensures we get at least 1 refresh to start
        this.gm = game;
        this.player = player;

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addCenter(mainPanel);

        messages.append("Starting...");

        // Apply settings to notifications
        mnClient.getNotificationManager()
                .setColours(Color.decode(fgColour), Color.decode(bgColour), Color.decode(bgColourHover))
                .setFont(fonts.get(0).getFontName())
                .setBorder(buttonBorder)
                .setNotificationArea(mainPanel);

        mnClient.getDialogManager()
                .setColours(Color.decode(fgColour), Color.decode(bgColour), Color.decode(bgColourHover))
                .setFont(fonts.get(0).getFontName())
                .setBorder(buttonBorder);

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
    }

    /**
     * Execute a specific command from the game's "vocabulary"
     *
     * @param game    The meta-data of the current game
     * @param command The JsonObject command to be executed
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't nead to say "break;" at the end of our cases)
        switch (command.getString("command")) {
            case "inputAllowable" -> userCommand.setEditable(Boolean.parseBoolean(command.getString("allowed")));
            case "clearText" -> {
                nauticalText.setText("");
                targetText.setText("");
                messages.setText("");
            }
            case "updateHistory" -> {
                messages.setText(command.getString("history"));
                messages.setCaretPosition(messages.getDocument().getLength());
            }
            case "waitReady" -> {
                messages.append("\nWaiting for other players to Ready");
                userCommand.setEditable(false);
            }
            case "wait" -> {
                messages.append("\nWaiting for Turn");
                userCommand.setEditable(false);
                waiting = true;
            }
            case "prepareTurn" -> {
                //we only set this messaging on the first instance that we are told our turn is ready
                waiting = false;
                userCommand.setEditable(true);

            }
            case "updatePlayerName" -> currentPlayerName.setText("Current Player: " + command.getString("player"));
            case "placePlayer1Board" -> nauticalText.setText(nauticalText.getText() + command.getString("text"));
            case "placePlayer2Board" -> targetText.setText(targetText.getText() + command.getString("text"));
        }
    }

    /**
     * Ends the game once certain conditions are met, or manually ended by the player
     */
    @Override
    public void closeGame() {
        // Nothing to do
        sendCommand("exitGame");
        isQuitting = true;
        // JOptionPane.showMessageDialog(mainPanel, "You will be returned to the main menu", "Exit", JOptionPane.INFORMATION_MESSAGE, null);
    }


    /**
     * This client will constantly tick at some interval. If we're in the waiting state (e.g waiting for another
     * player to take their turn), we will ask the server for updates in order to get any messages or info about the
     * players turn, and also to be notified when it is now our turn.
     * @param al the animator
     * @param now current time
     * @param delta delta time (time since last tick)
     */
    @Override
    public void tick(Animator al, long now, long delta) {
        al.requestTick(this);
        tickTimer++;
        if (tickTimer > tickInterval) {
            tickTimer = 0;
            if(waiting) {
                sendCommand("refresh");
            }
        }
    }
}
