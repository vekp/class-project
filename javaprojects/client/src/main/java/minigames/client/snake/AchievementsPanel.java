package minigames.client.snake;

import javax.swing.*;

/**
 * The AchievementsPanel class represents the UI panel that displays the player's achievements in
 * the Snake game.
 */
public class AchievementsPanel extends JPanel {

    private final MainMenuPanel.PanelSwitcher panelSwitcher;
    private final BackgroundContainer backgroundContainer;

    /**
     * Constructs the AchievementsPanel.
     *
     * @param panelSwitcher A functional interface used to switch between different panels in the
     *                     game UI.
     */
    public AchievementsPanel(MainMenuPanel.PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;

        setLayout(null);
        backgroundContainer = new BackgroundContainer();
        add(backgroundContainer);
        setupReturnButton();
    }

    /**
     * Sets up the return button that takes the player back to the main menu.
     */
    private void setupReturnButton() {
        // Calculate the X position of the return button to center it
        int buttonX = (MultimediaManager.getPhoneBackground()
                                        .getImageResourceWidth() - GameConstants.BUTTON_WIDTH) / 2;

        // Use the UIHelper to create the return button and add it to the background container
        ButtonFactory.setupReturnButton(panelSwitcher, backgroundContainer, buttonX,
                                        GameConstants.RETURN_BUTTON_Y,
                                        GameConstants.RETURN_BUTTON_TEXT
                                       );
    }
}
