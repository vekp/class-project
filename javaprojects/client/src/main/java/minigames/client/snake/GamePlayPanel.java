package minigames.client.snake;

import javax.print.attribute.standard.Media;
import javax.swing.*;

/**
 * The GameViewPanel class represents the main view where the Snake game is played.
 * It provides the primary game interface for the user.
 * This class makes use of the BackgroundContainer for setting the background and logo,
 * and the ButtonFactory for creating a consistent look for buttons.
 */
public class GamePlayPanel extends JPanel {

    // Represents the main container for the game view
    private final BackgroundContainer backgroundContainer;

    // Interface to switch between different panels
    private final MainMenuPanel.PanelSwitcher panelSwitcher;

    /**
     * Constructs the GameViewPanel and initializes the game interface.
     *
     * @param panelSwitcher An implementation of the PanelSwitcher interface to switch panels.
     */
    public GamePlayPanel(MainMenuPanel.PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
        setLayout(null);
        backgroundContainer = new BackgroundContainer();
        add(backgroundContainer);
        initializeGameInterface();
    }

    /**
     * Initializes the primary game interface components.
     */
    private void initializeGameInterface() {
        // TODO: Implement the game interface components.
        setupExitButton();
    }

    /**
     * Sets up the exit button, which allows the user to return to the main menu.
     * This uses the UIHelper class to create a button with a consistent look and feel.
     */
    private void setupExitButton() {
        UIHelper.setupReturnButton(panelSwitcher, backgroundContainer, 0, 0, GameConstants.EXIT_GAME);
    }
}
