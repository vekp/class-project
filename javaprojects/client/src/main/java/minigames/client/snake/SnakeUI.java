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
    private JLabel mainMenuLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel gameViewLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel helpMenuLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel aboutMeLabel = new JLabel("", SwingConstants.CENTER);
    private JLabel achieveLabel = new JLabel("", SwingConstants.CENTER);

    public SnakeUI() {
        // Constructor remains empty
    }



    private void setupUI() {
        // Setup the main panels
        setupMainPanels();

        // Set background for main menu
        setupBackground();

        // Set labels for each view
        setupViewLabels();

        // Set buttons for navigation and actions
        setupButtons();

        // Add mainPanel to containerPanel
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center-align the button
        JButton mainMenuButton = ButtonFactory.createButton("Main Menu", 0, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Main Menu"));

        bottomPanel.add(mainMenuButton);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);
        // Setup main window properties
        setupMainWindow();
    }

    private void setupMainPanels() {
        mainPanel.add(mainMenu, "Main Menu");
        mainPanel.add(gameView, "Play");
        mainPanel.add(helpMenu, "Help Menu");
        mainPanel.add(aboutMe, "About Me");
        mainPanel.add(achieve, "Achievements");
    }

    private void setupBackground() {
        ImageIcon backgroundIcon = getImage();
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        backgroundLabel.setLayout(null); // For adding components directly

        mainMenu.add(backgroundLabel);
        mainMenu.setBackground(Color.BLACK);
    }

    private void setupViewLabels() {
        mainMenu.add(new JLabel("Main Menu", SwingConstants.CENTER));
        gameView.add(new JLabel("Game View", SwingConstants.CENTER));
        helpMenu.add(new JLabel("Help Menu", SwingConstants.CENTER));
        aboutMe.add(new JLabel("About Me", SwingConstants.CENTER));
        achieve.add(new JLabel("Achievements", SwingConstants.CENTER));

        // For testing
        mainMenu.add(mainMenuLabel);
        gameView.add(gameViewLabel);
        helpMenu.add(helpMenuLabel);
        aboutMe.add(aboutMeLabel);
        achieve.add(achieveLabel);
    }

    private void setupButtons() {
        int buttonX = (getStartMenuWidth() - BUTTON_WIDTH) / 2;

        JButton mainMenuButton = ButtonFactory.createButton("Main Menu", buttonX, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Main Menu"));
        JButton gameViewButton = ButtonFactory.createButton("Play", buttonX, HOW_TO_PLAY_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Play"));
        JButton helpMenuButton = ButtonFactory.createButton("Help Menu", buttonX, ACHIEVEMENT_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Help Menu"));
        JButton aboutMeButton = ButtonFactory.createButton("About Me", buttonX, BACK_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("About Me"));
        JButton achieveButton = ButtonFactory.createButton("Achievements", buttonX, BACK_BUTTON_Y + 60, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Achievements"));
        JButton exitGameButton = ButtonFactory.createButton("Exit Game", buttonX, BACK_BUTTON_Y + 120, BUTTON_WIDTH, BUTTON_HEIGHT, e -> closeGame());

        // Assuming the background label is the one with null layout and where buttons are added
        JLabel backgroundLabel = (JLabel) mainMenu.getComponent(0);
        backgroundLabel.add(mainMenuButton);
        backgroundLabel.add(gameViewButton);
        backgroundLabel.add(helpMenuButton);
        backgroundLabel.add(aboutMeButton);
        backgroundLabel.add(achieveButton);
        backgroundLabel.add(exitGameButton);
    }

    private void setupMainWindow() {
        JFrame mainWindow = mnClient.getMainWindow().getFrame();
        mainWindow.setSize(getStartMenuWidth(), getStartMenuHeight() + 100);
        mainWindow.setLocationRelativeTo(null);
    }


    private void updateTitleLabel(String panelName) {
        JLabel labelToUpdate;

        switch (panelName) {
            case "Main Menu":
                labelToUpdate = mainMenuLabel;
                break;
            case "Play":
                labelToUpdate = gameViewLabel;
                break;
            case "Help Menu":
                labelToUpdate = helpMenuLabel;
                break;
            case "About Me":
                labelToUpdate = aboutMeLabel;
                break;
            case "Achievements":
                labelToUpdate = achieveLabel;
                break;
            default:
                return; // Unknown panel name
        }

        labelToUpdate.setText("TESTING " + panelName);
        labelToUpdate.setForeground(Color.RED);
        labelToUpdate.setFont(new Font("Arial", Font.BOLD, 24));
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, panelName);
        updateTitleLabel(panelName);
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
