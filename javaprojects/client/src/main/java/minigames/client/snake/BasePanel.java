package minigames.client.snake;

import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import java.awt.*;
//ImageResource phoneBackground = MultimediaManager.getPhoneBackground();
//        ImageIcon image = phoneBackground.getImage();
//        int width = phoneBackground.getWidth();
//        int height = phoneBackground.getHeight();

/**
 * The BasePanel class is the main container for various sub-panels that make up the Snake game UI.
 * It provides functionality to manage and switch between different views or screens within the game.
 */
public class BasePanel extends JPanel {

    // Constants for button dimensions and positions
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int START_BUTTON_Y = 150;
    private final int BUTTON_GAP = 10;

    // Main panel with a CardLayout to switch between different views
    private final JPanel mainPanel = new JPanel(new CardLayout());
    private final JPanel containerPanel = new JPanel(new BorderLayout());

    // Sub-panels for different game views
    private final MainMenuPanel mainMenu = new MainMenuPanel();
    private final JPanel gameView = new JPanel();
    private final JPanel helpMenu = new JPanel();
    private final JPanel aboutMe = new JPanel();
    private final JPanel achieve = new JPanel();

    // Labels for the respective panels (used for testing)
    private final JLabel gameViewLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel helpMenuLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel aboutMeLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel achieveLabel = new JLabel("", SwingConstants.CENTER);

    // Client to interact with the game server
    private final MinigameNetworkClient mnClient;
    // Action to be performed when the game is closed
    private final Runnable closeGameAction;

    /**
     * Constructs a BasePanel with a specified network client and close game action.
     *
     * @param mnClient        The network client used for server communication.
     * @param closeGameAction The action to be executed when the game is closed.
     */
    public BasePanel(MinigameNetworkClient mnClient, Runnable closeGameAction) {
        this.mnClient = mnClient;
        this.closeGameAction = closeGameAction;

        setupMainPanels();
        setupBackground();
        setupViewLabels();
        setupButtons();
        setupMainWindow();
        setupUI();
    }

    /**
     * Sets up the main UI components for the game.
     * This includes adding the main panel to the center of the container and
     * setting up a bottom panel with the "Main Menu" button.
     */
    private void setupUI() {
        // Add the main panel to the center of the container
        containerPanel.add(mainPanel, BorderLayout.CENTER);

        // Set up a bottom panel with a centered button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton mainMenuButton = ButtonFactory.createButton("Main Menu", 0, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Main Menu"));
        bottomPanel.add(mainMenuButton);

        // Add the bottom panel to the south of the container
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Returns the main container panel.
     *
     * @return JPanel representing the main container.
     */
    public JPanel getContainerPanel() {
        return containerPanel;
    }

    /**
     * Initializes the main panels, assigning names for each sub-panel.
     */
    private void setupMainPanels() {
        mainPanel.add(mainMenu, "Main Menu");
        mainPanel.add(gameView, "Play");
        mainPanel.add(helpMenu, "Help Menu");
        mainPanel.add(aboutMe, "About Me");
        mainPanel.add(achieve, "Achievements");
    }

    /**
     * Configures the background for the main menu panel.
     */
    private void setupBackground() {
        ImageIcon backgroundIcon = MultimediaManager.getPhoneBackground().getImage();
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        backgroundLabel.setLayout(null); // For adding components directly

        mainMenu.add(backgroundLabel);
        mainMenu.setBackground(Color.BLACK);
    }

    /**
     * Displays a specified panel by its name.
     *
     * @param panelName The name of the panel to be displayed.
     */
    public void showPanel(String panelName) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, panelName);
        updateTitleLabel(panelName);
    }

    /**
     * Updates the title label of the currently displayed panel.
     *
     * @param panelName The name of the panel currently displayed.
     */
    private void updateTitleLabel(String panelName) {
        JLabel labelToUpdate;
        switch (panelName) {
            case "Play" -> labelToUpdate = gameViewLabel;
            case "Help Menu" -> labelToUpdate = helpMenuLabel;
            case "About Me" -> labelToUpdate = aboutMeLabel;
            case "Achievements" -> labelToUpdate = achieveLabel;
            default -> {
            }
        }
    }

    /**
     * Sets up labels for each view or screen in the game.
     */
    private void setupViewLabels() {
        mainMenu.add(new JLabel("Main Menu", SwingConstants.CENTER));
        gameView.add(new JLabel("Game View", SwingConstants.CENTER));
        helpMenu.add(new JLabel("Help Menu", SwingConstants.CENTER));
        aboutMe.add(new JLabel("About Me", SwingConstants.CENTER));
        achieve.add(new JLabel("Achievements", SwingConstants.CENTER));

        // For testing
        gameView.add(gameViewLabel);
        helpMenu.add(helpMenuLabel);
        aboutMe.add(aboutMeLabel);
        achieve.add(achieveLabel);
    }

    /**
     * Sets up buttons for each view, with their respective actions.
     */
    private void setupButtons() {
        int buttonX = (MultimediaManager.getPhoneBackground().getWidth() - BUTTON_WIDTH) / 2;

//        JButton mainMenuButton = ButtonFactory.createButton("Main Menu", buttonX, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Main Menu"));
        JButton gameViewButton = ButtonFactory.createButton("Play", buttonX, START_BUTTON_Y + BUTTON_HEIGHT + BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Play"));
        JButton helpMenuButton = ButtonFactory.createButton("Help Menu", buttonX, gameViewButton.getY() + BUTTON_HEIGHT + BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Help Menu"));
        JButton aboutMeButton = ButtonFactory.createButton("About Me", buttonX, helpMenuButton.getY() + BUTTON_HEIGHT + BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("About Me"));
        JButton achieveButton = ButtonFactory.createButton("Achievements", buttonX, aboutMeButton.getY() + BUTTON_HEIGHT + BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, e -> showPanel("Achievements"));
        JButton exitGameButton = ButtonFactory.createButton("Exit Game", buttonX, achieveButton.getY() + BUTTON_HEIGHT + BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, e -> closeGameAction.run());

        JLabel backgroundLabel = (JLabel) mainMenu.getComponent(0);
//        backgroundLabel.add(mainMenuButton);
        backgroundLabel.add(gameViewButton);
        backgroundLabel.add(helpMenuButton);
        backgroundLabel.add(aboutMeButton);
        backgroundLabel.add(achieveButton);
        backgroundLabel.add(exitGameButton);
    }

    /**
     * Configures properties for the main game window.
     */
    private void setupMainWindow() {
        JFrame mainWindow = mnClient.getMainWindow().getFrame();

        ImageResource phoneBackground = MultimediaManager.getPhoneBackground();
        mainWindow.setSize(phoneBackground.getWidth(), phoneBackground.getHeight() + 100);
        mainWindow.setLocationRelativeTo(null);
    }

}
