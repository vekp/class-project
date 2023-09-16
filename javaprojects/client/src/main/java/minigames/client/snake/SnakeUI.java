package minigames.client.snake;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Objects;

public class SnakeUI implements GameClient {
    // Network client to communicate with the game server
    private MinigameNetworkClient mnClient;
    // Metadata about the game
    private GameMetadata gameMetadata;
    // Player's name
    String player;
    // UI components
    private JButton next = new JButton("Next");
    private JButton prev = new JButton("Previous");
    private JLabel currentPageLabel = new JLabel("", SwingConstants.CENTER);

    private final JPanel mainPanel = new JPanel(new CardLayout());
    private final JPanel mainMenu = new JPanel();
    private final JPanel gameView = new JPanel();
    private final JPanel helpMenu = new JPanel();
    private final JPanel aboutMe = new JPanel();
    private final JPanel achieve = new JPanel();

    private final int BUTTON_WIDTH = 200;
    private final int BUTTON_HEIGHT = 50;
    private final int START_BUTTON_Y = 300;
    private final int HOW_TO_PLAY_BUTTON_Y = 360;
    private final int ACHIEVEMENT_BUTTON_Y = 420;
    private final int BACK_BUTTON_Y = 480;
    private final JPanel containerPanel = new JPanel(new BorderLayout());
    private static final String snakeStartMenuPath = "/snake/snake_image.png";

    public SnakeUI() {
        // Constructor remains empty
    }



    private void setupUI() {
        // Set up the main panel with CardLayout
        mainPanel.add(mainMenu, "Main Menu");
        mainPanel.add(gameView, "Game View");
        mainPanel.add(helpMenu, "Help Menu");
        mainPanel.add(aboutMe, "About Me");
        mainPanel.add(achieve, "Achievements");

        // Initialize each panel with a label displaying its name
        mainMenu.add(new JLabel("Main Menu", SwingConstants.CENTER));

        ImageIcon backgroundIcon = getImage();
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        mainMenu.add(backgroundLabel);

        mainMenu.setBackground(Color.BLACK);
// TODO Reconfugure this and place relevant parts in the new main menu panel class
        //ckground image icon
//
//        // Set up the background label with the image icon.
//        // Using a JLabel to hold the image as it will also act as a container for the buttons.
//        JLabel backgroundLabel = new JLabel(backgroundIcon);
//        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
//
//        // Create and set up the "Start Game" button
//        JButton startButton = createButton("Start Game", START_BUTTON_Y);
//        startButton.addActionListener(e -> {
//            new GameFrame();  // Initialize a new game frame when clicked
//        });
//
//        // Create and set up the "How to Play" button
//        JButton howToPlayButton = createButton("How to Play", HOW_TO_PLAY_BUTTON_Y);
//        howToPlayButton.addActionListener(e -> {
//            showHowToPlayDialog(panel);  // Show game instructions when clicked
//        });
//
//        // Create and set up the "Achievements" button
//        JButton achievementButton = createButton("Achievements", ACHIEVEMENT_BUTTON_Y);
//        achievementButton.addActionListener(e -> {
//            // TODO: Implement the action for displaying achievements.
//            // Example: mnClient.getGameAchievements(player, gm.gameServer());
//        });
//
//        // Create and set up the "Back" button
//        JButton backButton = createButton("Back", BACK_BUTTON_Y);
//        backButton.addActionListener(e -> mnClient.runMainMenuSequence());
//
//        // Add the buttons to the background label
//        backgroundLabel.add(startButton);
//        backgroundLabel.add(howToPlayButton);
//        backgroundLabel.add(achievementButton);
//        backgroundLabel.add(backButton);
//
//        // Add the background label (with buttons) to the main panel
//        panel.add(backgroundLabel);
//
//        // Return the fully set up panel
//        return panel;
//    }
//



        gameView.add(new JLabel("Game View", SwingConstants.CENTER));
        helpMenu.add(new JLabel("Help Menu", SwingConstants.CENTER));


        aboutMe.add(new JLabel("About Me", SwingConstants.CENTER));
        achieve.add(new JLabel("Achievements", SwingConstants.CENTER));

        // Create a panel for the navigation buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        // Create specific buttons for each screen
        JButton mainMenuButton = new JButton("Main Menu");
        JButton navigateButton = ButtonFactory.createButton("Main Menu", 50,
                50, 200, 50, e -> {showPanel("Main Menu");});

        JButton gameViewButton = new JButton("Game View");
        JButton helpMenuButton = new JButton("Help Menu");
        JButton aboutMeButton = new JButton("About Me");
        JButton achieveButton = new JButton("Achievements");

        // Attach action listeners to navigate to specific screens
        mainMenuButton.addActionListener(e -> showPanel("Main Menu"));
        gameViewButton.addActionListener(e -> showPanel("Game View"));
        helpMenuButton.addActionListener(e -> showPanel("Help Menu"));
        aboutMeButton.addActionListener(e -> showPanel("About Me"));
        achieveButton.addActionListener(e -> showPanel("Achievements"));

        // Add specific screen buttons to the buttonsPanel
        buttonsPanel.add(mainMenuButton);
        buttonsPanel.add(gameViewButton);
        buttonsPanel.add(helpMenuButton);
        buttonsPanel.add(aboutMeButton);
        buttonsPanel.add(achieveButton);

        JButton exitGameButton = new JButton("Exit Game");
        exitGameButton.addActionListener(e -> closeGame());
        buttonsPanel.add(exitGameButton);

        // Add mainPanel and buttonsPanel to containerPanel
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        containerPanel.add(navigateButton, BorderLayout.NORTH);
        containerPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }


    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, panelName);
    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gameMetadata = game;
        this.player = player;

        // Setup UI components
        setupUI();

        // Add UI components to the main window
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(containerPanel);  // Using containerPanel now
        mnClient.getMainWindow().pack();
    }




    private void nextPanel(ActionEvent e) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.next(mainPanel);
    }

    private void previousPanel(ActionEvent e) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.previous(mainPanel);
    }

    /**
     * Sends a command to the game server.
     * Commands are JSON objects with the structure: { "command": command }
     *
     * @param command The command to be sent to the server.
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        mnClient.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), player, Collections.singletonList(json)));
    }


    /**
     * Handles the actions to be taken when the client is loaded into the main screen.
     *
     * @param mnClient The network client for communication.
     * @param game Game's metadata.
     * @param player Player's name.
     */


    /**
     * Executes a command.
     *
     * @param game Game's metadata.
     * @param command The command to be executed.
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gameMetadata = game;
        // TODO: Execution logic
    }

    /**
     * Handles the actions to be taken when closing the game.
     */
    public void closeGame() {
        mnClient.runMainMenuSequence();
    }

    public static int getStartMenuWidth() {
        return getImage().getIconWidth();
    }

    /**
     * Returns the height of the start menu.
     *
     * @return height of the start menu image.
     */
    public static int getStartMenuHeight() {
        return getImage().getIconHeight();
    }

    /**
     * Helper method to get the image icon for the background.
     *
     * @return ImageIcon for the background.
     */
    private static ImageIcon getImage() {
        return new ImageIcon(Objects.requireNonNull(SnakeUI.class.getResource(snakeStartMenuPath)));
    }
}
