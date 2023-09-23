package minigames.client.snake;

import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import java.awt.*;

/**
 * The BasePanel class serves as the primary user interface panel for the Snake game.
 * It contains different game panels such as the main menu, game view, information panels, and achievements panel.
 */
public class BasePanel extends JPanel implements MainMenuPanel.PanelSwitcher {
    // Panels
    private final JPanel mainPanel = new JPanel(new CardLayout());
    private final JPanel containerPanel = new JPanel(new BorderLayout());

    // Sub-panel instances
    private final MainMenuPanel mainMenuPanel;
    private final GamePlayPanel gameViewPanel;
    private final InformationPanel gameRules;
    private final InformationPanel aboutMePanel;
    private final AchievementsPanel achievementsPanel;

    /**
     * Initializes the BasePanel with various sub panels and configures the UI.
     *
     * @param mnClient        The MinigameNetworkClient for communication with the game server.
     * @param closeGameAction A Runnable action to close the game.
     */
    public BasePanel(MinigameNetworkClient mnClient, Runnable closeGameAction) {
        GameLogic gameLogic = new GameLogic();

        // Initialize sub-panels
        mainMenuPanel = new MainMenuPanel(closeGameAction, this);
        gameViewPanel = new GamePlayPanel(this, gameLogic);
        gameRules = new InformationPanel(this, GameConstants.GAME_RULES_TITLE, GameConstants.GAME_RULES_MESSAGES);
        aboutMePanel = new InformationPanel(this, GameConstants.ABOUT_ME_TITLE, GameConstants.ABOUT_ME_MESSAGES);
        achievementsPanel = new AchievementsPanel(this);

        // Initialize UI
        initUI();
        configureMainWindow(mnClient);
        MultimediaManager.playBackgroundSound(MusicChoice.MENU_MUSIC);
    }

    /**
     * Initializes the user interface by adding sub panels to the main panel and configuring the container panel.
     */
    private void initUI() {
        mainPanel.add(mainMenuPanel, GameConstants.MAIN_MENU_PANEL);
        mainPanel.add(gameViewPanel, GameConstants.PLAY_PANEL);
        mainPanel.add(gameRules, GameConstants.HELP_MENU_PANEL);
        mainPanel.add(aboutMePanel, GameConstants.ABOUT_ME_PANEL);
        mainPanel.add(achievementsPanel, GameConstants.ACHIEVEMENTS_PANEL);

        containerPanel.add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Retrieves the container panel that holds the game UI.
     *
     * @return The container panel.
     */
    public JPanel getContainerPanel() {
        return containerPanel;
    }

    /**
     * Switches to a specified panel by name using CardLayout.
     *
     * @param panelName The name of the panel to switch to.
     */
    @Override
    public void switchToPanel(String panelName) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, panelName);

        if (GameConstants.PLAY_PANEL.equals(panelName)) {
            gameViewPanel.startGame();
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Configures the main window's properties and size.
     *
     * @param mnClient The MinigameNetworkClient used to access the main window.
     */
    private void configureMainWindow(MinigameNetworkClient mnClient) {
        JFrame mainWindow = mnClient.getMainWindow().getFrame();
        Dimension backgroundDimension = getBackgroundDimension();
        mainWindow.setSize(backgroundDimension);
        mainWindow.setVisible(true);
    }

    /**
     * Retrieves the dimension of the background image.
     *
     * @return The dimension of the background image.
     */
    public Dimension getBackgroundDimension() {
        ImageIcon backgroundIcon = MultimediaManager.getPhoneBackground().getImageResource();
        return new Dimension(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
    }
}
